package com.example.learningdashboard.ui.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.learningdashboard.domain.model.Course
import com.example.learningdashboard.domain.repository.CourseRepository
import com.example.learningdashboard.ui.navigation.CourseDetails
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface CourseDetailsUiState {
    data object Loading : CourseDetailsUiState
    data object NotFound : CourseDetailsUiState
    data class Success(val course: Course) : CourseDetailsUiState
}

class CourseDetailsViewModel(
    savedStateHandle: SavedStateHandle,
    private val courseRepository: CourseRepository,
) : ViewModel() {

    private val courseId: Int = savedStateHandle.toRoute<CourseDetails>().courseId

    val uiState: StateFlow<CourseDetailsUiState> = courseRepository.observeCourse(courseId)
        .map { course -> course?.let { CourseDetailsUiState.Success(it) } ?: CourseDetailsUiState.NotFound }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), CourseDetailsUiState.Loading)

    fun markLessonCompleted(lessonId: Int) {
        viewModelScope.launch { courseRepository.markLessonCompleted(lessonId) }
    }
}
