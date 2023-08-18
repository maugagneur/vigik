package com.kidor.vigik.ui.animation

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

class Fleck(
    vector: Offset,
    private val glitterColor: Color,
    private val radius: Float,
    private val shape: GlitterShape = GlitterShape.Circle,
    position: Offset
) {
    var lifeCount: Int = MAX_LIFE
        private set
    private var drawRadius = radius
    private var position by mutableStateOf(position)
    private var vector by mutableStateOf(vector)
    private val paint: Paint = Paint().apply {
        isAntiAlias = true
        color = glitterColor
        style = PaintingStyle.Fill
    }

    fun next(
        borders: Size,
        durationMillis: Long,
        speedCoefficient: Float
    ) {
        lifeCount -= 1
        if (lifeCount <= 0) lifeCount = 0
        drawRadius = radius * lifeCount / MAX_LIFE
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
     * Draw glitter shape.
     *
     * @param canvas The [Canvas] where the shape will be draw.
     */
    fun draw(canvas: Canvas) {
        if (lifeCount > 0) {
            if (shape == GlitterShape.Circle) {
                canvas.drawCircle(
                    radius = drawRadius,
                    center = position,
                    paint = paint
                )
            } else {
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

    companion object {
        private const val MAX_LIFE = 100 // TODO: customizable
        private val radiusRange = (5..25)

        fun create(
            xVectorRange: IntRange,
            yVectorRange: IntRange,
            colors: List<Color>,
            glitterShape: GlitterShape,
            source: Offset = Offset(0f, 0f)
        ): Fleck {
            val shape = if (glitterShape == GlitterShape.Mixed) {
                if ((0..1).random() == 0) GlitterShape.Circle else GlitterShape.Rectangle
            } else {
                glitterShape
            }
            return Fleck(
                position = source,
                vector = Offset(
                    x = listOf(-1f, 1f).random() * xVectorRange.random().toFloat(),
                    y = yVectorRange.random().toFloat()
                ),
                glitterColor = colors.random(),
                radius = radiusRange.random().toFloat(),
                shape = shape
            )
        }
    }
}
