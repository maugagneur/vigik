package com.kidor.vigik.ui.animation

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color

/**
 * State of [GlitterBox].
 *
 * @param glitters         The list of glitters in the box.
 * @param colors           List of colors used when generated new shapes.
 * @param glitterShape     The type of shape available for glitters.
 * @param size             The size of the [GlitterBox].
 * @param speedCoefficient The glitter's speed coefficient.
 * @param fleckCount       TODO
 * @param sourceOffset     The offset position of the cursor.
 */
data class GlitterBoxState(
    val glitters: List<Glitter> = emptyList(),
    val colors: List<Color>,
    val glitterShape: GlitterShape,
    val size: Size = Size.Zero,
    val speedCoefficient: Float,
    val fleckCount: Int = 10,
    val sourceOffset: Offset = Offset(0f, 0f)
) {

    /**
     * Generates new frame of the component.
     *
     * @param durationMillis The time since last frame was generated.
     */
    fun next(durationMillis: Long) {
        glitters.forEach {
            it.next(size, durationMillis, speedCoefficient)
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
                glitters = glitters.filter { it.lifeCount > 0 } + (0..fleckCount).map {
                    Glitter.create(
                        color = colors.random(),
                        glitterShape = glitterShape,
                        source = source
                    )
                },
                sourceOffset = source
            )
        }

        /**
         * Update the glitter speed coefficient if needed.
         *
         * @param speedCoefficient The new speed coefficient.
         * @return A [GlitterBoxState] with the new speed coefficient.
         */
        fun GlitterBoxState.updateSpeedCoefficient(speedCoefficient: Float): GlitterBoxState {
            if (speedCoefficient == this.speedCoefficient) return this
            return copy(speedCoefficient = speedCoefficient)
        }
    }
}
