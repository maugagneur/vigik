package com.kidor.vigik.ui.animations.gauge

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.size
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import com.kidor.vigik.ui.theme.dimensions
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val MIN_DELAY_MILLIS = 20L
private const val MAX_DELAY_MILLIS = 600L
private const val DELAY_FACTOR = 0.22f

/**
 * Custom [FilledIconButton] which repeats its [onClick] callback when maintained.
 *
 * @param onClick  Called when this icon button is clicked and when the click is maintained.
 * @param modifier The [Modifier] to be applied to this icon button
 * @param content  The content of this icon button, typically an icon.
 */
@Composable
fun RepeatableFilledIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable (() -> Unit)
) {
    val clickAction by rememberUpdatedState(onClick)
    val scope = rememberCoroutineScope()

    FilledIconButton(
        onClick = { },
        modifier = modifier
            .size(MaterialTheme.dimensions.commonSpaceXXLarge)
            .pointerInput(Unit) {
                scope.launch {
                    awaitEachGesture {
                        val down = awaitFirstDown(requireUnconsumed = false)
                        val heldButtonJob = launch {
                            var currentDelay = MAX_DELAY_MILLIS
                            while (down.pressed) {
                                clickAction()
                                delay(currentDelay)
                                val nextDelay = currentDelay - (currentDelay * DELAY_FACTOR)
                                currentDelay = nextDelay.toLong().coerceAtLeast(MIN_DELAY_MILLIS)
                            }
                        }
                        waitForUpOrCancellation()
                        heldButtonJob.cancel()
                    }
                }
            }
    ) {
        content()
    }
}
