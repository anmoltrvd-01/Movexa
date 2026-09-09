package com.movexa.android.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.movexa.android.domain.model.*
import com.movexa.android.domain.repository.AuthRepository
import com.movexa.android.domain.repository.HealthRepository
import com.movexa.android.data.repository.WorkoutRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import java.text.SimpleDateFormat
import java.util.*

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val healthRepository: HealthRepository,
    private val authRepository: AuthRepository,
    private val workoutRepository: WorkoutRepository
) : ViewModel() {

    val userName: StateFlow<String> = authRepository.getSession()
        .map { it?.name ?: "User" }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = "User"
        )

    val stats: StateFlow<TodayStats> = healthRepository.getTodayStats()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = TodayStats(0, 10000, 0, 0f, 0)
        )

    val hasPermissions: StateFlow<Boolean> = healthRepository.hasAllPermissions()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = true
        )

    val weekly: StateFlow<List<DayActivity>> = healthRepository.getWeeklyActivity()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val recent: StateFlow<List<ActivitySummary>> = workoutRepository.getAllWorkouts()
        .map { workouts ->
            workouts.take(5).map { entity ->
                ActivitySummary(
                    id = entity.id,
                    type = entity.type,
                    distanceKm = entity.distanceMeters / 1000f,
                    durationMinutes = (entity.durationSeconds / 60).toInt(),
                    pace = entity.avgPaceSecPerKm.toPaceString(),
                    dateLabel = entity.timestamp.toDateLabel()
                )
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private fun Int.toPaceString(): String {
        val mins = this / 60
        val secs = this % 60
        return "%d:%02d /km".format(mins, secs)
    }

    private fun Long.toDateLabel(): String {
        val now = Calendar.getInstance()
        val date = Calendar.getInstance().apply { timeInMillis = this@toDateLabel }
        
        return when {
            now.get(Calendar.DATE) == date.get(Calendar.DATE) && now.get(Calendar.MONTH) == date.get(Calendar.MONTH) && now.get(Calendar.YEAR) == date.get(Calendar.YEAR) -> "Today"
            else -> {
                now.add(Calendar.DATE, -1)
                if (now.get(Calendar.DATE) == date.get(Calendar.DATE) && now.get(Calendar.MONTH) == date.get(Calendar.MONTH) && now.get(Calendar.YEAR) == date.get(Calendar.YEAR)) {
                    "Yesterday"
                } else {
                    SimpleDateFormat("EEE", Locale.getDefault()).format(date.time)
                }
            }
        }
    }
}