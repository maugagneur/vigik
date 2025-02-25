@file:ExcludedFromKoverReport

package com.kidor.vigik.ui.snackbar

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarDefaults
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarVisuals
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kidor.vigik.ui.theme.AppTheme
import com.kidor.vigik.ui.theme.dimensions
import com.kidor.vigik.utils.ExcludedFromKoverReport
import kotlinx.coroutines.delay

private const val REFRESH_PERIOD = 40L // 40ms means 25 FPS which is quite smooth for the human eye

/**
 * Custom SnackBar with a countdown.
 *
 * @param snackbarData     Data for the snackbar.
 * @param modifier         Modifier to be applied to the snackbar.
 * @param durationInMillis Duration of the countdown timer in millis.
 * @param actionOnNewLine  Whether to display the action on a separate line.
 * @param shape            The shape of the snackbar’s container.
 * @param containerColor   Color used for the background of this snackbar. Use [Color.Transparent] to have no color.
 * @param contentColor     The preferred color for content inside this snackbar.
 * @param actionColor      The preferred content color for the optional action inside this snackbar.
 */
@Suppress("LongParameterList")
@Composable
fun CountdownSnackbar(
    snackbarData: SnackbarData,
    modifier: Modifier = Modifier,
    durationInMillis: Long = 5_000L,
    actionOnNewLine: Boolean = false,
    shape: Shape = SnackbarDefaults.shape,
    containerColor: Color = SnackbarDefaults.color,
    contentColor: Color = SnackbarDefaults.contentColor,
    actionColor: Color = SnackbarDefaults.actionColor
) {
    var millisRemaining by remember { mutableLongStateOf(durationInMillis) }

    LaunchedEffect(snackbarData) {
        while (millisRemaining > 0) {
            delay(REFRESH_PERIOD)
            millisRemaining -= REFRESH_PERIOD
        }
        snackbarData.dismiss()
    }

    val actionLabel = snackbarData.visuals.actionLabel
    val actionComposable: (@Composable () -> Unit)? = actionLabel?.let {
        {
            TextButton(onClick = { snackbarData.performAction() }) {
                Text(
                    text = actionLabel.uppercase(),
                    color = actionColor
                )
            }
        }
    }
    val dismissActionComposable: (@Composable () -> Unit)? = if (snackbarData.visuals.withDismissAction) {
        {
            IconButton(onClick = { snackbarData.dismiss() }) {
                Icon(
                    imageVector = Icons.Rounded.Close,
                    contentDescription = null,
                    tint = contentColor
                )
            }
        }
    } else {
        null
    }

    Snackbar(
        modifier = modifier,
        action = actionComposable,
        dismissAction = dismissActionComposable,
        actionOnNewLine = actionOnNewLine,
        shape = shape,
        containerColor = containerColor,
        contentColor = contentColor,
        actionContentColor = actionColor
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.dimensions.commonSpaceMedium),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SnackBarCountDown(
                timerProgress = millisRemaining.toFloat() / durationInMillis.toFloat(),
                secondsRemaining = (millisRemaining.toInt() / 1000) + 1,
                color = contentColor
            )
            Text(
                text = snackbarData.visuals.message,
                color = contentColor
            )
        }
    }
}

/**
 * Display a custom countdown timer.
 *
 * @param timerProgress    The countdown progress.
 * @param secondsRemaining The remaining time in seconds.
 * @param color            The color to apply to the countdown.
 */
@Composable
private fun SnackBarCountDown(
    timerProgress: Float,
    secondsRemaining: Int,
    color: Color
) {
    Box(
        modifier = Modifier.size(MaterialTheme.dimensions.commonSpaceLarge),
        contentAlignment = Alignment.Center
    ) {
        // Display the progress
        Canvas(modifier = Modifier.matchParentSize()) {
            // Define the stroke
            val strokeStyle = Stroke(
                width = 2.dp.toPx(),
                cap = StrokeCap.Round
            )
            // Draw the track
            drawCircle(
                color = color.copy(alpha = 0.12f),
                style = strokeStyle
            )
            // Draw the progress
            drawArc(
                color = color,
                startAngle = -90f,
                sweepAngle = (-360f * timerProgress),
                useCenter = false,
                style = strokeStyle
            )
        }
        // Display the remaining seconds
        Text(
            text = secondsRemaining.toString(),
            color = color,
            fontSize = MaterialTheme.dimensions.textSizeSmall
        )
    }
}

@Preview
@Composable
private fun CountdownSnackbarPreview() {
    val data: SnackbarData = object : SnackbarData {
        override val visuals: SnackbarVisuals
            get() = object : SnackbarVisuals {
                override val actionLabel: String = "Undo"
                override val duration: SnackbarDuration = SnackbarDuration.Indefinite
                override val message: String = "Stupid thing done"
                override val withDismissAction: Boolean = true
            }
        override fun dismiss() { /* Do nothing */ }
        override fun performAction() { /* Do nothing */ }
    }
    CountdownSnackbar(
        snackbarData = data
    )
}

@Preview
@Composable
private fun CountdownSnackbarPreviewCustomColors() {
    val data: SnackbarData = object : SnackbarData {
        override val visuals: SnackbarVisuals
            get() = object : SnackbarVisuals {
                override val actionLabel: String = "Undo"
                override val duration: SnackbarDuration = SnackbarDuration.Indefinite
                override val message: String = "Stupid thing done"
                override val withDismissAction: Boolean = true
            }
        override fun dismiss() { /* Do nothing */ }
        override fun performAction() { /* Do nothing */ }
    }
    AppTheme {
        CountdownSnackbar(
            snackbarData = data,
            shape = RoundedCornerShape(size = MaterialTheme.dimensions.commonSpaceMedium),
            contentColor = MaterialTheme.colorScheme.onPrimary,
            containerColor = MaterialTheme.colorScheme.primary,
            actionColor = MaterialTheme.colorScheme.secondary
        )
    }
}
