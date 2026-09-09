package com.movexa.android.data.repository

import com.movexa.android.data.health.HealthConnectManager
import com.movexa.android.domain.model.DayActivity
import com.movexa.android.domain.model.TodayStats
import com.movexa.android.domain.repository.HealthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.TextStyle
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HealthRepositoryImpl @Inject constructor(
    private val healthConnectManager: HealthConnectManager
) : HealthRepository {

    override fun getTodayStats(): Flow<TodayStats> = flow {
        while (true) {
            emit(TodayStats()) // Start with defaults

            try {
                if (healthConnectManager.hasAllPermissions()) {
                    val zone = ZoneId.systemDefault()
                    val start = LocalDate.now().atStartOfDay(zone).toInstant()
                    val end = Instant.now()

                    val steps = healthConnectManager.readSteps(start, end)
                    val calories = healthConnectManager.readCalories(start, end)
                    val distance = healthConnectManager.readDistance(start, end)
                    val activeMinutes = healthConnectManager.readActiveMinutes(start, end)

                    emit(
                        TodayStats(
                            steps = steps.toInt(),
                            stepsGoal = 10_000,
                            caloriesBurned = calories,
                            distanceKm = distance,
                            activeMinutes = activeMinutes
                        )
                    )
                }
            } catch (e: Exception) {
                // Keep default stats
            }
            kotlinx.coroutines.delay(10000) // Refresh every 10 seconds
        }
    }

    override fun getWeeklyActivity(): Flow<List<DayActivity>> = flow {
        // Emit empty list immediately so UI doesn't wait
        emit(emptyList())

        try {
            if (!healthConnectManager.hasAllPermissions()) return@flow

            val today = LocalDate.now()
            val weeklyData = healthConnectManager.readWeeklyData()

            val days = weeklyData.mapIndexed { index, data ->
                val daysAgo = 6 - index
                val date = today.minusDays(daysAgo.toLong())
                val dayLabel = date.dayOfWeek
                    .getDisplayName(TextStyle.SHORT, Locale.getDefault())
                    .take(1)

                DayActivity(
                    day = dayLabel,
                    distanceKm = data.distanceKm,
                    steps = data.steps,
                    calories = data.calories,
                    activeMinutes = data.activeMinutes,
                    isToday = daysAgo == 0
                )
            }
            emit(days)
        } catch (e: Exception) {
            // permissions not granted — empty list already emitted
        }
    }

    override fun hasAllPermissions(): Flow<Boolean> = flow {
        while (true) {
            emit(
                try {
                    healthConnectManager.hasAllPermissions()
                } catch (e: Exception) {
                    false
                }
            )
            kotlinx.coroutines.delay(5000) // Re-check every 5 seconds while active
        }
    }
}