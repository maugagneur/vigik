package com.kidor.vigik.ui.camera

import android.Manifest
import android.net.Uri
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
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
import androidx.compose.material.icons.filled.FlipCameraAndroid
import androidx.compose.material.icons.filled.Lens
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.kidor.vigik.ui.home.HomeButtonData
import timber.log.Timber
import java.io.File
import java.util.concurrent.Executor
import java.util.concurrent.Executors

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
                            HomeButtonData(R.string.camera_retry_capture_button_label) {
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
private fun CameraView(
    executor: Executor,
    onImageCaptured: (Uri) -> Unit
) {
    val lensFacing = remember { mutableStateOf(CameraSelector.LENS_FACING_BACK) }
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
        .requireLensFacing(lensFacing.value)
        .build()

    LaunchedEffect(lensFacing) {
        context.getCameraProvider().let { cameraProvider ->
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, preview, imageCapture)
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
                IconButton(
                    onClick = {
                        lensFacing.value = if (lensFacing.value == CameraSelector.LENS_FACING_BACK) {
                            CameraSelector.LENS_FACING_FRONT
                        } else {
                            CameraSelector.LENS_FACING_BACK
                        }
                    }
                ) {
                    Icon(imageVector = Icons.Default.FlipCameraAndroid, contentDescription = "Flip")
                }
            }
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
private fun ButtonBar(buttons: List<HomeButtonData>) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = AppTheme.dimensions.commonSpaceMedium),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        buttons.forEach { data ->
            Button(onClick = data.onClick) {
                Text(
                    text = stringResource(id = data.textId).uppercase(),
                    fontSize = AppTheme.dimensions.textSizeSmall
                )
            }
        }
    }
}
