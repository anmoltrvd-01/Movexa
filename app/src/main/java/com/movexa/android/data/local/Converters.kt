package com.movexa.android.data.local

import androidx.room.TypeConverter
import com.movexa.android.domain.model.ActivityType

class Converters {
    @TypeConverter
    fun fromActivityType(type: ActivityType): String = type.name

    @TypeConverter
    fun toActivityType(name: String): ActivityType = ActivityType.valueOf(name)
}