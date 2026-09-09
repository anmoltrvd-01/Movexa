package com.movexa.android.presentation.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.movexa.android.data.local.entity.WorkoutEntity
import com.movexa.android.data.repository.WorkoutRepository
import com.movexa.android.domain.model.ActivityType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class HistoryFilter { ALL, RUN, CYCLE, WALK, GYM, SWIM }

data class HistoryUiState(
    val workouts: List<WorkoutEntity> = emptyList(),
    val weeklyDistanceKm: Float = 0f,
    val weeklyCount: Int = 0,
    val selectedFilter: HistoryFilter = HistoryFilter.ALL
)

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val repository: WorkoutRepository
) : ViewModel() {

    private val _filter = MutableStateFlow(HistoryFilter.ALL)
    
    val state: StateFlow<HistoryUiState> = combine(
        _filter.flatMapLatest { filter ->
            if (filter == HistoryFilter.ALL) repository.getAllWorkouts()
            else {
                val activityType = when (filter) {
                    HistoryFilter.RUN   -> ActivityType.RUN
                    HistoryFilter.CYCLE -> ActivityType.CYCLE
                    HistoryFilter.WALK  -> ActivityType.WALK
                    HistoryFilter.GYM   -> ActivityType.GYM
                    HistoryFilter.SWIM  -> ActivityType.SWIM
                    else -> ActivityType.RUN
                }
                repository.getWorkoutsByType(activityType)
            }
        },
        repository.getThisWeekDistance().map { it ?: 0f },
        repository.getThisWeekCount(),
        _filter
    ) { workouts, weekDist, weekCount, filter ->
        HistoryUiState(
            workouts = workouts,
            weeklyDistanceKm = weekDist / 1000f,
            weeklyCount = weekCount,
            selectedFilter = filter
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), HistoryUiState())

    fun setFilter(filter: HistoryFilter) { _filter.value = filter }

    fun deleteWorkout(workout: WorkoutEntity) {
        viewModelScope.launch { repository.deleteWorkout(workout) }
    }
}