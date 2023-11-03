package com.kidor.vigik.ui.compose.switchingtheme

import android.graphics.Bitmap
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

/**
 * Compose state to capture and hold the [Bitmap] of the screenshot.
 */
class ScreenshotState {

    private val _bitmap = mutableStateOf<Bitmap?>(null)

    /**
     * The captured [Bitmap]'s state.
     */
    val bitmap: State<Bitmap?> = _bitmap

    /**
     * The callback triggered when trying to capture a screenshot.
     */
    var callback: (() -> Unit)? = null

    /**
     * Holds the result of the capture.
     *
     * @param bitmap The captured [Bitmap] of the screen.
     */
    fun setBitmap(bitmap: Bitmap?) {
        _bitmap.value = bitmap
    }

    /**
     * Request to capture an image of the screen.
     */
    fun capture() {
        callback?.invoke()
    }
}

/**
 * Remember then return a [ScreenshotState] bound to this point in the composition.
 */
@Composable
fun rememberScreenshotState() = remember {
    ScreenshotState()
}
