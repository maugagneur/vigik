package com.kidor.vigik.ui.animation

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.round
import androidx.compose.ui.unit.toSize
import com.kidor.vigik.ui.animation.GlitterBoxState.Companion.sizeChanged
import com.kidor.vigik.ui.animation.GlitterBoxState.Companion.updateSourceOffset
import kotlinx.coroutines.isActive

private const val CURSOR_HORIZONTAL_OFFSET_RATIO = 2f
private const val CURSOR_VERTICAL_OFFSET_RATIO = 2.5f

/**
 * Component that display a cursor in a box that produces glitters when dragged.
 *
 * @param colors     The colors used to draw glitters.
 * @param fleckCount The glitter's generation frequency.
 * @param speed      The glitter's velocity.
 */
@Composable
fun GlitterBox(colors: List<Color>, fleckCount: Int, speed: Float) {
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current
    val initialXOffset = with(density) {
        configuration.screenWidthDp.dp.roundToPx() / CURSOR_HORIZONTAL_OFFSET_RATIO
    }
    val initialYOffset = with(density) {
        configuration.screenHeightDp.dp.roundToPx() / CURSOR_VERTICAL_OFFSET_RATIO
    }
    var size by remember { mutableStateOf(Size.Zero) }
    var cursorOffset by remember { mutableStateOf(Offset(initialXOffset, initialYOffset)) }
    var glitterBoxState by remember {
        mutableStateOf(
            GlitterBoxState(
                speed = speed,
                colors = colors,
                glitterShape = GlitterShape.Mixed,
                fleckCount = fleckCount,
                sourceOffset = cursorOffset
            )
        )
    }
    var lastFrame by remember { mutableStateOf(-1L) }

    LaunchedEffect(true) {
        while (isActive) {
            withFrameMillis { newTick ->
                val elapsedMillis = newTick - lastFrame
                val wasFirstFrame = lastFrame < 0
                lastFrame = newTick
                if (wasFirstFrame) return@withFrameMillis
                glitterBoxState.next(elapsedMillis)
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .onSizeChanged { size = it.toSize() }
    ) {
        // Glitters
        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            glitterBoxState = glitterBoxState.sizeChanged(this.size)
            for (fleck in glitterBoxState.flecks) {
                fleck.draw(drawContext.canvas)
            }
        }

        // Cursor = glitter source
        val cursorColor = MaterialTheme.colorScheme.secondary
        val cursorRadius = 16.dp
        Canvas(
            modifier = Modifier
                .offset { cursorOffset.round() }
                .pointerInput(Unit) {
                    detectDragGestures { _, dragAmount ->
                        val offsetAfterGesture = cursorOffset + dragAmount
                        val newOffsetValue = Offset(
                            x = offsetAfterGesture.x.coerceIn(cursorRadius.toPx(), size.width - cursorRadius.toPx()),
                            y = offsetAfterGesture.y.coerceIn(cursorRadius.toPx(), size.height - cursorRadius.toPx())
                        )
                        cursorOffset = newOffsetValue
                        glitterBoxState = glitterBoxState.updateSourceOffset(cursorOffset)
                    }
                }
        ) {
            drawCircle(
                color = cursorColor,
                radius = cursorRadius.toPx()
            )
        }
    }
}
