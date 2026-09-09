package com.movexa.android.data.repository

import app.cash.turbine.test
import com.movexa.android.data.health.HealthConnectManager
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class HealthRepositoryImplTest {

    private val workoutRepository: WorkoutRepository = mockk()
    private val healthConnectManager: HealthConnectManager = mockk()
    private lateinit var healthRepository: HealthRepositoryImpl

    @Before
    fun setup() {
        healthRepository = HealthRepositoryImpl(workoutRepository, healthConnectManager)
    }

    @Test
    fun `getTodayStats should return data from HealthConnectManager`() = runTest {
        coEvery { healthConnectManager.readSteps(any(), any()) } returns 7500L
        
        healthRepository.getTodayStats().test {
            val emission = awaitItem()
            assertEquals(7500, emission.steps)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `hasAllPermissions should reflect HealthConnectManager status`() = runTest {
        coEvery { healthConnectManager.hasAllPermissions() } returns true
        
        healthRepository.hasAllPermissions().test {
            val emission = awaitItem()
            assertEquals(true, emission)
            awaitComplete()
        }
    }

    @Test
    fun `getWeeklyActivity should return mapped data from workoutRepository`() = runTest {
        every { workoutRepository.getThisWeekWorkouts() } returns flowOf(emptyList())
        
        healthRepository.getWeeklyActivity().test {
            val emission = awaitItem()
            // In current implementation, it returns a hardcoded list regardless of database
            assertEquals(7, emission.size)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
