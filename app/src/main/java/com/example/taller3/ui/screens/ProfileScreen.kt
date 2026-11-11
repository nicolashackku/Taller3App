package com.example.taller3.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.taller3.R
import com.example.taller3.data.UserInfo
import com.example.taller3.ui.components.EditForm
import com.example.taller3.ui.components.ProfileImage
import com.example.taller3.viewmodels.AuthEvent
import com.example.taller3.viewmodels.AuthState
import com.example.taller3.viewmodels.AuthViewModel
import com.example.taller3.viewmodels.FirestoreEvent
import com.example.taller3.viewmodels.FirestoreViewModel

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    viewModel: AuthViewModel = viewModel(),
    firestoreViewModel: FirestoreViewModel = viewModel(),
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val profile by firestoreViewModel.state.collectAsState()
    val userInfo = profile.user ?: UserInfo(
        displayName = currentUser?.displayName?.takeIf { it.isNotBlank() }
            ?: currentUser?.email
            ?: stringResource(R.string.profile_screen_guest_fallback_text),
        photo = currentUser?.photoUrl?.toString(),
    )

    val uid = currentUser?.uid
    val authState by viewModel.state.collectAsState()
    val busy = authState is AuthState.Loading || profile.loading
    val context = LocalContext.current

    fun toast(message: String) = Toast.makeText(context, message, Toast.LENGTH_SHORT).show()

    LaunchedEffect(viewModel) {
        viewModel.events.collect { if (it is AuthEvent.ShowMessage) toast(it.text.asString(context)) }
    }
    LaunchedEffect(authState) {
        (authState as? AuthState.Error)?.let {
            toast(it.message.asString(context))
            viewModel.clearError()
        }
    }
    LaunchedEffect(firestoreViewModel) {
        firestoreViewModel.events.collect { if (it is FirestoreEvent.Error) toast(it.message.asString(context)) }
    }
    Column(
        modifier = modifier
            .fillMaxSize()
            .imePadding()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        ProfileImage(
            imageUrl = userInfo.photo,
            enabled = !busy,
            onImageCaptured = { uri ->
                if (uri == null || uid == null) return@ProfileImage
                /* Firebase Storage image upload is disabled.
                storageViewModel.uploadUserFile(uid, "profile_image.jpg", uri) { url ->
                    if (url != null) {
                        firestoreViewModel.updateUser(uid, mapOf("photo" to url)) { ok ->
                            if (ok) toast(context.getString(R.string.profile_screen_image_updated_toast))
                        }
                    }
                }
                */
            },
            modifier = Modifier.padding(16.dp),
        )
        EditForm(
            email = currentUser?.email.orEmpty(),
            displayName = userInfo.displayName,
            phone = userInfo.phone.takeIf { it != 0L }?.toString().orEmpty(),
            enabled = !busy,
            onUserInfoChange = { displayName, phone ->
                if (uid == null) return@EditForm
                firestoreViewModel.updateUser(
                    uid,
                    mapOf(
                        "displayName" to displayName,
                        "phone" to (phone.filter { it.isDigit() }.toLongOrNull() ?: 0L),
                    ),
                ) { ok -> if (ok) toast(context.getString(R.string.profile_screen_updated_toast)) }
            },
            onPasswordChange = { password -> viewModel.updatePassword(password) },
        )
    }
}
