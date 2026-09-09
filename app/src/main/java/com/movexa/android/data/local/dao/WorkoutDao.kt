package com.movexa.android.data.local.dao

import androidx.room.*
import com.movexa.android.data.local.entity.WorkoutEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkout(workout: WorkoutEntity): Long

    @Query("SELECT * FROM workouts ORDER BY timestamp DESC")
    fun getAllWorkouts(): Flow<List<WorkoutEntity>>

    @Query("SELECT * FROM workouts WHERE type = :type ORDER BY timestamp DESC")
    fun getWorkoutsByType(type: String): Flow<List<WorkoutEntity>>

    @Query("SELECT * FROM workouts WHERE timestamp >= :fromTimestamp ORDER BY timestamp DESC")
    fun getWorkoutsSince(fromTimestamp: Long): Flow<List<WorkoutEntity>>

    @Query("SELECT SUM(distanceMeters) FROM workouts WHERE timestamp >= :fromTimestamp")
    fun getTotalDistanceSince(fromTimestamp: Long): Flow<Float?>

    @Query("SELECT COUNT(*) FROM workouts WHERE timestamp >= :fromTimestamp")
    fun getWorkoutCountSince(fromTimestamp: Long): Flow<Int>

    @Delete
    suspend fun deleteWorkout(workout: WorkoutEntity)
}