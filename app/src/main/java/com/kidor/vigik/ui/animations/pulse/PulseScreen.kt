package com.kidor.vigik.ui.animations.pulse

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.rounded.Mic
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kidor.vigik.R
import com.kidor.vigik.extensions.doublePulseEffect
import com.kidor.vigik.extensions.pulseEffect
import com.kidor.vigik.ui.theme.dimensions

private const val INTERNAL_COLOR_OFFSET = 0.6f
private const val EXTERNAL_COLOR_OFFSET = 1f

/**
 * View that displays some pulse animations.
 */
@Preview
@Composable
fun PulseScreen() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            FilledIconButton(
                onClick = { /* Do nothing */ },
                modifier = Modifier
                    .pulseEffect(targetScale = 2f)
                    .size(64.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Mic,
                    contentDescription = null
                )
            }
            FilledIconButton(
                onClick = { /* Do nothing */ },
                modifier = Modifier
                    .doublePulseEffect(
                        targetScale = 2f,
                        brush = Brush.radialGradient(
                            INTERNAL_COLOR_OFFSET to Color.Yellow,
                            EXTERNAL_COLOR_OFFSET to Color.Magenta,
                        )
                    )
                    .size(64.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null
                )
            }
        }
        Button(
            onClick = { /* Do nothing */ },
            modifier = Modifier
                .doublePulseEffect(
                    brush = SolidColor(value = MaterialTheme.colorScheme.primary),
                    shape = ButtonDefaults.shape
                )
                .height(42.dp)
        ) {
            Text(
                text = stringResource(id = R.string.animation_pulse_button_label).uppercase(),
                fontSize = MaterialTheme.dimensions.textSizeMedium,
                textAlign = TextAlign.Center
            )
        }
    }
}
