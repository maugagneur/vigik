package com.kidor.vigik.extensions

import androidx.compose.animation.core.DurationBasedAnimationSpec
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.scale

private const val PULSE_DEFAULT_TARGET_SCALE = 1.5f
private const val PULSE_DEFAULT_BRUSH_ALPHA = 0.45f
private const val PULSE_DEFAULT_DURATION = 1200

/**
 * Applies a pulsating effect that is drawn behind the composable element. This effect creates a visual appearance
 * where the background pulse scales up and fades out in a loop, simulating a pulsating effect.
 *
 * @param targetScale   The scale to which the pulse effect will grow.
 * @param brush         The brush used to fill the pulse effect.
 * @param shape         The shape of the pulse effect.
 * @param animationSpec The animation specification that controls the effect.
 */
@Composable
fun Modifier.pulseEffect(
    targetScale: Float = PULSE_DEFAULT_TARGET_SCALE,
    brush: Brush = SolidColor(MaterialTheme.colorScheme.onBackground.copy(alpha = PULSE_DEFAULT_BRUSH_ALPHA)),
    shape: Shape = CircleShape,
    animationSpec: DurationBasedAnimationSpec<Float> = tween(
        durationMillis = PULSE_DEFAULT_DURATION,
        easing = FastOutSlowInEasing
    )
): Modifier {
    val pulseTransition = rememberInfiniteTransition(label = "PulseTransition")
    val pulseScale by pulseTransition.animateFloat(
        initialValue = 1f,
        targetValue = targetScale,
        animationSpec = infiniteRepeatable(animation = animationSpec),
        label = "PulseScale"
    )
    val pulseAlpha by pulseTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(animationSpec),
        label = "PulseAlpha"
    )
    return this.drawBehind {
        // Convert the shape into an Outline
        minimumInteractiveComponentSize()
        val outline = shape.createOutline(
            size = size,
            layoutDirection = layoutDirection,
            density = this
        )
        // Apply the scale
        scale(scale = pulseScale) {
            // Draw the shape outline
            drawOutline(
                outline = outline,
                brush = brush,
                alpha = pulseAlpha
            )
        }
    }
}

/**
 * Applies a double pulsating effect that is drawn behind the composable element. This effect creates two consecutive
 * pulsations, each with its own timing and easing, giving a layered and dynamic visual appearance.
 *
 * @param targetScale The scale to which the pulse effect will grow.
 * @param brush       The brush used to fill the pulse effect.
 * @param shape       The shape of the pulse effect.
 * @param duration    The total duration for the pulse animation in milliseconds.
 */
@Composable
fun Modifier.doublePulseEffect(
    targetScale: Float = PULSE_DEFAULT_TARGET_SCALE,
    brush: Brush = SolidColor(MaterialTheme.colorScheme.onBackground.copy(alpha = PULSE_DEFAULT_BRUSH_ALPHA)),
    shape: Shape = CircleShape,
    duration: Int = PULSE_DEFAULT_DURATION,
): Modifier = this
    .pulseEffect(
        targetScale = targetScale,
        brush = brush,
        shape = shape,
        animationSpec = tween(
            durationMillis = duration,
            easing = FastOutSlowInEasing
        )
    )
    .pulseEffect(
        targetScale = targetScale,
        brush = brush,
        shape = shape,
        animationSpec = tween(
            durationMillis = (duration * 0.7f).toInt(),
            delayMillis = (duration * 0.3f).toInt(),
            easing = LinearEasing
        )
    )
