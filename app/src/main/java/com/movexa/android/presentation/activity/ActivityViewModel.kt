package com.movexa.android.presentation.activity

import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.movexa.android.data.repository.LocationRepository
import com.movexa.android.domain.model.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import com.movexa.android.data.local.entity.WorkoutEntity
import com.movexa.android.data.repository.WorkoutRepository

@HiltViewModel
class ActivityViewModel @Inject constructor(
    private val locationRepo: LocationRepository,
    private val workoutRepo: WorkoutRepository
) : ViewModel() {

    private val _session = MutableStateFlow(ActivitySession())
    val session = _session.asStateFlow()

    private var timerJob: Job? = null
    private var locationJob: Job? = null
    private var lastLocation: Location? = null

    init {
        startLocationTracking()
    }

    fun selectType(type: ActivityType) {
        if (_session.value.state == TrackingState.IDLE)
            _session.update { it.copy(type = type) }
    }

    fun searchDestination(query: String, context: android.content.Context) {
        if (query.isBlank()) return
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val geocoder = android.location.Geocoder(context)
                val addresses = geocoder.getFromLocationName(query, 1)
                if (!addresses.isNullOrEmpty()) {
                    val address = addresses[0]
                    val loc = Location("search").apply {
                        latitude = address.latitude
                        longitude = address.longitude
                    }
                    withContext(Dispatchers.Main) {
                        setDestination(loc)
                        // If IDLE, auto-follow to the searched location to show it on map
                        if (_session.value.state == TrackingState.IDLE) {
                            _session.update { it.copy(currentLocation = loc, isFollowMode = true) }
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun startActivity() {
        val currentLoc = lastLocation
        _session.update { 
            it.copy(
                state = TrackingState.ACTIVE,
                startPoint = currentLoc,
                routePoints = if (currentLoc != null) listOf(currentLoc) else emptyList()
            ) 
        }
        startTimer()
    }

    fun setDestination(location: Location) {
        if (_session.value.state == TrackingState.IDLE || _session.value.state == TrackingState.ACTIVE) {
            _session.update { it.copy(destination = location) }
        }
    }

    fun clearDestination() {
        _session.update { it.copy(destination = null) }
    }

    fun toggleFollowMode() {
        _session.update { it.copy(isFollowMode = !it.isFollowMode) }
    }

    fun cycleMapType() {
        _session.update {
            val next = when (it.mapType) {
                ActivitySession.MapType.NORMAL -> ActivitySession.MapType.SATELLITE
                ActivitySession.MapType.SATELLITE -> ActivitySession.MapType.TERRAIN
                ActivitySession.MapType.TERRAIN -> ActivitySession.MapType.NORMAL
            }
            it.copy(mapType = next)
        }
    }

    fun pauseActivity() {
        timerJob?.cancel()
        _session.update { it.copy(state = TrackingState.PAUSED) }
    }

    fun resumeActivity() {
        _session.update { it.copy(state = TrackingState.ACTIVE) }
        startTimer()
    }

    fun stopActivity() {
        timerJob?.cancel()
        val current = _session.value
        _session.update { it.copy(state = TrackingState.FINISHED) }

        viewModelScope.launch {
            workoutRepo.saveWorkout(
                WorkoutEntity(
                    type = current.type,
                    distanceMeters = current.distanceMeters,
                    durationSeconds = current.elapsedSeconds,
                    calories = current.calories,
                    avgPaceSecPerKm = current.currentPaceSecPerKm
                )
            )
        }
    }

    fun resetActivity() {
        timerJob?.cancel()
        locationJob?.cancel()
        lastLocation = null
        _session.value = ActivitySession()
    }

    private fun startTimer() {
        timerJob = viewModelScope.launch {
            while (isActive) {
                delay(1000L)
                _session.update { it.copy(elapsedSeconds = it.elapsedSeconds + 1) }
            }
        }
    }

    private fun startLocationTracking() {
        locationJob = viewModelScope.launch {
            locationRepo.locationUpdates()
                .catch { /* permission not granted — handle in UI */ }
                .collect { location -> processLocation(location) }
        }
    }

    private fun processLocation(location: Location) {
        val current = _session.value
        val isTracking = current.state == TrackingState.ACTIVE
        
        val prev = lastLocation
        // Accurate distance calculation: ignore small jumps and poor accuracy
        val added = if (isTracking && location.accuracy < 25f && prev != null) {
            val dist = prev.distanceTo(location)
            if (dist > 1.0f) dist else 0f // Filter jitter
        } else 0f

        val newDistance = current.distanceMeters + added
        
        // Accurate Pace calculation (seconds per km)
        val pace = if (isTracking && newDistance > 10 && current.elapsedSeconds > 5) {
            (current.elapsedSeconds.toFloat() / (newDistance / 1000f)).toInt()
        } else current.currentPaceSecPerKm

        // Use location speed if available and accurate
        val speedMps = if (location.hasSpeed() && location.speedAccuracyMetersPerSecond < 2.0f) {
            location.speed
        } else if (prev != null && isTracking) {
            // fallback to manual calc
            val timeDiff = (location.time - prev.time) / 1000f
            if (timeDiff > 0) added / timeDiff else 0f
        } else 0f
        
        // Calorie calculation based on MET values
        val metValue = when(current.type) {
            ActivityType.RUN -> 9.8f
            ActivityType.CYCLE -> 8.0f
            ActivityType.WALK -> 3.5f
            ActivityType.SWIM -> 7.0f
            ActivityType.GYM -> 5.0f
            ActivityType.HIKE -> 6.0f
        }
        val weightKg = 70f // Default, could be from user profile
        val calories = if (isTracking) {
            ((current.elapsedSeconds / 3600f) * metValue * weightKg).toInt()
        } else current.calories

        val newBearing = if (location.hasBearing()) location.bearing else current.bearing

        _session.update {
            it.copy(
                currentLocation = location,
                bearing = newBearing,
                distanceMeters = newDistance,
                currentPaceSecPerKm = pace,
                currentSpeedMps = speedMps,
                calories = calories,
                // Only add to route points if tracking and meaningful movement occurred
                routePoints = if (isTracking && added > 0.5f) it.routePoints + location else it.routePoints
            )
        }
        lastLocation = location
    }
}
