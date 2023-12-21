package com.kidor.vigik.ui.animations.glitterrainbow

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.kidor.vigik.R
import com.kidor.vigik.ui.base.ObserveViewState
import com.kidor.vigik.ui.compose.AppTheme
import kotlin.math.roundToInt

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
 * View that display the section dedicated to glitter rainbow animation.
 */
@Composable
@Preview(widthDp = 400, heightDp = 700)
fun GlitterRainbowScreen(
    viewModel: GlitterRainbowViewModel = hiltViewModel()
) {
    ObserveViewState(viewModel) { state ->
        Column {
            SettingsPanel(
                viewState = state,
                onSpeedCoefficientChanged = {
                    viewModel.handleAction(GlitterRainbowViewAction.ChangeSpeedCoefficient(it))
                },
                onLifeTimeChanged = { viewModel.handleAction(GlitterRainbowViewAction.ChangeLifeTime(it)) }
            )
            Divider()
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.BottomCenter
            ) {
                GlitterBox(
                    colors = vividRainbow,
                    speedCoefficient = state.speedCoefficient,
                    lifeTime = state.lifeTime
                )
                Text(
                    text = stringResource(id = R.string.animation_glitter_rainbow_source_label),
                    modifier = Modifier.padding(all = AppTheme.dimensions.commonSpaceMedium),
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = AppTheme.dimensions.textSizeXSmall,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun SettingsPanel(
    viewState: GlitterRainbowViewState,
    onSpeedCoefficientChanged: (Float) -> Unit,
    onLifeTimeChanged: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .padding(all = AppTheme.dimensions.commonSpaceMedium)
            .height(intrinsicSize = IntrinsicSize.Min)
    ) {
        Column(
            modifier = Modifier
                .padding(end = AppTheme.dimensions.commonSpaceMedium)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            Text(
                text = stringResource(id = R.string.animation_glitter_rainbow_speed_coefficient_label),
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = AppTheme.dimensions.textSizeMedium
            )
            Text(
                text = stringResource(id = R.string.animation_glitter_rainbow_life_time_label),
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = AppTheme.dimensions.textSizeMedium
            )
        }
        var speedCoefficientValue by remember { mutableFloatStateOf(viewState.speedCoefficient) }
        var lifeTimeValue by remember { mutableIntStateOf(viewState.lifeTime) }
        Column(verticalArrangement = Arrangement.SpaceEvenly) {
            Slider(
                value = speedCoefficientValue,
                onValueChange = {
                    speedCoefficientValue = it
                    onSpeedCoefficientChanged(it)
                },
                valueRange = 0.1f..5f
            )
            Slider(
                value = lifeTimeValue.toFloat(),
                onValueChange = { lifeTimeValue = it.roundToInt() },
                valueRange = 10f..500f,
                onValueChangeFinished = { onLifeTimeChanged(lifeTimeValue) }
            )
        }
    }
}
