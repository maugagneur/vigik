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

/**
 * Class that represents a glitter.
 *
 * @param vector       The glitter's vector.
 * @param glitterColor The glitter's color.
 * @param radius       The glitter's radius.
 * @param shape        The glitter's shape.
 * @param position     The glitter's position.
 */
class Glitter(
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

        position = Offset(
            x = position.x + (speed.x / 1000f * durationMillis),
            y = position.y + (speed.y / 1000f * durationMillis),
        )
        val vx = if (position.x < borderLeft || position.x > borderRight) -vector.x else vector.x
        val vy = if (position.y < borderTop || position.y > borderBottom) -vector.y else vector.y

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
                GeometricShape.Circle -> {
                    canvas.drawCircle(
                        radius = drawRadius,
                        center = position,
                        paint = paint
                    )
                }

                GeometricShape.Triangle -> {
                    val path = Path()
                    path.moveTo(position.x, position.y)
                    path.lineTo(position.x + drawRadius, position.y + 2 * drawRadius)
                    path.lineTo(position.x + 2 * drawRadius, position.y)
                    path.close()
                    canvas.drawPath(
                        path = path,
                        paint = paint
                    )
                }

                GeometricShape.Rectangle -> {
                    val rect = Rect(
                        position.x,
                        position.y,
                        position.x + drawRadius,
                        position.y + drawRadius
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
