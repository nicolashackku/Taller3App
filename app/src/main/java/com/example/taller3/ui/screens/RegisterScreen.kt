package com.example.taller3.ui.screens

import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.taller3.R
import com.example.taller3.data.UserInfo
import com.example.taller3.ui.components.ErrorMessage
import com.example.taller3.ui.components.ProfileImage
import com.example.taller3.ui.components.RegisterForm
import com.example.taller3.utils.UiText
import com.example.taller3.viewmodels.AuthEvent
import com.example.taller3.viewmodels.AuthState
import com.example.taller3.viewmodels.AuthViewModel
import com.example.taller3.viewmodels.FirestoreEvent
import com.example.taller3.viewmodels.FirestoreViewModel

@Composable
fun RegisterScreen(
    modifier: Modifier = Modifier,
    goHome: () -> Unit = {},
    authViewModel: AuthViewModel = viewModel(),
    firestoreViewModel: FirestoreViewModel = viewModel(),
) {
    val authState by authViewModel.state.collectAsState()
    val firestoreState by firestoreViewModel.state.collectAsState()

    val busy = authState is AuthState.Loading || firestoreState.loading
    var pendingProfile by remember { mutableStateOf<UserInfo?>(null) }
    var photoUri by remember { mutableStateOf<Uri?>(null) }
    var profileError by remember { mutableStateOf<UiText?>(null) }

    LaunchedEffect(firestoreViewModel) {
        firestoreViewModel.events.collect { event ->
            if (event is FirestoreEvent.Error) profileError = event.message
        }
    }

    LaunchedEffect(authViewModel) {
        authViewModel.events.collect { event ->
            if (event !is AuthEvent.NavigateToHome) return@collect
            val uid = (authViewModel.state.value as? AuthState.Authenticated)?.user?.uid
            val profile = pendingProfile
            if (uid != null && profile != null) {
                /* Firebase Storage image upload is disabled.
                storageViewModel.uploadUserFile(uid, "profile_image.jpg", photo) { url ->
                    if (url == null) return@uploadUserFile
                    firestoreViewModel.saveUser(uid, profile.copy(photo = url)) { ok ->
                        if (ok) goHome()
                    }
                }
                */
                firestoreViewModel.saveUser(uid, profile) { ok ->
                    if (ok) goHome()
                    else if (profileError == null) profileError = UiText.Resource(R.string.register_screen_profile_save_error_message)
                }
            } else profileError = UiText.Resource(R.string.register_screen_profile_save_error_message)
        }
    }

    Box(
        modifier = modifier.fillMaxSize().safeDrawingPadding(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.verticalScroll(rememberScrollState()),
        ) {
            ProfileImage(
                onImageCaptured = { photoUri = it },
                enabled = !busy,
                modifier = Modifier.padding(8.dp),
            )
            if (busy) {
                CircularProgressIndicator(Modifier.padding(8.dp))
            }
            ErrorMessage(
                message = ((authState as? AuthState.Error)?.message ?: profileError)?.asString(),
                onDismiss = {
                    authViewModel.clearError()
                    profileError = null
                },
                modifier = Modifier.padding(horizontal = 16.dp),
            )
            RegisterForm(
                enabled = !busy,
                registerEnabled = true,
                onRegister = { email, displayName, phone, password ->
                    authViewModel.clearError()
                    profileError = null
                    pendingProfile = UserInfo(
                        displayName = displayName,
                        phone = phone.filter { it.isDigit() }.toLongOrNull() ?: 0L,
                    )
                    authViewModel.register(email, password)
                },
            )
        }
    }
}