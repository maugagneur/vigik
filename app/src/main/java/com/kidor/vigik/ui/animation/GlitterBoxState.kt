package com.kidor.vigik.ui.animation

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color

/**
 * State of [GlitterBox].
 *
 * @param flecks       TODO
 * @param colors       List of colors used when generated new shapes.
 * @param glitterShape TODO
 * @param size         The size of the [GlitterBox].
 * @param speed        TODO
 * @param fleckCount   TODO
 * @param sourceOffset The offset position of the cursor.
 */
data class GlitterBoxState(
    val flecks: List<Fleck> = emptyList(),
    val colors: List<Color>,
    val glitterShape: GlitterShape,
    val size: Size = Size.Zero,
    val speed: Float,
    val fleckCount: Int = 10,
    val sourceOffset: Offset = Offset(0f, 0f)
) {

    private val xVectorRange: IntRange = -100..100
    private val yVectorRange: IntRange = 0..500

    /**
     * Generates new frame of the component.
     *
     * @param durationMillis The time since last frame was generated.
     */
    fun next(durationMillis: Long) {
        flecks.forEach {
            it.next(size, durationMillis, speed)
        }
    }

    companion object {
        /**
         * Updates the size of the component.
         *
         * @param size The new size of the component.
         * @return A [GlitterBoxState] with the new size.
         */
        fun GlitterBoxState.sizeChanged(size: Size): GlitterBoxState {
            if (size == this.size) return this
            return copy(size = size)
        }

        /**
         * Update the glitter source offset.
         *
         * @param source The new source offset.
         * @return A [GlitterBoxState] with the new source offset.
         */
        fun GlitterBoxState.updateSourceOffset(source: Offset): GlitterBoxState {
            if (source == this.sourceOffset) return this
            return copy(
                flecks = flecks.filter { it.lifeCount > 0 } + (0..fleckCount).map {
                    Fleck.create(
                        xVectorRange,
                        yVectorRange,
                        colors = colors,
                        glitterShape = glitterShape,
                        source
                    )
                },
                sourceOffset = source
            )
        }
    }
}
