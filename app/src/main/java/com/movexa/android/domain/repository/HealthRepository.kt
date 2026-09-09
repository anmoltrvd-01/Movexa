package com.movexa.android.domain.repository

import com.movexa.android.domain.model.TodayStats
import com.movexa.android.domain.model.DayActivity
import kotlinx.coroutines.flow.Flow

interface HealthRepository {
    fun getTodayStats(): Flow<TodayStats>
    fun getWeeklyActivity(): Flow<List<DayActivity>>
    fun hasAllPermissions(): Flow<Boolean>
}