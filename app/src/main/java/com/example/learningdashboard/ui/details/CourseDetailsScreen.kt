package com.example.learningdashboard.ui.details

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.learningdashboard.R
import com.example.learningdashboard.di.AppViewModelFactory
import com.example.learningdashboard.domain.model.Course
import com.example.learningdashboard.domain.model.Lesson

@Composable
fun CourseDetailsScreen(
    courseId: Int,
    onBack: () -> Unit,
    viewModel: CourseDetailsViewModel = viewModel(factory = AppViewModelFactory),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    CourseDetailsContent(state = state, onBack = onBack, onMarkCompleted = viewModel::markLessonCompleted)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CourseDetailsContent(
    state: CourseDetailsUiState,
    onBack: () -> Unit,
    onMarkCompleted: (lessonId: Int) -> Unit,
) {
    val title = (state as? CourseDetailsUiState.Success)?.course?.title.orEmpty()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.details_back))
                    }
                },
            )
        },
    ) { padding ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(padding)) {
            when (state) {
                CourseDetailsUiState.Loading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                CourseDetailsUiState.NotFound -> Text(
                    stringResource(R.string.details_not_found),
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(32.dp),
                )
                is CourseDetailsUiState.Success -> CourseDetailsBody(state.course, onMarkCompleted)
            }
        }
    }
}

@Composable
private fun CourseDetailsBody(course: Course, onMarkCompleted: (Int) -> Unit) {
    LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 24.dp)) {
        item(key = "header") { ProgressHeader(course) }
        item(key = "lessons-title") {
            Text(
                stringResource(R.string.details_lessons_header),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            )
        }
        items(course.lessons, key = { it.id }) { lesson ->
            LessonRow(lesson = lesson, onMarkCompleted = { onMarkCompleted(lesson.id) })
            HorizontalDivider()
        }
    }
}

@Composable
private fun ProgressHeader(course: Course) {
    Column(modifier = Modifier.padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            LinearProgressIndicator(
                progress = { course.progressPercent / 100f },
                modifier = Modifier.weight(1f),
            )
            Spacer(Modifier.width(12.dp))
            Text(stringResource(R.string.course_progress, course.progressPercent), style = MaterialTheme.typography.titleMedium)
        }
        Spacer(Modifier.height(8.dp))
        Text(
            stringResource(R.string.details_progress_summary, course.completedCount, course.lessonCount),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun LessonRow(lesson: Lesson, onMarkCompleted: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = if (lesson.isCompleted) Icons.Filled.CheckCircle else Icons.Outlined.Circle,
            contentDescription = stringResource(if (lesson.isCompleted) R.string.lesson_completed else R.string.lesson_pending),
            tint = if (lesson.isCompleted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
        )
        Spacer(Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(lesson.title, style = MaterialTheme.typography.bodyLarge)
            Text(
                stringResource(if (lesson.isCompleted) R.string.lesson_completed else R.string.lesson_pending),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        if (!lesson.isCompleted) {
            TextButton(onClick = onMarkCompleted) { Text(stringResource(R.string.lesson_mark_done)) }
        }
    }
}
