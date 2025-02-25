package com.kidor.vigik.ui.theme.switchingtheme

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import com.kidor.vigik.utils.ExcludedFromKoverReport

/**
 * Rectangular [Shape] with a removal visual effect.
 *
 * @param offset    The offset of the removal effect.
 * @param direction The direction of the removal effect.
 */
@ExcludedFromKoverReport
class RemovableDiagonalRectShape(private val offset: Float, private val direction: ShapeDirection) : Shape {

    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline {
        return Outline.Generic(
            path = drawPath(size, offset, direction)
        )
    }

    private fun drawPath(size: Size, offset: Float, direction: ShapeDirection): Path {
        return Path().apply {
            when (direction) {
                ShapeDirection.FROM_RIGHT_TO_LEFT -> {
                    reset()
                    lineTo(x = 0f, y = 0f)
                    lineTo(x = 0f, y = size.height)
                    lineTo(x = offset * 1.5f, y = size.height)
                    lineTo(x = offset * 1f, y = 0f)
                    close()
                }
                ShapeDirection.FROM_LEFT_TO_RIGHT -> {
                    reset()
                    lineTo(x = size.width, y = 0f)
                    lineTo(x = size.width, y = size.height)
                    lineTo(x = offset * 1.5f, y = size.height)
                    lineTo(x = offset * 1f, y = 0f)
                    close()
                }
            }
        }
    }
}
