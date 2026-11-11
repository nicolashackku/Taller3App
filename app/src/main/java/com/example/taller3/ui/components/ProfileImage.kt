package com.example.taller3.ui.components

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import coil3.compose.AsyncImage
import com.example.taller3.R
import com.example.taller3.ui.theme.AppTheme

/**
 * Profile picture selector for the register and profile screens.
 *
 * Shows [imageUrl] (previous profile image) or a placeholder inside a circle. Tapping it checks
 * the CAMERA permission and then embeds a [CameraPreviewContent] in the same circle. A taken
 * photo is shown as a thumbnail at the bottom end and [onImageCaptured] is called with its Uri, or with null when it is removed.
 */
@Composable
fun ProfileImage(
    onImageCaptured: (Uri?) -> Unit,
    modifier: Modifier = Modifier,
    imageUrl: String? = null,
    enabled: Boolean = true,
    size: Dp = 240.dp,
) {
    val context = LocalContext.current
    var showCamera by rememberSaveable { mutableStateOf(false) }
    var cameraDenied by rememberSaveable { mutableStateOf(false) }
    var confirmedUri by rememberSaveable { mutableStateOf<Uri?>(null) }
    var capturedUri by rememberSaveable { mutableStateOf<Uri?>(null) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        cameraDenied = !granted
        showCamera = granted
    }

    fun openCamera() {
        val granted = ContextCompat.checkSelfPermission(
            context, Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
        if (granted) showCamera = true
        else permissionLauncher.launch(Manifest.permission.CAMERA)
    }

    // Without a previous image the camera opens right away; otherwise the image is shown first.
    LaunchedEffect(Unit) {
        if (imageUrl.isNullOrBlank() && confirmedUri == null && !cameraDenied) openCamera()
    }

    // The camera surface ignores alpha and stays interactive, so close it while disabled.
    LaunchedEffect(enabled) {
        if (!enabled) showCamera = false
    }

    val shape = RoundedCornerShape(12.dp)
    val displayed: Any? = confirmedUri ?: imageUrl?.takeIf { it.isNotBlank() }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Box(modifier = Modifier.size(size).alpha(if (enabled) 1f else 0.4f)) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .shadow(6.dp, shape)
                    .border(4.dp, Color.White, shape)
                    .clip(shape)
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .clickable(enabled = enabled && !showCamera) { openCamera() },
            ) {
                if (showCamera) {
                    CameraPreviewContent(
                        modifier = Modifier.fillMaxSize(),
                        onPhotoTaken = { uri ->
                            capturedUri = uri
                            confirmedUri = uri
                            onImageCaptured(uri)
                        },
                    )
                } else if (displayed != null) {
                    AsyncImage(
                        model = displayed,
                        contentDescription = stringResource(R.string.profile_image_profile_picture_image),
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                    )
                } else {
                    Icon(
                        Icons.Filled.CameraAlt,
                        contentDescription = stringResource(R.string.profile_image_take_picture_icon),
                        modifier = Modifier.size(size / 3),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                }
            }
            if (showCamera) {
                capturedUri?.let { uri ->
                    CapturedPhotoPreview(
                        uri = uri,
                        onRemove = {
                            capturedUri = null
                            confirmedUri = null
                            onImageCaptured(null)
                        },
                        modifier = Modifier.align(Alignment.TopEnd).padding(12.dp),
                    )
                }
            }
        }
        if (cameraDenied) {
            Text(
                text = stringResource(R.string.profile_image_camera_permission_text),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
            )
        }
    }
}

/** Thumbnail of the taken photo with a remove button beside it, within its height. */
@Composable
private fun CapturedPhotoPreview(
    uri: Uri?,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        AsyncImage(
            model = uri,
            contentDescription = stringResource(R.string.profile_image_captured_photo_image),
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(72.dp)
                .border(4.dp, Color.White, RoundedCornerShape(12.dp))
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.secondaryContainer),
        )
        IconButton(
            onClick = onRemove,
            modifier = Modifier.size(24.dp),
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = MaterialTheme.colorScheme.surface,
            ),
        ) {
            Icon(
                Icons.Filled.Close,
                contentDescription = stringResource(R.string.profile_image_remove_photo_icon),
                modifier = Modifier.size(16.dp),
            )
        }
    }
}

@Preview(showBackground = true, name = "Profile image empty")
@Composable
private fun ProfileImageEmptyPreview() {
    AppTheme { ProfileImage(onImageCaptured = {}, modifier = Modifier.padding(16.dp)) }
}

@Preview(showBackground = true, name = "Profile image with previous url")
@Composable
private fun ProfileImageWithUrlPreview() {
    AppTheme {
        ProfileImage(
            onImageCaptured = {},
            imageUrl = "https://example.com/profile.jpg",
            modifier = Modifier.padding(16.dp),
        )
    }
}

@Preview(showBackground = true, name = "Profile image disabled")
@Composable
private fun ProfileImageDisabledPreview() {
    AppTheme {
        ProfileImage(
            onImageCaptured = {},
            imageUrl = "https://example.com/profile.jpg",
            enabled = false,
            modifier = Modifier.padding(16.dp),
        )
    }
}

@Preview(showBackground = true, name = "Captured photo preview")
@Composable
private fun CapturedPhotoPreviewPreview() {
    AppTheme {
        CapturedPhotoPreview(uri = null, onRemove = {}, modifier = Modifier.padding(16.dp))
    }
}
