package com.kidor.vigik.ui.camera

import android.Manifest
import android.net.Uri
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.FocusMeteringAction
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.core.TorchState
import androidx.camera.core.UseCaseGroup
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.magnifier
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import coil3.SingletonImageLoader
import coil3.compose.rememberAsyncImagePainter
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.kidor.vigik.R
import com.kidor.vigik.extensions.getCameraProvider
import com.kidor.vigik.ui.base.ObserveViewState
import com.kidor.vigik.ui.theme.dimensions
import timber.log.Timber
import java.io.File
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import kotlin.math.max
import kotlin.math.min

private const val CAMERA_ZOOM_RATIO_STEP = 0.25f
private const val MAGNIFIER_Y_OFFSET = 200f
private val MAGNIFIER_SIZE = 100.dp
private val MAGNIFIER_CORNER_RADIUS = 100.dp

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

                CameraViewState.ShowCamera -> CameraView(
                    executor = Executors.newSingleThreadExecutor(),
                    onImageCaptured = { viewModel.handleAction(CameraViewAction.PhotoCaptured(it)) }
                )

                is CameraViewState.ShowCapturedPhoto -> DisplayPhotoView(
                    imageUri = state.uri,
                    retry = { viewModel.handleAction(CameraViewAction.RetryCapture) }
                )
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
        modifier = Modifier.padding(vertical = MaterialTheme.dimensions.commonSpaceMedium),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.dimensions.commonSpaceXSmall),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.dimensions.commonSpaceXSmall),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(id = R.string.camera_permission_status_label),
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = MaterialTheme.dimensions.textSizeMedium
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
                    fontSize = MaterialTheme.dimensions.textSizeLarge
                )
            }
        }
    }
}

@Composable
@Suppress("LongMethod")
private fun CameraView(executor: Executor, onImageCaptured: (Uri) -> Unit) {
    val camera = remember { mutableStateOf(null as Camera?) }
    val lensFacing = remember { mutableIntStateOf(CameraSelector.LENS_FACING_BACK) }
    val rotationDegrees = remember { mutableIntStateOf(0) }
    val imageHeight = remember { mutableIntStateOf(0) }
    val imageWidth = remember { mutableIntStateOf(0) }
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // Preview use case
    val preview = remember { Preview.Builder().build() }
    val previewView = remember {
        PreviewView(context).apply {
            // Focus camera on clicked point
            setOnTouchListener { view, motionEvent ->
                val meteringPoint = this.meteringPointFactory.createPoint(motionEvent.x, motionEvent.y)
                camera.value?.cameraControl?.startFocusAndMetering(
                    FocusMeteringAction.Builder(meteringPoint)
                        .build()
                )
                view.performClick()
                false
            }
        }
    }

    // Image capture use case
    val imageCapture = rememberImageCapture()
    val cameraSelector = CameraSelector.Builder()
        .requireLensFacing(lensFacing.intValue)
        .build()
    val isTorchAvailable = remember { mutableStateOf(false) }
    val isTorchEnabled = remember { mutableStateOf(false) }

    // Image analysis use case
    val imageAnalysis = rememberImageAnalysis {
        it.setAnalyzer(executor) { imageProxy ->
            rotationDegrees.intValue = imageProxy.imageInfo.rotationDegrees
            imageHeight.intValue = imageProxy.height
            imageWidth.intValue = imageProxy.width
            // Release the ImageProxy object when the analyse is done
            imageProxy.close()
        }
    }

    // Group use cases
    val useCaseGroup = remember {
        UseCaseGroup.Builder()
            .addUseCase(preview)
            .addUseCase(imageCapture)
            .addUseCase(imageAnalysis)
            .build()
    }

    LaunchedEffect(lensFacing.intValue) {
        camera.value = context.getCameraProvider().let { cameraProvider ->
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, useCaseGroup)
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
        Text(
            text = "Rotation: ${rotationDegrees.intValue}\n" +
                    "Height: ${imageHeight.intValue} / Width: ${imageWidth.intValue}",
            modifier = Modifier.fillMaxWidth(),
            color = Color.Yellow,
            fontSize = MaterialTheme.dimensions.textSizeSmall
        )
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.padding(top = MaterialTheme.dimensions.commonSpaceMedium)
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
                outputDirectory = context.filesDir, // Photo will be saved in \\data\data\{package_name}\files\
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
        Icon(imageVector = Icons.Default.FlipCameraAndroid, contentDescription = "Flip", tint = Color.White)
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
        Icon(imageVector = Icons.Default.ZoomOut, contentDescription = "Zoom out", tint = Color.White)
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
        Icon(imageVector = Icons.Default.ZoomIn, contentDescription = "Zoom in", tint = Color.White)
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
            Icon(imageVector = Icons.Default.FlashlightOff, contentDescription = "Torch Off", tint = Color.White)
        } else {
            Icon(imageVector = Icons.Default.FlashlightOn, contentDescription = "Torch On", tint = Color.White)
        }
    }
}

@Composable
private fun TakePhotoButton(
    outputDirectory: File,
    executor: Executor,
    imageCapture: ImageCapture,
    onImageCaptured: (Uri) -> Unit
) {
    IconButton(
        onClick = {
            takePhoto(
                outputDirectory = outputDirectory,
                imageCapture = imageCapture,
                executor = executor,
                onImageCaptured = onImageCaptured
            )
        },
        modifier = Modifier
            .size(120.dp)
            .padding(bottom = MaterialTheme.dimensions.commonSpaceXLarge)
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
private fun rememberImageCapture() = remember {
    ImageCapture.Builder()
        .setFlashMode(ImageCapture.FLASH_MODE_AUTO)
        .build()
}

@Composable
private fun rememberImageAnalysis(initialize: (ImageAnalysis) -> Unit) = remember {
    ImageAnalysis.Builder()
        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
        .build()
        .apply {
            initialize(this)
        }
}

@Composable
private fun DisplayPhotoView(imageUri: Uri, retry: () -> Unit) {
    var magnifierCenter by remember { mutableStateOf(Offset.Unspecified) }

    Button(
        onClick = retry,
        modifier = Modifier.padding(vertical = MaterialTheme.dimensions.commonSpaceXSmall)
    ) {
        Text(
            text = stringResource(id = R.string.camera_retry_capture_button_label).uppercase(),
            fontSize = MaterialTheme.dimensions.textSizeSmall
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = MaterialTheme.dimensions.commonSpaceMedium)
            .pointerInput(true) {
                detectDragGestures(
                    // Show the magnifier in the initial position
                    onDragStart = { magnifierCenter = it },
                    // Magnifier follows the pointer during a drag event
                    onDrag = { change, _ -> magnifierCenter = change.position },
                    // Hide the magnifier when a user ends the drag movement
                    onDragEnd = { magnifierCenter = Offset.Unspecified },
                    onDragCancel = { magnifierCenter = Offset.Unspecified }
                )
            }
            .magnifier(
                sourceCenter = { magnifierCenter },
                magnifierCenter = { magnifierCenter - Offset(x = 0f, y = MAGNIFIER_Y_OFFSET) },
                zoom = 2f,
                size = DpSize(width = MAGNIFIER_SIZE, height = MAGNIFIER_SIZE),
                cornerRadius = MAGNIFIER_CORNER_RADIUS
            )
    ) {
        Image(
            painter = rememberAsyncImagePainter(
                model = imageUri,
                imageLoader = SingletonImageLoader.get(LocalContext.current)
            ),
            contentDescription = "Photo",
            modifier = Modifier.fillMaxSize()
        )
    }
}
