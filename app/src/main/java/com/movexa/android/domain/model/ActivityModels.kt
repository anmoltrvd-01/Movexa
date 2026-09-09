package com.movexa.android.domain.model

import android.location.Location

enum class TrackingState { IDLE, ACTIVE, PAUSED, FINISHED }

data class ActivitySession(
    val type: ActivityType = ActivityType.RUN,
    val state: TrackingState = TrackingState.IDLE,
    val elapsedSeconds: Long = 0L,
    val distanceMeters: Float = 0f,
    val currentPaceSecPerKm: Int = 0,
    val currentSpeedMps: Float = 0f,
    val calories: Int = 0,
    val routePoints: List<Location> = emptyList(),
    val destination: Location? = null,
    val startPoint: Location? = null,
    val currentLocation: Location? = null,
    val bearing: Float = 0f,
    val isFollowMode: Boolean = true,
    val mapType: MapType = MapType.NORMAL
) {
    enum class MapType { NORMAL, SATELLITE, TERRAIN }

    val distanceKm get() = distanceMeters / 1000f
    
    val currentSpeedKmh get() = currentSpeedMps * 3.6f

    val distanceToDestination: Float?
        get() {
            val last = currentLocation ?: routePoints.lastOrNull() ?: startPoint ?: return null
            return destination?.distanceTo(last)
        }

    val paceFormatted
        get() = if (currentPaceSecPerKm <= 0) "--:--"
        else "%d:%02d".format(currentPaceSecPerKm / 60, currentPaceSecPerKm % 60)
    val elapsedFormatted
        get() = "%02d:%02d:%02d".format(
            elapsedSeconds / 3600,
            (elapsedSeconds % 3600) / 60,
            elapsedSeconds % 60
        )
}