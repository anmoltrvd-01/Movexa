package com.movexa.android.data.repository

import com.movexa.android.data.local.dao.WorkoutDao
import com.movexa.android.data.local.entity.WorkoutEntity
import com.movexa.android.domain.model.ActivityType
import kotlinx.coroutines.flow.Flow
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WorkoutRepository @Inject constructor(
    private val workoutDao: WorkoutDao
) {
    fun getAllWorkouts(): Flow<List<WorkoutEntity>> =
        workoutDao.getAllWorkouts()

    fun getWorkoutsByType(type: ActivityType): Flow<List<WorkoutEntity>> =
        workoutDao.getWorkoutsByType(type.name)

    fun getThisWeekWorkouts(): Flow<List<WorkoutEntity>> =
        workoutDao.getWorkoutsSince(getStartOfWeek())

    fun getThisWeekDistance(): Flow<Float?> =
        workoutDao.getTotalDistanceSince(getStartOfWeek())

    fun getThisWeekCount(): Flow<Int> =
        workoutDao.getWorkoutCountSince(getStartOfWeek())

    suspend fun saveWorkout(workout: WorkoutEntity): Long =
        workoutDao.insertWorkout(workout)

    suspend fun deleteWorkout(workout: WorkoutEntity) =
        workoutDao.deleteWorkout(workout)

    private fun getStartOfWeek(): Long {
        val cal = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_WEEK, firstDayOfWeek)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return cal.timeInMillis
    }
}