package com.kidor.vigik.ui.animations.gauge

import androidx.compose.foundation.Canvas
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.util.fastCoerceIn
import com.kidor.vigik.ui.theme.dimensions
import kotlin.math.cos
import kotlin.math.sin

private const val START_ANGLE = 150f
private const val SWEEP_ANGLE = 240f
private const val NEEDLE_LENGTH_RATIO = 0.35f
private const val NEEDLE_BASE_WIDTH_RATIO = 0.04f
private const val DEFAULT_MIN_RANGE_VALUE = 0f
private const val DEFAULT_MAX_RANGE_VALUE = 100f
private const val QUARTER_CIRCLE_ANGLE = 90f

/**
 * Custom component that display a gauge.
 *
 * @param value The gauge's value.
 * @param range The gauge's range.
 */
@Composable
fun Gauge(
    modifier: Modifier = Modifier,
    value: Float,
    range: ClosedFloatingPointRange<Float> = DEFAULT_MIN_RANGE_VALUE..DEFAULT_MAX_RANGE_VALUE
) {
    val needleColor = MaterialTheme.colorScheme.onBackground
    val auraColor = MaterialTheme.colorScheme.secondary
    val trackColor = MaterialTheme.colorScheme.surface
    val progressColors = listOf(
        MaterialTheme.colorScheme.error,
        MaterialTheme.colorScheme.tertiary
    )
    val arcWidth = MaterialTheme.dimensions.commonSpaceLarge

    Canvas(modifier = modifier) {
        val gaugeValue = value.fastCoerceIn(range.start, range.endInclusive)
        val fillSwipeAngle = ((gaugeValue - range.start) / (range.endInclusive - range.start)) * SWEEP_ANGLE

        // Track
        drawArc(
            color = trackColor,
            startAngle = START_ANGLE,
            sweepAngle = SWEEP_ANGLE,
            useCenter = false,
            style = Stroke(
                width = arcWidth.toPx(),
                cap = StrokeCap.Round
            )
        )

        // Gauge value
        drawArc(
            brush = Brush.horizontalGradient(colors = progressColors),
            startAngle = START_ANGLE,
            sweepAngle = fillSwipeAngle,
            useCenter = false,
            style = Stroke(
                width = arcWidth.toPx(),
                cap = StrokeCap.Round
            )
        )

        drawNeedle(
            scope = this,
            size = size,
            needleColor = needleColor,
            auraColor = auraColor,
            angle = fillSwipeAngle
        )
    }
}

/**
 * Draws the needle of the gauge.
 *
 * @param scope       The [DrawScope] that will perform drawing.
 * @param size        The size of the gauge.
 * @param needleColor The color of the needle.
 * @param auraColor   The color of the aura in background.
 * @param angle       The angle of the needle.
 */
private fun drawNeedle(
    scope: DrawScope,
    size: Size,
    needleColor: Color,
    auraColor: Color,
    angle: Float
) {
    val height = size.height
    val width = size.width
    val centerOffset = Offset(
        x = width / 2f,
        y = height / 2f
    )

    // Background aura
    scope.drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                auraColor.copy(alpha = 0.25f),
                Color.Transparent
            )
        ),
        radius = width / 2f
    )

    // Center circle
    scope.drawCircle(
        color = needleColor,
        radius = size.minDimension * 0.07f,
        center = centerOffset
    )

    val needleAngle = angle + START_ANGLE
    val needleLength = size.minDimension * NEEDLE_LENGTH_RATIO

    val needlePath = Path().apply {
        // Calculate the top point of the needle
        val topX = centerOffset.x + needleLength * cos(Math.toRadians(needleAngle.toDouble())).toFloat()
        val topY = centerOffset.y + needleLength * sin(Math.toRadians(needleAngle.toDouble())).toFloat()

        // Calculate the base points of the needle
        val needleBaseWidth = size.minDimension * NEEDLE_BASE_WIDTH_RATIO
        val baseLeftX = centerOffset.x + needleBaseWidth * cosFromDegrees(angle = needleAngle - QUARTER_CIRCLE_ANGLE)
        val baseLeftY = centerOffset.y + needleBaseWidth * sinFromDegrees(angle = needleAngle - QUARTER_CIRCLE_ANGLE)
        val baseRightX = centerOffset.x + needleBaseWidth * cosFromDegrees(angle = needleAngle + QUARTER_CIRCLE_ANGLE)
        val baseRightY = centerOffset.y + needleBaseWidth * sinFromDegrees(angle = needleAngle + QUARTER_CIRCLE_ANGLE)

        moveTo(x = topX, y = topY)
        lineTo(x = baseLeftX, y = baseLeftY)
        lineTo(x = baseRightX, y = baseRightY)
        close()
    }

    scope.drawPath(
        path = needlePath,
        color = needleColor
    )
}

/**
 * Computes the cosine of the angle given in degrees.
 */
private fun cosFromDegrees(angle: Float): Float = cos(Math.toRadians(angle.toDouble())).toFloat()

/**
 * Computes the sine of the angle given in degrees.
 */
private fun sinFromDegrees(angle: Float): Float = sin(Math.toRadians(angle.toDouble())).toFloat()
