package com.movexa.android.domain.model

data class TodayStats(
    val steps: Int = 0,
    val stepsGoal: Int = 10_000,
    val caloriesBurned: Int = 0,
    val distanceKm: Float = 0f,
    val activeMinutes: Int = 0
) {
    val stepsProgress get() = if (stepsGoal == 0) 0f
    else (steps.toFloat() / stepsGoal.toFloat()).coerceIn(0f, 1f)
}

data class ActivitySummary(
    val id: Long,
    val type: ActivityType,
    val distanceKm: Float,
    val durationMinutes: Int,
    val pace: String,
    val dateLabel: String
)

enum class ActivityType(val label: String, val emoji: String) {
    RUN("Run", "🏃"), CYCLE("Cycle", "🚴"), WALK("Walk", "🚶"),
    SWIM("Swim", "🏊"), GYM("Gym", "🏋️"), HIKE("Hike", "🥾")
}

data class DayActivity(
    val day: String,
    val distanceKm: Float,
    val steps: Long = 0L,
    val calories: Int = 0,
    val activeMinutes: Int = 0,
    val isToday: Boolean = false
)