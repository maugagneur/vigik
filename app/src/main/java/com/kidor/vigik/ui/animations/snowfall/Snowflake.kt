package com.kidor.vigik.ui.animations.snowfall

import androidx.annotation.VisibleForTesting
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PaintingStyle
import kotlin.random.Random

/**
 * Object model of a snowflake on screen.
 *
 * @param position Snowflake's position.
 * @param radius   Snowflake's size.
 * @param speed    Snowflake's falling speed.
 */
class Snowflake private constructor(
    @get:VisibleForTesting internal var position: Offset,
    @get:VisibleForTesting internal var radius: Float,
    @get:VisibleForTesting internal var speed: Float
) {
    /**
     * Calculates the next snowflake's position.
     *
     * @param container      The current size of the drawing environment.
     * @param durationMillis The elapsed time since previous frame.
     */
    fun next(container: Size, durationMillis: Long) {
        position = Offset(
            x = position.x,
            y = (position.y + speed * durationMillis) % container.height,
        )
    }

    /**
     * Draw snowflake.
     *
     * @param canvas    The [Canvas] where the shape of the snowflake will be draw.
     * @param container The current size of the drawing environment.
     */
    fun draw(canvas: Canvas, container: Size) {
        canvas.drawCircle(
            center = Offset(x = position.x * container.width, y = position.y),
            radius = radius,
            paint = paint
        )
    }

    companion object {
        private val paint: Paint = Paint().apply {
            isAntiAlias = true
            color = Color.White
            style = PaintingStyle.Fill
        }

        /**
         * Generates a [Snowflake] with random values.
         */
        fun generateRandom(): Snowflake = Snowflake(
            position = Offset(
                x = Random.nextFloat(),
                y = Random.nextFloat() * 1000f
            ),
            radius = Random.nextFloat() * 2f + 2f,
            speed = Random.nextFloat() * 0.15f + 0.12f
        )
    }
}
