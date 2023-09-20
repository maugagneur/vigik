package com.kidor.vigik.ui.camera

import android.Manifest
import android.content.Context
import android.net.Uri
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.core.TorchState
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FlashlightOff
import androidx.compose.material.icons.filled.FlashlightOn
import androidx.compose.material.icons.filled.FlipCameraAndroid
import androidx.compose.material.icons.filled.Lens
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material.icons.filled.ZoomOut
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.kidor.vigik.R
import com.kidor.vigik.extensions.getCameraProvider
import com.kidor.vigik.ui.base.ObserveViewState
import com.kidor.vigik.ui.compose.AppTheme
import timber.log.Timber
import java.io.File
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import kotlin.math.max
import kotlin.math.min

private const val CAMERA_ZOOM_RATIO_STEP = 0.25f

/**
 * View that display the section dedicated to camera.
 */
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraScreen(viewModel: CameraViewModel = hiltViewModel()) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ObserveViewState(viewModel) { state ->
            when (state) {
                CameraViewState.CheckPermission -> PermissionView {
                    viewModel.handleAction(CameraViewAction.PermissionGranted)
                }

                CameraViewState.ShowCamera -> {
                    CameraView(
                        executor = Executors.newSingleThreadExecutor(),
                        onImageCaptured = { viewModel.handleAction(CameraViewAction.PhotoCaptured(it)) }
                    )
                }

                is CameraViewState.ShowCapturedPhoto -> {
                    ButtonBar(
                        buttons = listOf(
                            CameraButtonData(R.string.camera_retry_capture_button_label) {
                                viewModel.handleAction(CameraViewAction.RetryCapture)
                            }
                        )
                    )
                    Image(
                        painter = rememberAsyncImagePainter(model = state.uri),
                        contentDescription = "Photo",
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}

@ExperimentalPermissionsApi
@Composable
private fun PermissionView(onPermissionGranted: () -> Unit) {
    val cameraPermissionState = rememberPermissionState(permission = Manifest.permission.CAMERA)
    val permissionGranted = cameraPermissionState.status.isGranted
    if (permissionGranted) {
        onPermissionGranted()
    }

    Column(
        modifier = Modifier.padding(vertical = AppTheme.dimensions.commonSpaceMedium),
        verticalArrangement = Arrangement.spacedBy(AppTheme.dimensions.commonSpaceSmall),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(AppTheme.dimensions.commonSpaceSmall),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(id = R.string.camera_permission_status_label),
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = AppTheme.dimensions.textSizeMedium
            )
            if (permissionGranted) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Permissions granted",
                    tint = MaterialTheme.colorScheme.tertiary
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Permissions not granted",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
        if (!permissionGranted) {
            Button(onClick = { cameraPermissionState.launchPermissionRequest() }) {
                Text(
                    text = stringResource(id = R.string.camera_permission_request_button_label).uppercase(),
                    fontSize = AppTheme.dimensions.textSizeLarge
                )
            }
        }
    }
}

@Composable
private fun CameraView(executor: Executor, onImageCaptured: (Uri) -> Unit) {
    val lensFacing = remember { mutableIntStateOf(CameraSelector.LENS_FACING_BACK) }
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val preview = Preview.Builder().build()
    val previewView = remember { PreviewView(context) }
    val imageCapture = remember {
        ImageCapture.Builder()
            .setFlashMode(ImageCapture.FLASH_MODE_AUTO)
            .build()
    }
    val cameraSelector = CameraSelector.Builder()
        .requireLensFacing(lensFacing.intValue)
        .build()
    val camera = remember { mutableStateOf(null as Camera?) }
    val isTorchAvailable = remember { mutableStateOf(false) }
    val isTorchEnabled = remember { mutableStateOf(false) }

    LaunchedEffect(lensFacing.intValue) {
        camera.value = context.getCameraProvider().let { cameraProvider ->
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, preview, imageCapture)
        }.also {
            isTorchAvailable.value = it.cameraInfo.hasFlashUnit()
            isTorchEnabled.value = it.cameraInfo.torchState.value == TorchState.ON
        }
        preview.setSurfaceProvider(previewView.surfaceProvider)
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        AndroidView(
            factory = { previewView },
            modifier = Modifier.fillMaxSize()
        )
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.padding(top = AppTheme.dimensions.commonSpaceMedium)
            ) {
                SettingsLensButton(lensFacing = lensFacing)
                SettingsZoomOutButton(camera = camera)
                SettingsZoomInButton(camera = camera)
                // Hide flash icon if camera does not have torch
                if (isTorchAvailable.value) {
                    SettingsTorchButton(camera = camera, isTorchEnabled = isTorchEnabled)
                }
            }
            TakePhotoButton(
                context = context,
                executor = executor,
                imageCapture = imageCapture,
                onImageCaptured = onImageCaptured
            )
        }
    }
}

