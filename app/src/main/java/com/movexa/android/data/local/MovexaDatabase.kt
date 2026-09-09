package com.movexa.android.data.local

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import com.movexa.android.data.local.dao.WorkoutDao
import com.movexa.android.data.local.entity.WorkoutEntity
import androidx.room.TypeConverters

@Database(
    entities = [WorkoutEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class MovexaDatabase : RoomDatabase() {
    abstract fun workoutDao(): WorkoutDao

    companion object {
        const val DATABASE_NAME = "movexa_db"
    }
}