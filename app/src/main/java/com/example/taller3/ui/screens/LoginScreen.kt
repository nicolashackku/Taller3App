package com.example.taller3.ui.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.taller3.viewmodels.AuthEvent
import com.example.taller3.viewmodels.AuthState
import com.example.taller3.viewmodels.AuthViewModel
import com.example.taller3.R
import com.example.taller3.ui.components.ErrorMessage
import com.example.taller3.ui.components.LoginForm
import com.example.taller3.ui.theme.AppTheme

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    goHome: () -> Unit = {},
    goRegister: () -> Unit = {},
    viewModel: AuthViewModel = viewModel(),
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                is AuthEvent.NavigateToHome -> goHome()
                is AuthEvent.ShowMessage ->
                    Toast.makeText(context, event.text.asString(context), Toast.LENGTH_SHORT).show()
                else -> Unit
            }
        }
    }

    LoginContent(
        state = state,
        onDismissError = viewModel::clearError,
        onLogin = { email, password ->
            viewModel.clearError()
            viewModel.login(email, password)
        },
        onRegister = goRegister,
        onForgotPassword = { email ->
            viewModel.clearError()
            viewModel.forgotPassword(email)
        },
        modifier = modifier,
    )
}

@Composable
private fun LoginContent(
    state: AuthState,
    onDismissError: () -> Unit,
    onLogin: (String, String) -> Unit,
    onRegister: () -> Unit,
    onForgotPassword: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .imePadding(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier.verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Surface(shape = RoundedCornerShape(20.dp), color = Color(0xFF123F42)) {
                    Image(
                        painter = painterResource(R.drawable.ic_route_mark),
                        contentDescription = null,
                        modifier = Modifier.size(76.dp),
                    )
                }
                Column {
                    Text(stringResource(R.string.app_name), style = MaterialTheme.typography.headlineMedium)
                    Text(
                        stringResource(R.string.login_screen_title),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            if (state is AuthState.Loading) CircularProgressIndicator(Modifier.padding(8.dp))
            ErrorMessage(
                message = (state as? AuthState.Error)?.message?.asString(),
                onDismiss = onDismissError,
                modifier = Modifier.padding(horizontal = 16.dp),
            )
            LoginForm(
                enabled = state !is AuthState.Loading,
                onLogin = onLogin,
                onRegister = onRegister,
                onForgotPassword = onForgotPassword,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LoginScreenPreview() {
    AppTheme {
        LoginContent(
            state = AuthState.Idle,
            onDismissError = {},
            onLogin = { _, _ -> },
            onRegister = {},
            onForgotPassword = {},
        )
    }
}
