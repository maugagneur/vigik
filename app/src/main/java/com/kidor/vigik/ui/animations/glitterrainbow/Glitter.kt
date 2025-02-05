package com.kidor.vigik.ui.animations.glitterrainbow

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.graphics.Path
import kotlin.math.sqrt

/**
 * Class that represents a glitter.
 *
 * @param vector       The glitter's vector.
 * @param glitterColor The glitter's color.
 * @param radius       The glitter's radius.
 * @param shape        The glitter's shape.
 * @param position     The glitter's position.
 */
class Glitter private constructor(
    vector: Offset,
    private val glitterColor: Color,
    private val radius: Float,
    private val shape: GeometricShape,
    position: Offset,
    private val lifeTime: Int
) {
    /**
     * The number of glitter's frame until it disappears.
     */
    var lifeCount: Int = lifeTime
        private set
    private var drawRadius = radius
    private var position by mutableStateOf(position)
    private var vector by mutableStateOf(vector)
    private val paint: Paint = Paint().apply {
        isAntiAlias = true
        color = glitterColor
        style = PaintingStyle.Fill
    }

    /**
     * Calculates the next glitter's position.
     *
     * @param borders          The size of the area where glitter can exist.
     * @param durationMillis   The elapsed time since previous frame.
     * @param speedCoefficient The glitter's speed coefficient.
     */
    fun next(
        borders: Size,
        durationMillis: Long,
        speedCoefficient: Float
    ) {
        lifeCount -= 1
        if (lifeCount <= 0) lifeCount = 0
        drawRadius = radius * lifeCount / lifeTime
        val speed = vector * speedCoefficient
        val borderTop = 0
        val borderLeft = 0
        val borderBottom = borders.height
        val borderRight = borders.width

        // Compute next position
        var nextPosition = Offset(
            x = position.x + (speed.x / 1000f * durationMillis),
            y = position.y + (speed.y / 1000f * durationMillis),
        )

        // If the next position is out of border, inverse vector and actualize the next position
        val vx = if ((nextPosition.x - drawRadius) < borderLeft || (nextPosition.x + drawRadius) > borderRight) {
            nextPosition = nextPosition.copy(x = position.x - (speed.x / 1000f * durationMillis))
            -vector.x
        } else {
            vector.x
        }
        val vy = if ((nextPosition.y - drawRadius) < borderTop || (nextPosition.y + drawRadius) > borderBottom) {
            nextPosition = nextPosition.copy(y = position.y - (speed.y / 1000f * durationMillis))
            -vector.y
        } else {
            vector.y
        }

        // Update position and vector if needed
        position = nextPosition
        if (vx != vector.x || vy != vector.y) {
            vector = Offset(vx, vy)
        }
    }

    /**
     * Draw glitter.
     *
     * @param canvas The [Canvas] where the shape of the glitter will be draw.
     */
    fun draw(canvas: Canvas) {
        if (lifeCount > 0) {
            when (shape) {
                GeometricShape.CIRCLE -> {
                    canvas.drawCircle(
                        radius = drawRadius,
                        center = position,
                        paint = paint
                    )
                }

                GeometricShape.TRIANGLE -> {
                    val path = Path()
                    path.moveTo(position.x - drawRadius * cos30, position.y - drawRadius / 2f)
                    path.lineTo(position.x + drawRadius * cos30, position.y - drawRadius / 2f)
                    path.lineTo(position.x, position.y + drawRadius)
                    path.close()
                    canvas.drawPath(
                        path = path,
                        paint = paint
                    )
                }

                GeometricShape.RECTANGLE -> {
                    val offset: Float = drawRadius / sqrt(2f)
                    val rect = Rect(
                        position.x - offset,
                        position.y - offset,
                        position.x + offset,
                        position.y + offset
                    )
                    canvas.drawRect(
                        rect = rect,
                        paint = paint
                    )
                }
            }
        }
    }

    companion object {
        private val xVectorRange: IntRange = -100..100
        private val yVectorRange: IntRange = 0..500
        private val radiusRange = (5..25)
        private val cos30: Float = sqrt(3f) / 2f // cos(π/6) value

        /**
         * Creates a glitter with given parameters.
         *
         * @param color        The glitter color.
         * @param glitterShape The glitter shape.
         * @param source       The initial position of the glitter.
         * @param lifeTime     The life time of the glitter.
         */
        fun create(color: Color, glitterShape: GlitterShape, source: Offset, lifeTime: Int): Glitter {
            val shape: GeometricShape = if (glitterShape is GeometricShape) {
                glitterShape
            } else {
                GeometricShape.entries.random()
            }
            return Glitter(
                position = source,
                vector = Offset(
                    x = xVectorRange.random().toFloat(),
                    y = yVectorRange.random().toFloat()
                ),
                glitterColor = color,
                radius = radiusRange.random().toFloat(),
                shape = shape,
                lifeTime = lifeTime
            )
        }
    }
}
