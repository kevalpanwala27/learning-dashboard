package com.example.learningdashboard.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.learningdashboard.domain.model.Course
import com.example.learningdashboard.domain.model.ProgressCalculator
import com.example.learningdashboard.domain.repository.AuthRepository
import com.example.learningdashboard.domain.repository.CourseRepository
import com.example.learningdashboard.domain.repository.RefreshOutcome
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/** Aggregate across all courses, shown in the dashboard header. */
data class OverallProgress(val courseCount: Int, val completedLessons: Int, val totalLessons: Int) {
    val percent: Int get() = ProgressCalculator.percent(completedLessons, totalLessons)

    companion object {
        fun of(courses: List<Course>) = OverallProgress(
            courseCount = courses.size,
            completedLessons = courses.sumOf { it.completedCount },
            totalLessons = courses.sumOf { it.lessonCount },
        )
    }
}

sealed interface DashboardUiState {
    data object Loading : DashboardUiState
    data object Empty : DashboardUiState
    data class Error(val message: String, val isOffline: Boolean) : DashboardUiState
    data class Success(
        val courses: List<Course>,
        val overall: OverallProgress,
        /** Last refresh failed for lack of connectivity; the list is the cached copy. */
        val isOffline: Boolean,
        val isRefreshing: Boolean,
    ) : DashboardUiState
}

/** What the most recent refresh attempt did. Combined with the cache to produce the screen state. */
private sealed interface RefreshStatus {
    data object Idle : RefreshStatus
    data object Refreshing : RefreshStatus
    data object Offline : RefreshStatus
    data class Failed(val message: String) : RefreshStatus
}

class DashboardViewModel(
    private val courseRepository: CourseRepository,
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val refreshStatus = MutableStateFlow<RefreshStatus>(RefreshStatus.Refreshing)

    /** One-off messages (e.g. a failed refresh when cached data is still visible). */
    private val _messages = Channel<String>(Channel.BUFFERED)
    val messages = _messages.receiveAsFlow()

    val uiState: StateFlow<DashboardUiState> =
        combine(courseRepository.observeCourses(), refreshStatus) { courses, status ->
            if (courses.isEmpty()) {
                when (status) {
                    RefreshStatus.Refreshing -> DashboardUiState.Loading
                    RefreshStatus.Idle -> DashboardUiState.Empty
                    RefreshStatus.Offline -> DashboardUiState.Error(message = "", isOffline = true)
                    is RefreshStatus.Failed -> DashboardUiState.Error(status.message, isOffline = false)
                }
            } else {
                DashboardUiState.Success(
                    courses = courses,
                    overall = OverallProgress.of(courses),
                    isOffline = status == RefreshStatus.Offline,
                    isRefreshing = status == RefreshStatus.Refreshing,
                )
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), DashboardUiState.Loading)

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            refreshStatus.value = RefreshStatus.Refreshing
            val outcome = courseRepository.refreshCourses()
            refreshStatus.value = when (outcome) {
                RefreshOutcome.Success -> RefreshStatus.Idle
                RefreshOutcome.Offline -> RefreshStatus.Offline
                is RefreshOutcome.Failed -> {
                    _messages.send(outcome.message)
                    RefreshStatus.Failed(outcome.message)
                }
            }
        }
    }

    fun logout() = authRepository.logout()
}
