package com.movexa.android.data.health

import android.content.Context
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.DistanceRecord
import androidx.health.connect.client.records.ExerciseSessionRecord
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.records.TotalCaloriesBurnedRecord
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.TextStyle
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

data class DailyHealthData(
    val date: LocalDate,
    val steps: Long,
    val calories: Int,
    val distanceKm: Float,
    val activeMinutes: Int
)

@Singleton
class HealthConnectManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    val permissions = setOf(
        HealthPermission.getReadPermission(StepsRecord::class),
        HealthPermission.getReadPermission(DistanceRecord::class),
        HealthPermission.getReadPermission(TotalCaloriesBurnedRecord::class),
        HealthPermission.getReadPermission(ExerciseSessionRecord::class),
        HealthPermission.getReadPermission(androidx.health.connect.client.records.HeartRateRecord::class),
        HealthPermission.getReadPermission(androidx.health.connect.client.records.SleepSessionRecord::class)
    )

    val isAvailable: Boolean
        get() = HealthConnectClient.getSdkStatus(context) == HealthConnectClient.SDK_AVAILABLE

    private val client by lazy {
        if (isAvailable) HealthConnectClient.getOrCreate(context) else null
    }

    suspend fun hasAllPermissions(): Boolean =
        client?.permissionController
            ?.getGrantedPermissions()
            ?.containsAll(permissions) ?: false

    suspend fun readSteps(start: Instant, end: Instant): Long {
        return try {
            client?.readRecords(
                ReadRecordsRequest(StepsRecord::class, TimeRangeFilter.between(start, end))
            )?.records?.sumOf { it.count } ?: 0L
        } catch (e: Exception) { 0L }
    }

    suspend fun readCalories(start: Instant, end: Instant): Int {
        return try {
            client?.readRecords(
                ReadRecordsRequest(TotalCaloriesBurnedRecord::class, TimeRangeFilter.between(start, end))
            )?.records?.sumOf { it.energy.inKilocalories }?.toInt() ?: 0
        } catch (e: Exception) { 0 }
    }

    suspend fun readDistance(start: Instant, end: Instant): Float {
        return try {
            client?.readRecords(
                ReadRecordsRequest(DistanceRecord::class, TimeRangeFilter.between(start, end))
            )?.records?.sumOf { it.distance.inKilometers }?.toFloat() ?: 0f
        } catch (e: Exception) { 0f }
    }

    suspend fun readActiveMinutes(start: Instant, end: Instant): Int {
        return try {
            client?.readRecords(
                ReadRecordsRequest(ExerciseSessionRecord::class, TimeRangeFilter.between(start, end))
            )?.records?.sumOf {
                (it.endTime.epochSecond - it.startTime.epochSecond) / 60
            }?.toInt() ?: 0
        } catch (e: Exception) { 0 }
    }

    suspend fun readDailyData(date: LocalDate): DailyHealthData {
        val zone = ZoneId.systemDefault()
        val start = date.atStartOfDay(zone).toInstant()
        val end = date.plusDays(1).atStartOfDay(zone).toInstant()
        return DailyHealthData(
            date = date,
            steps = readSteps(start, end),
            calories = readCalories(start, end),
            distanceKm = readDistance(start, end),
            activeMinutes = readActiveMinutes(start, end)
        )
    }

    suspend fun readWeeklyData(): List<DailyHealthData> {
        val today = LocalDate.now()
        return (6 downTo 0).map { daysAgo ->
            readDailyData(today.minusDays(daysAgo.toLong()))
        }
    }
}