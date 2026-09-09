package com.movexa.android.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.movexa.android.domain.model.ActivityType

@Entity(tableName = "workouts")
data class WorkoutEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val type: ActivityType,
    val distanceMeters: Float,
    val durationSeconds: Long,
    val calories: Int,
    val avgPaceSecPerKm: Int,
    val timestamp: Long = System.currentTimeMillis()
)