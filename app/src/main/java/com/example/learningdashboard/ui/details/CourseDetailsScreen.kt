package com.example.learningdashboard.ui.details

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.School
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.learningdashboard.R
import com.example.learningdashboard.di.AppViewModelFactory
import com.example.learningdashboard.domain.model.Course
import com.example.learningdashboard.domain.model.Lesson
import com.example.learningdashboard.ui.components.AppCard
import com.example.learningdashboard.ui.components.OutlinePill
import com.example.learningdashboard.ui.components.PercentBadge
import com.example.learningdashboard.ui.components.ProgressGauge
import com.example.learningdashboard.ui.components.StateMessage
import com.example.learningdashboard.ui.components.ThinProgressBar

@Composable
fun CourseDetailsScreen(
    courseId: Int,
    onBack: () -> Unit,
    viewModel: CourseDetailsViewModel = viewModel(factory = AppViewModelFactory),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    CourseDetailsContent(state = state, onBack = onBack, onMarkCompleted = viewModel::markLessonCompleted)
}

@Composable
private fun CourseDetailsContent(
    state: CourseDetailsUiState,
    onBack: () -> Unit,
    onMarkCompleted: (lessonId: Int) -> Unit,
) {
    val course = (state as? CourseDetailsUiState.Success)?.course

    Scaffold(containerColor = MaterialTheme.colorScheme.background) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = padding.calculateBottomPadding()),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            item(key = "topbar") {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(top = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    OutlinePill(text = stringResource(R.string.details_back), onClick = onBack, icon = Icons.AutoMirrored.Filled.ArrowBack)
                    Spacer(Modifier.weight(1f))
                    if (course != null) PercentBadge(course.progressPercent)
                }
            }

            when (state) {
                CourseDetailsUiState.Loading -> item(key = "loading") {
                    Box(Modifier.fillMaxWidth().padding(vertical = 96.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }
                }

                CourseDetailsUiState.NotFound -> item(key = "not-found") {
                    StateMessage(icon = Icons.Outlined.School, title = stringResource(R.string.details_not_found), body = "")
                }

                is CourseDetailsUiState.Success -> {
                    item(key = "title") {
                        Column(modifier = Modifier.padding(top = 8.dp)) {
                            Text(state.course.title, style = MaterialTheme.typography.headlineLarge)
                            Spacer(Modifier.height(4.dp))
                            Text(
                                stringResource(R.string.course_by, state.course.instructor),
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                    item(key = "progress") { ProgressCard(state.course) }
                    item(key = "lessons-title") {
                        Text(
                            stringResource(R.string.details_lessons_header),
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(top = 12.dp, bottom = 2.dp),
                        )
                    }
                    itemsIndexed(state.course.lessons, key = { _, lesson -> lesson.id }) { index, lesson ->
                        LessonRow(index = index + 1, lesson = lesson, onMarkCompleted = { onMarkCompleted(lesson.id) })
                    }
                }
            }
        }
    }
}

@Composable
private fun ProgressCard(course: Course) {
    val remaining = course.lessonCount - course.completedCount
    AppCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    stringResource(R.string.details_progress_label).uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    stringResource(R.string.details_lessons_fraction, course.completedCount, course.lessonCount),
                    style = MaterialTheme.typography.headlineMedium,
                )
                Text(
                    stringResource(R.string.details_lessons_completed_label),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            ProgressGauge(progress = course.progressPercent / 100f, size = 104.dp, strokeWidth = 10.dp) {
                Text(stringResource(R.string.course_progress, course.progressPercent), style = MaterialTheme.typography.titleMedium)
            }
        }
        Spacer(Modifier.height(16.dp))
        ThinProgressBar(progress = course.progressPercent / 100f)
        Spacer(Modifier.height(10.dp))
        Text(
            text = if (remaining == 0) stringResource(R.string.details_all_done) else stringResource(R.string.details_remaining, remaining),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun LessonRow(index: Int, lesson: Lesson, onMarkCompleted: () -> Unit) {
    AppCard(contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(
                        if (lesson.isCompleted) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                if (lesson.isCompleted) {
                    Icon(
                        Icons.Filled.Check,
                        contentDescription = stringResource(R.string.lesson_completed),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(20.dp),
                    )
                } else {
                    Text(index.toString(), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    lesson.title,
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (lesson.isCompleted) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    stringResource(if (lesson.isCompleted) R.string.lesson_completed else R.string.lesson_pending),
                    style = MaterialTheme.typography.bodySmall,
                    color = if (lesson.isCompleted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            if (!lesson.isCompleted) {
                Spacer(Modifier.width(8.dp))
                OutlinePill(text = stringResource(R.string.lesson_mark_done), onClick = onMarkCompleted)
            }
        }
    }
}
