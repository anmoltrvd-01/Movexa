package com.movexa.android.presentation.home

import app.cash.turbine.test
import com.movexa.android.domain.model.DayActivity
import com.movexa.android.domain.model.TodayStats
import com.movexa.android.domain.model.User
import com.movexa.android.domain.repository.AuthRepository
import com.movexa.android.domain.repository.HealthRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private val healthRepository: HealthRepository = mockk()
    private val authRepository: AuthRepository = mockk()
    private lateinit var viewModel: HomeViewModel
    private val testDispatcher = UnconfinedTestDispatcher()

    private val statsFlow = MutableStateFlow(TodayStats(5000, 10000, 300, 3.5f, 45))
    private val weeklyFlow = MutableStateFlow(listOf(DayActivity("M", 5f)))
    private val permissionsFlow = MutableStateFlow(true)

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        
        every { healthRepository.getTodayStats() } returns statsFlow
        every { healthRepository.getWeeklyActivity() } returns weeklyFlow
        every { healthRepository.hasAllPermissions() } returns permissionsFlow
        every { authRepository.getSession() } returns flowOf(User("1", "test@test.com", "John Doe", "token"))
        
        viewModel = HomeViewModel(healthRepository, authRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun stats_state_flow_emits_correct_values() = runTest {
        viewModel.stats.test {
            val emission = awaitItem()
            assertEquals(5000, emission.steps)
            assertEquals(10000, emission.stepsGoal)
        }
    }

    @Test
    fun hasPermissions_state_flow_reflects_repository_status() = runTest {
        viewModel.hasPermissions.test {
            val emission = awaitItem()
            assertEquals(true, emission)
        }
    }

    @Test
    fun weekly_state_flow_emits_correct_data() = runTest {
        viewModel.weekly.test {
            val emission = awaitItem()
            assertEquals(1, emission.size)
            assertEquals("M", emission[0].day)
            assertEquals(5f, emission[0].distanceKm)
        }
    }

    @Test
    fun userName_state_flow_emits_correct_name() = runTest {
        viewModel.userName.test {
            assertEquals("John Doe", awaitItem())
        }
    }
}
