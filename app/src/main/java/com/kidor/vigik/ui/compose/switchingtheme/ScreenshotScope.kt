package com.kidor.vigik.ui.compose.switchingtheme

import android.view.View
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalView
import com.kidor.vigik.extensions.screenshot

/**
 * Custom [Box] that allow to capture a screenshot of the given [content] through [screenshotState].
 *
 * @param modifier        The modifier to be applied to the layout.
 * @param screenshotState The screenshot state.
 * @param content         The composable content that can be captured.
 */
@Composable
fun ScreenshotScope(modifier: Modifier = Modifier, screenshotState: ScreenshotState, content: @Composable () -> Unit) {
    val view: View = LocalView.current

    var composableBounds by remember {
        mutableStateOf<Rect?>(null)
    }

    DisposableEffect(Unit) {
        screenshotState.callback = {
            composableBounds?.let { bounds ->
                if (bounds.width == 0f || bounds.height == 0f) return@let
                view.screenshot(bounds) {
                    screenshotState.setBitmap(it)
                }
            }
        }

        onDispose {
            val bitmap = screenshotState.bitmap.value
            bitmap?.apply {
                if (!isRecycled) {
                    recycle()
                }
            }
            screenshotState.setBitmap(null)
            screenshotState.callback = null
        }
    }

    Box(
        modifier = modifier.onGloballyPositioned {
            composableBounds = it.boundsInWindow()
        }
    ) {
        content()
    }
}
