package com.kidor.vigik.ui.animation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import com.kidor.vigik.R
import com.kidor.vigik.ui.base.ObserveViewState
import com.kidor.vigik.ui.compose.AppTheme

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
fun AnimationScreen(
    viewModel: AnimationViewModel = hiltViewModel()
) {
    ObserveViewState(viewModel) { state ->
        Column {
            Row(
                modifier = Modifier.padding(all = AppTheme.dimensions.commonSpaceMedium),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(id = R.string.animation_speed_coefficient_label),
                    modifier = Modifier.weight(0.5f),
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = AppTheme.dimensions.textSizeMedium
                )
                var speedCoefficientValue by remember { mutableStateOf(state.speedCoefficient) }
                Slider(
                    value = speedCoefficientValue,
                    onValueChange = { speedCoefficientValue = it },
                    modifier = Modifier.weight(0.5f),
                    valueRange = 0.1f..5f,
                    onValueChangeFinished = {
                        viewModel.handleAction(AnimationViewAction.ChangeSpeedCoefficient(speedCoefficientValue))
                    }
                )
            }
            Divider()
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.BottomCenter
            ) {
                GlitterBox(
                    colors = vividRainbow,
                    fleckCount = 2, // TODO: customizable?, rename
                    speedCoefficient = state.speedCoefficient
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
