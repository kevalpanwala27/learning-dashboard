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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.School
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.learningdashboard.R
import com.example.learningdashboard.di.AppViewModelFactory
import com.example.learningdashboard.domain.model.Course
import com.example.learningdashboard.domain.model.Lesson
import com.example.learningdashboard.ui.components.GradientHeader
import com.example.learningdashboard.ui.components.ProgressRing
import com.example.learningdashboard.ui.components.StateMessage

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
    Scaffold(containerColor = MaterialTheme.colorScheme.background) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = padding.calculateBottomPadding()),
            contentPadding = PaddingValues(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            item(key = "header") {
                DetailsHero(course = (state as? CourseDetailsUiState.Success)?.course, onBack = onBack)
            }
            when (state) {
                CourseDetailsUiState.Loading -> item(key = "loading") {
                    Box(Modifier.fillMaxWidth().padding(vertical = 64.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                CourseDetailsUiState.NotFound -> item(key = "not-found") {
                    StateMessage(
                        icon = Icons.Outlined.School,
                        title = stringResource(R.string.details_not_found),
                        body = "",
                    )
                }

                is CourseDetailsUiState.Success -> {
                    item(key = "lessons-title") {
                        Text(
                            stringResource(R.string.details_lessons_header),
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 12.dp, bottom = 4.dp),
                        )
                    }
                    itemsIndexed(state.course.lessons, key = { _, lesson -> lesson.id }) { index, lesson ->
                        LessonRow(
                            index = index + 1,
                            lesson = lesson,
                            onMarkCompleted = { onMarkCompleted(lesson.id) },
                            modifier = Modifier.padding(horizontal = 16.dp),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailsHero(course: Course?, onBack: () -> Unit) {
    GradientHeader {
        IconButton(onClick = onBack, modifier = Modifier.offset(x = (-12).dp)) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.details_back), tint = Color.White)
        }
        Spacer(Modifier.height(4.dp))
        Text(course?.title.orEmpty(), style = MaterialTheme.typography.headlineMedium, color = Color.White)
        if (course != null) {
            Text(
                stringResource(R.string.course_instructor, course.instructor),
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.85f),
            )
            Spacer(Modifier.height(20.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                ProgressRing(
                    progress = course.progressPercent / 100f,
                    size = 84.dp,
                    strokeWidth = 8.dp,
                    trackColor = Color.White.copy(alpha = 0.25f),
                    progressColor = Color.White,
                ) {
                    Text(
                        stringResource(R.string.course_progress, course.progressPercent),
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                    )
                }
                Spacer(Modifier.width(20.dp))
                Column {
                    Text(
                        stringResource(R.string.details_progress_summary, course.completedCount, course.lessonCount),
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                    )
                    val remaining = course.lessonCount - course.completedCount
                    Text(
                        text = if (remaining == 0) {
                            stringResource(R.string.details_all_done)
                        } else {
                            stringResource(R.string.details_remaining, remaining)
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.8f),
                    )
                }
            }
        }
    }
}

@Composable
private fun LessonRow(index: Int, lesson: Lesson, onMarkCompleted: () -> Unit, modifier: Modifier = Modifier) {
    Surface(
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 1.dp,
        modifier = modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(
                        if (lesson.isCompleted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                if (lesson.isCompleted) {
                    Icon(
                        Icons.Filled.Check,
                        contentDescription = stringResource(R.string.lesson_completed),
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(20.dp),
                    )
                } else {
                    Text(
                        index.toString(),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    lesson.title,
                    style = MaterialTheme.typography.bodyLarge,
                    textDecoration = if (lesson.isCompleted) TextDecoration.LineThrough else null,
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
                FilledTonalButton(
                    onClick = onMarkCompleted,
                    shape = CircleShape,
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                ) {
                    Text(stringResource(R.string.lesson_mark_done), style = MaterialTheme.typography.labelMedium)
                }
            }
        }
    }
}
