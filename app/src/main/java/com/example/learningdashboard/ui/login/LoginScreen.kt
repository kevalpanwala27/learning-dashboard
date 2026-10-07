package com.example.learningdashboard.ui.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.School
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.learningdashboard.R
import com.example.learningdashboard.di.AppViewModelFactory
import com.example.learningdashboard.ui.components.GradientButton
import com.example.learningdashboard.ui.components.GradientHeader

@Composable
fun LoginScreen(viewModel: LoginViewModel = viewModel(factory = AppViewModelFactory)) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    LoginContent(
        state = state,
        onEmailChange = viewModel::onEmailChange,
        onPasswordChange = viewModel::onPasswordChange,
        onSubmit = viewModel::submit,
    )
}

@Composable
private fun LoginContent(
    state: LoginUiState,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onSubmit: () -> Unit,
) {
    var showPassword by remember { mutableStateOf(false) }

    // Surface-coloured so the form sheet blends into the rest of the screen below it.
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .verticalScroll(rememberScrollState())
            .imePadding(),
    ) {
        GradientHeader {
            Spacer(Modifier.height(32.dp))
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White.copy(alpha = 0.18f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Outlined.School, contentDescription = null, tint = Color.White, modifier = Modifier.size(30.dp))
            }
            Spacer(Modifier.height(20.dp))
            Text(stringResource(R.string.app_name), style = MaterialTheme.typography.headlineLarge, color = Color.White)
            Spacer(Modifier.height(6.dp))
            Text(
                stringResource(R.string.login_tagline),
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White.copy(alpha = 0.85f),
            )
            Spacer(Modifier.height(40.dp))
        }

        // The form sheet overlaps the header's bottom corners.
        Surface(
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = (-28).dp),
        ) {
            Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 28.dp)) {
                Text(stringResource(R.string.login_title), style = MaterialTheme.typography.headlineMedium)
                Text(
                    stringResource(R.string.login_subtitle),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(Modifier.height(28.dp))

                OutlinedTextField(
                    value = state.email,
                    onValueChange = onEmailChange,
                    label = { Text(stringResource(R.string.login_email)) },
                    leadingIcon = { Icon(Icons.Outlined.Email, contentDescription = null) },
                    isError = state.emailError != null,
                    supportingText = state.emailError?.let { { Text(it) } },
                    singleLine = true,
                    enabled = !state.isSubmitting,
                    shape = MaterialTheme.shapes.medium,
                    colors = fieldColors(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    value = state.password,
                    onValueChange = onPasswordChange,
                    label = { Text(stringResource(R.string.login_password)) },
                    leadingIcon = { Icon(Icons.Outlined.Lock, contentDescription = null) },
                    isError = state.passwordError != null,
                    supportingText = state.passwordError?.let { { Text(it) } },
                    singleLine = true,
                    enabled = !state.isSubmitting,
                    shape = MaterialTheme.shapes.medium,
                    colors = fieldColors(),
                    visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { showPassword = !showPassword }) {
                            Icon(
                                imageVector = if (showPassword) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                contentDescription = stringResource(
                                    if (showPassword) R.string.login_hide_password else R.string.login_show_password,
                                ),
                            )
                        }
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { onSubmit() }),
                    modifier = Modifier.fillMaxWidth(),
                )

                state.loginError?.let { message ->
                    Spacer(Modifier.height(12.dp))
                    Surface(
                        color = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer,
                        shape = MaterialTheme.shapes.small,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(message, modifier = Modifier.padding(14.dp), style = MaterialTheme.typography.bodyMedium)
                    }
                }

                Spacer(Modifier.height(28.dp))
                GradientButton(
                    text = stringResource(R.string.login_button),
                    onClick = onSubmit,
                    loading = state.isSubmitting,
                    modifier = Modifier.fillMaxWidth(),
                )

                Spacer(Modifier.height(20.dp))
                Text(
                    stringResource(R.string.login_demo_hint),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
        Spacer(Modifier.navigationBarsPadding())
    }
}

@Composable
private fun fieldColors() = OutlinedTextFieldDefaults.colors(
    focusedContainerColor = MaterialTheme.colorScheme.background,
    unfocusedContainerColor = MaterialTheme.colorScheme.background,
    disabledContainerColor = MaterialTheme.colorScheme.background,
    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
)