@Composable
private fun SettingsLensButton(lensFacing: MutableIntState) {
    IconButton(
        onClick = {
            lensFacing.intValue = if (lensFacing.intValue == CameraSelector.LENS_FACING_BACK) {
                CameraSelector.LENS_FACING_FRONT
            } else {
                CameraSelector.LENS_FACING_BACK
            }
        }
    ) {
        Icon(imageVector = Icons.Default.FlipCameraAndroid, contentDescription = "Flip")
    }
}

@Composable
private fun SettingsZoomOutButton(camera: MutableState<Camera?>) {
    IconButton(
        onClick = {
            camera.value?.let {
                it.cameraInfo.zoomState.value?.let { zoomState ->
                    it.cameraControl.setZoomRatio(
                        max(zoomState.minZoomRatio, zoomState.zoomRatio - CAMERA_ZOOM_RATIO_STEP)
                    )
                }
            }
        }
    ) {
        Icon(imageVector = Icons.Default.ZoomOut, contentDescription = "Zoom out")
    }
}

@Composable
private fun SettingsZoomInButton(camera: MutableState<Camera?>) {
    IconButton(
        onClick = {
            camera.value?.let {
                it.cameraInfo.zoomState.value?.let { zoomState ->
                    it.cameraControl.setZoomRatio(
                        min(zoomState.maxZoomRatio, zoomState.zoomRatio + CAMERA_ZOOM_RATIO_STEP)
                    )
                }
            }
        }
    ) {
        Icon(imageVector = Icons.Default.ZoomIn, contentDescription = "Zoom in")
    }
}

@Composable
private fun SettingsTorchButton(camera: MutableState<Camera?>, isTorchEnabled: MutableState<Boolean>) {
    IconButton(
        onClick = {
            camera.value?.let {
                // Inverse torch state
                isTorchEnabled.value = !isTorchEnabled.value
                it.cameraControl.enableTorch(isTorchEnabled.value)
            }
        }
    ) {
        if (isTorchEnabled.value) {
            Icon(imageVector = Icons.Default.FlashlightOff, contentDescription = "Torch Off")
        } else {
            Icon(imageVector = Icons.Default.FlashlightOn, contentDescription = "Torch On")
        }
    }
}

@Composable
private fun TakePhotoButton(
    context: Context,
    executor: Executor,
    imageCapture: ImageCapture,
    onImageCaptured: (Uri) -> Unit
) {
    IconButton(
        onClick = {
            takePhoto(
                outputDirectory = context.filesDir, // Photo will be saved in \\data\data\{package_name}\files\
                imageCapture = imageCapture,
                executor = executor,
                onImageCaptured = onImageCaptured
            )
        },
        modifier = Modifier
            .size(120.dp)
            .padding(bottom = AppTheme.dimensions.commonSpaceXLarge)
    ) {
        Icon(
            imageVector = Icons.Default.Lens,
            contentDescription = "Take photo",
            modifier = Modifier
                .size(80.dp)
                .padding(1.dp)
                .border(1.dp, Color.White, CircleShape),
            tint = Color.White
        )
    }
}

/**
 * Captures a photo through camera.
 *
 * @param outputDirectory The directory where the photo should be stored.
 * @param imageCapture    The use case to capture the photo.
 * @param executor        The executor in which the image capture will be run.
 * @param onImageCaptured Callback to notify the photo's URI.
 */
private fun takePhoto(
    outputDirectory: File,
    imageCapture: ImageCapture,
    executor: Executor,
    onImageCaptured: (Uri) -> Unit
) {
    val photoFile = File(outputDirectory, "captured_photo.jpg")
    val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

    imageCapture.takePicture(
        outputOptions,
        executor,
        object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                outputFileResults.savedUri?.let { onImageCaptured(it) }
            }

            override fun onError(exception: ImageCaptureException) {
                Timber.e(exception, "Fail to take picture")
            }
        }
    )
}

@Composable
private fun ButtonBar(buttons: List<CameraButtonData>) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = AppTheme.dimensions.commonSpaceMedium),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        buttons.forEach { button ->
            Button(onClick = button.onClick) {
                Text(
                    text = stringResource(id = button.textId).uppercase(),
                    fontSize = AppTheme.dimensions.textSizeSmall
                )
            }
        }
    }
}
