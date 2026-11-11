package com.example.taller3.ui.components

import android.net.Uri
import android.widget.Toast
import androidx.camera.compose.CameraXViewfinder
import androidx.camera.viewfinder.compose.MutableCoordinateTransformer
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.taller3.R
import com.example.taller3.ui.theme.AppTheme
import com.example.taller3.viewmodels.CameraViewModel

@Composable
fun CameraPreviewContent(
    modifier: Modifier = Modifier,
    viewModel: CameraViewModel = viewModel(),
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    onPhotoTaken: (Uri) -> Unit = { _ -> }
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(lifecycleOwner) {
        viewModel.bindToCamera(context.applicationContext, lifecycleOwner)
    }

    Box(modifier = modifier) {
        uiState.surfaceRequest?.let { request ->
            val coordinateTransformer = remember { MutableCoordinateTransformer() }
            CameraXViewfinder(
                surfaceRequest = request,
                coordinateTransformer = coordinateTransformer,
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectTapGestures { tapCoords ->
                            with(coordinateTransformer) {
                                viewModel.tapToFocus(tapCoords.transform())
                            }
                        }
                    }
            )
        }
        //Top start switch camera button
        FilledTonalIconButton(
            onClick = { viewModel.toggleCameraLens() },
            modifier = Modifier
                .padding(12.dp)
                .align(Alignment.TopStart)
        ) {
            Icon(Icons.Default.Cameraswitch, contentDescription = stringResource(R.string.camera_preview_switch_camera_icon))
        }
        //Bottom buttons
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(12.dp)
        ) {
            FilledIconButton(
                onClick = {
                    viewModel.takePhoto(context = context) { uri ->
                        if (uri != null) {
                            onPhotoTaken(uri)
                        }
                    }
                    Toast.makeText(context, context.getString(R.string.camera_preview_taking_photo_toast), Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.size(64.dp)
            ) {
                Icon(
                    Icons.Default.Camera,
                    contentDescription = stringResource(R.string.camera_preview_take_photo_icon),
                    modifier = Modifier.size(48.dp)
                )
            }
        }
    }
}

@Composable
@Preview
fun CameraPreviewContentPreview() {
    AppTheme() {
        CameraPreviewContent(modifier = Modifier.size(500.dp), onPhotoTaken = {})
    }
}