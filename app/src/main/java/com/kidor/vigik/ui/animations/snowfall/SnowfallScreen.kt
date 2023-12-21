package com.kidor.vigik.ui.animations.snowfall

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.toSize
import com.kidor.vigik.ui.animations.snowfall.SnowfallBoxState.Companion.sizeChanged
import com.kidor.vigik.ui.animations.snowfall.SnowfallBoxState.Companion.updateSnowflakes
import kotlinx.coroutines.isActive

private const val SNOWFLAKE_NUMBER = 100

/**
 * View that display the section dedicated to snowfall animation.
 */
@Composable
@Preview(widthDp = 400, heightDp = 700)
fun SnowfallScreen() {
    var size by remember { mutableStateOf(Size.Zero) }
    var snowfallBoxState by remember {
        mutableStateOf(
            SnowfallBoxState(
                snowflakes = List(size = SNOWFLAKE_NUMBER) { Snowflake.generateRandom() }
            )
        )
    }

    snowfallBoxState = snowfallBoxState.sizeChanged(size)

    SnowfallBox(
        modifier = Modifier
            .fillMaxSize()
            .onSizeChanged { size = it.toSize() },
        snowfallBoxState = snowfallBoxState
    ) { frameTime ->
        snowfallBoxState = snowfallBoxState.updateSnowflakes(frameTime)
    }
}

@Composable
private fun SnowfallBox(modifier: Modifier, snowfallBoxState: SnowfallBoxState, updateSnowflakes: (Long) -> Unit) {
    LaunchedEffect(Unit) {
        while (isActive) {
            withFrameMillis { newTick ->
                updateSnowflakes(newTick)
            }
        }
    }

    Box(modifier = modifier) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
            snowfallBoxState.snowflakes.forEach { snowflake ->
                snowflake.draw(drawContext.canvas, drawContext.size)
            }
        }
    }
}
