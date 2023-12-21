package com.kidor.vigik.ui.animations.snowfall

import androidx.compose.ui.geometry.Size
import timber.log.Timber

/**
 * State of the component that produce and display snowflakes over time.
 *
 * @param size          The size of the the box.
 * @param snowflakes    The list of snowflakes in the box.
 * @param lastFrameTime The last time the snowflakes were draw.
 */
data class SnowfallBoxState(
    private val size: Size = Size.Zero,
    val snowflakes: List<Snowflake> = emptyList(),
    private val lastFrameTime: Long = -1
) {
    companion object {
        /**
         * Updates the size of the component.
         *
         * @param size The new size of the component.
         * @return A [SnowfallBoxState] with the new size.
         */
        fun SnowfallBoxState.sizeChanged(size: Size): SnowfallBoxState {
            Timber.d("update size from ${this.size} to $size")
            if (size == this.size) return this
            return copy(size = size)
        }

        /**
         * Update the list of snowflakes.
         *
         * @param frameTime The current time.
         * @return A [SnowfallBoxState] with the new snowflakes.
         */
        fun SnowfallBoxState.updateSnowflakes(frameTime: Long): SnowfallBoxState {
            val wasFirstFrame = lastFrameTime < 0
            val elapsedMillis = frameTime - lastFrameTime

            if (!wasFirstFrame) {
                snowflakes.forEach { snowflake ->
                    snowflake.next(size, elapsedMillis)
                }
            }

            return copy(lastFrameTime = frameTime)
        }
    }
}
