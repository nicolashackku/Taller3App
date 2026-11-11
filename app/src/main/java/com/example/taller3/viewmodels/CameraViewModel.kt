package com.example.taller3.viewmodels

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.camera.core.CameraControl
import androidx.camera.core.CameraSelector
import androidx.camera.core.FocusMeteringAction
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.core.SurfaceOrientedMeteringPointFactory
import androidx.camera.core.SurfaceRequest
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.lifecycle.awaitInstance
import androidx.compose.ui.geometry.Offset
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class CameraPreviewState(
    val surfaceRequest: SurfaceRequest? = null,
    val useBackLens: Boolean = true,
    val lastCapturedImageUri: Uri? = null,
)

class CameraViewModel : ViewModel() {
    // Used to set up a link between the Camera and your UI.
    private val _uiState = MutableStateFlow(CameraPreviewState())
    val uiState: StateFlow<CameraPreviewState> = _uiState.asStateFlow()

    // Used for camera control and tap-to-focus functionality.
    private var surfaceMeteringPointFactory: SurfaceOrientedMeteringPointFactory? = null
    private var cameraControl: CameraControl? = null
    private var lifecycleOwner: LifecycleOwner? = null
    private var processCameraProvider: ProcessCameraProvider? = null
    private var selector = CameraSelector.DEFAULT_BACK_CAMERA
    private var imageCapture = ImageCapture.Builder()
        .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
        .build()

    private val cameraPreviewUseCase = Preview.Builder().build().apply {
        setSurfaceProvider { newSurfaceRequest ->
            surfaceMeteringPointFactory = SurfaceOrientedMeteringPointFactory(
                newSurfaceRequest.resolution.width.toFloat(),
                newSurfaceRequest.resolution.height.toFloat()
            )
            _uiState.update { it.copy(surfaceRequest = newSurfaceRequest) }
        }
    }

    suspend fun bindToCamera(appContext: Context, lifecycleOwner: LifecycleOwner) {
        this.lifecycleOwner = lifecycleOwner
        processCameraProvider = ProcessCameraProvider.awaitInstance(appContext)
        val camera = processCameraProvider?.bindToLifecycle(
            lifecycleOwner, selector, cameraPreviewUseCase, imageCapture
        )
        cameraControl = camera?.cameraControl

        try {
            awaitCancellation()
        } finally {
            cameraControl = null
            surfaceMeteringPointFactory = null
            processCameraProvider?.unbindAll()
        }
    }

    fun tapToFocus(tapCoords: Offset) {
        val point = surfaceMeteringPointFactory?.createPoint(tapCoords.x, tapCoords.y)
        if (point != null) {
            val meteringAction = FocusMeteringAction.Builder(point).build()
            cameraControl?.startFocusAndMetering(meteringAction)
        }
    }

    fun toggleCameraLens() {
        val nextUseBackLens = !_uiState.value.useBackLens
        _uiState.update { it.copy(useBackLens = nextUseBackLens) }
        selector = if (nextUseBackLens) {
            CameraSelector.DEFAULT_BACK_CAMERA
        } else {
            CameraSelector.DEFAULT_FRONT_CAMERA
        }

        val owner = lifecycleOwner
        val provider = processCameraProvider
        if (owner != null && provider != null) {
            provider.unbindAll()
            val camera = provider.bindToLifecycle(owner, selector, cameraPreviewUseCase, imageCapture)
            cameraControl = camera.cameraControl
        }
    }

    fun takePhoto(context: Context, onPhotoCaptured: (Uri?) -> Unit = {}) {
        // OutputFileOptions.file is null for MediaStore targets, so save to a MediaStore Uri instead.
        val values = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "CHAT_IMG_${System.currentTimeMillis()}.jpg")
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        }
        val outputOptions = ImageCapture.OutputFileOptions.Builder(
            context.contentResolver,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            values
        ).build()
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(context),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    val imageUri = outputFileResults.savedUri
                    Log.w("CameraPreviewViewModel", "Photo saved to: $imageUri")
                    _uiState.update { it.copy(lastCapturedImageUri = imageUri) }
                    onPhotoCaptured(imageUri)
                }

                override fun onError(exception: ImageCaptureException) {
                    Log.e("CameraPreviewViewModel", "Photo capture failed", exception)
                    onPhotoCaptured(null)
                }
            }
        )
    }
}
