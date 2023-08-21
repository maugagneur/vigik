package com.kidor.vigik.ui.animation

import android.content.res.Configuration
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
import com.kidor.vigik.ui.animation.GlitterBoxState.Companion.updateSpeedCoefficient
import kotlinx.coroutines.isActive

private const val CURSOR_HORIZONTAL_OFFSET_RATIO = 2f
private const val CURSOR_VERTICAL_OFFSET_RATIO_LANDSCAPE = 4f
private const val CURSOR_VERTICAL_OFFSET_RATIO_PORTRAIT = 3f

/**
 * Component that display a cursor in a box that produces glitters when dragged.
 *
 * @param colors           The colors used to draw glitters.
 * @param fleckCount       The glitter's generation frequency.
 * @param speedCoefficient The glitter's speed coefficient.
 */
@Composable
fun GlitterBox(colors: List<Color>, fleckCount: Int, speedCoefficient: Float) {
    var size by remember { mutableStateOf(Size.Zero) }
    var glitterBoxState by remember {
        mutableStateOf(
            GlitterBoxState(
                colors = colors,
                glitterShape = Mixed,
                speedCoefficient = speedCoefficient,
                fleckCount = fleckCount
            )
        )
    }
    var lastFrame by remember { mutableStateOf(-1L) }

    glitterBoxState = glitterBoxState.updateSpeedCoefficient(speedCoefficient)

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
        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            glitterBoxState = glitterBoxState.sizeChanged(this.size)
            for (glitter in glitterBoxState.glitters) {
                glitter.draw(drawContext.canvas)
            }
        }

        if (size != Size.Zero) {
            GlitterSource(size = glitterBoxState.size) {
                glitterBoxState = glitterBoxState.updateSourceOffset(it)
            }
        }
    }
}

@Composable
private fun GlitterSource(size: Size, updateSourceOffset: (Offset) -> Unit) {
    val sourceColor = MaterialTheme.colorScheme.secondary
    val sourceRadius = 16.dp
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current
    val initialXOffset = with(density) {
        configuration.screenWidthDp.dp.roundToPx() / CURSOR_HORIZONTAL_OFFSET_RATIO
    }
    val initialYOffset = with(density) {
        if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            configuration.screenHeightDp.dp.roundToPx() / CURSOR_VERTICAL_OFFSET_RATIO_LANDSCAPE
        } else {
            configuration.screenHeightDp.dp.roundToPx() / CURSOR_VERTICAL_OFFSET_RATIO_PORTRAIT
        }
    }
    var sourceOffset by remember { mutableStateOf(Offset(initialXOffset, initialYOffset)) }

    Canvas(
        modifier = Modifier
            .offset { sourceOffset.round() }
            .pointerInput(Unit) {
                detectDragGestures { _, dragAmount ->
                    val offsetAfterGesture = sourceOffset + dragAmount
                    val newOffsetValue = Offset(
                        x = offsetAfterGesture.x.coerceIn(sourceRadius.toPx(), size.width - sourceRadius.toPx()),
                        y = offsetAfterGesture.y.coerceIn(sourceRadius.toPx(), size.height - sourceRadius.toPx())
                    )
                    sourceOffset = newOffsetValue
                    updateSourceOffset(newOffsetValue)
                }
            }
    ) {
        drawCircle(
            color = sourceColor,
            radius = sourceRadius.toPx()
        )
    }
}
