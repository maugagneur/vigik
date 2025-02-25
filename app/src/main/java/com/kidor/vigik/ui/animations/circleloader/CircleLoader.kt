@file:ExcludedFromKoverReport

package com.kidor.vigik.ui.animations.circleloader

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.toRect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.rotate
import com.kidor.vigik.utils.ExcludedFromKoverReport
import kotlin.math.roundToInt

private const val FULL_CIRCLE_DEGREES = 360f
private const val HALF_CIRCLE_DEGREES = FULL_CIRCLE_DEGREES / 2

/**
 * Custom circle loader.
 *
 * @param modifier      Modifier to be applied to the loader.
 * @param color         Primary color of the loader.
 * @param secondColor   Secondary color of the loader (optional).
 * @param tailLength    Length of the loader's tail in degrees.
 * @param tailAnimation True to add an variation to tail's length, otherwise false.
 * @param strokeStyle   Style of the stroke used in drawing the loader.
 * @param cycleDuration Loader’s animation cycle duration, measured in milliseconds.
 */
@Suppress("LongParameterList")
@Composable
fun CircleLoader(
    modifier: Modifier,
    color: Color,
    secondColor: Color? = null,
    tailLength: Float = 140f,
    tailAnimation: Boolean = false,
    strokeStyle: StrokeStyle = StrokeStyle(),
    cycleDuration: Int = 1400
) {
    // Rotation animation
    val transition = rememberInfiniteTransition(label = "circle_loader_transition")
    val spinAngel by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = cycleDuration,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "circle_loader_spin_animation"
    )

    // Tail's length
    val tailToDisplay: State<Float> = if (tailAnimation) {
         transition.animateFloat(
            initialValue = 0f,
            targetValue = tailLength,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = (cycleDuration * 1.5f).roundToInt(),
                    easing = LinearEasing
                ),
                repeatMode = RepeatMode.Reverse
            ),
            label = "circle_loader_tail_length_animation"
        )
    } else {
        remember { mutableFloatStateOf(tailLength) }
    }

    Canvas(
        modifier = modifier
            .rotate(degrees = spinAngel)
            .aspectRatio(1f)
    ) {
        // Iterate over non-null colors
        listOfNotNull(color, secondColor).forEachIndexed { index, color ->
            // If it's not a primary color we rotate the canvas for 180 degrees
            rotate(if (index == 0) 0f else HALF_CIRCLE_DEGREES) {
                // Create a sweep gradient brush for the loader
                val brush = Brush.sweepGradient(
                    0f to Color.Transparent,
                    tailToDisplay.value / FULL_CIRCLE_DEGREES to color,
                    1f to Color.Transparent
                )

                // Set up paint object
                val paint = setupPaint(
                    strokeStyle = strokeStyle,
                    brush = brush
                )

                // Draw the loader tail
                drawIntoCanvas { canvas ->
                    canvas.drawArc(
                        rect = size.toRect(),
                        startAngle = 0f,
                        sweepAngle = tailToDisplay.value,
                        useCenter = false,
                        paint = paint
                    )
                }
            }
        }
    }
}

/**
 * Configures the [Paint] used to draw the tails of the loader.
 *
 * @param strokeStyle Style of loader's tails.
 * @param brush       Brush to apply to loader's tails.
 */
private fun DrawScope.setupPaint(strokeStyle: StrokeStyle, brush: Brush): Paint {
    val paint = Paint().apply {
        isAntiAlias = true
        style = PaintingStyle.Stroke
        strokeWidth = strokeStyle.width.toPx()
        strokeCap = strokeStyle.strokeCap

        brush.applyTo(size, this, 1f)
    }

    strokeStyle.glowRadius?.let { radius ->
        paint.asFrameworkPaint().setShadowLayer(
            radius.toPx(),
            0f,
            0f,
            android.graphics.Color.WHITE
        )
    }

    return paint
}
