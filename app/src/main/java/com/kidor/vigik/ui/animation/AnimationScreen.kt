package com.kidor.vigik.ui.animation

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.round
import androidx.compose.ui.unit.toSize
import com.kidor.vigik.R
import com.kidor.vigik.ui.animation.GlitterBoxState.Companion.sizeChanged
import com.kidor.vigik.ui.animation.GlitterBoxState.Companion.updateSource
import com.kidor.vigik.ui.compose.AppTheme
import kotlinx.coroutines.isActive

private const val COLOR_DARK_VIOLET = 0xFF660099
private const val COLOR_ORANGE = 0xFFFF6600
private const val COLOR_PURPLE = 0xFFCC0066

private val vividRainbow = listOf(
    Color(COLOR_DARK_VIOLET),
    Color.Blue,
    Color.Green,
    Color.Yellow,
    Color(COLOR_ORANGE),
    Color(COLOR_PURPLE)
)

/**
 * View that display the section dedicated to Animation.
 */
@Composable
fun AnimationScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        GlitterBox()
        Text(
            text = stringResource(id = R.string.animation_glitter_rainbow_source_label),
            modifier = Modifier.padding(all = AppTheme.dimensions.commonSpaceMedium),
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = AppTheme.dimensions.textSizeXSmall,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun GlitterBox(
    fleckCount: Int = 2, // TODO: customizable
) {
    var size by remember { mutableStateOf(Size.Zero) }
    var source by remember { mutableStateOf(Offset(200f, 200f)) } // TODO: put it in center of screen
    var glitterBoxState by remember {
        mutableStateOf(
            GlitterBoxState(
                speed = 0.5f, // TODO: customizable
                colors = vividRainbow,
                glitterShape = GlitterShape.Mixed,
                fleckCount = fleckCount,
                sourceOffset = source
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
        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            glitterBoxState = glitterBoxState.sizeChanged(this.size)
            for (fleck in glitterBoxState.flecks) {
                fleck.draw(drawContext.canvas)
            }
        }
        Box(
            modifier = Modifier
                .offset { source.round() }
                .size(20.dp)
                .background(MaterialTheme.colorScheme.secondary)
                .pointerInput(Unit) {
                    detectDragGestures { _, dragAmount ->
                        val summed = source + dragAmount
                        val newValue = Offset(
                            x = summed.x.coerceIn(0f, size.width - 10.dp.toPx()),
                            y = summed.y.coerceIn(0f, size.height - 10.dp.toPx())
                        )
                        source = newValue
                        glitterBoxState = glitterBoxState.updateSource(source)
                    }
                }
        )
    }
}
