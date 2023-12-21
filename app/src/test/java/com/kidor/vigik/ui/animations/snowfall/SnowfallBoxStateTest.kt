package com.kidor.vigik.ui.animations.snowfall

import androidx.compose.ui.geometry.Size
import com.kidor.vigik.ui.animations.snowfall.SnowfallBoxState.Companion.sizeChanged
import com.kidor.vigik.ui.animations.snowfall.SnowfallBoxState.Companion.updateSnowflakes
import com.kidor.vigik.utils.TestUtils.logTestName
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for [SnowfallBoxState]
 */
class SnowfallBoxStateTest {

    private lateinit var snowfallBoxState: SnowfallBoxState

    private lateinit var snowflakes: List<Snowflake>

    @Before
    fun setUp() {
        snowflakes = List(10) { mockk<Snowflake>(relaxUnitFun = true) }
        snowfallBoxState = SnowfallBoxState(
            size = Size.Zero,
            snowflakes = snowflakes,
            lastFrameTime = 0
        )
    }

    @Test
    fun `test updating snowflakes`() {
        logTestName()

        val timeFrame = 10L

        // Update snowflakes based on new time frame
        snowfallBoxState = snowfallBoxState.updateSnowflakes(timeFrame)

        // Check that every snowflakes were updated with the time frame
        snowflakes.forEach { snowflake ->
            verify { snowflake.next(any(), timeFrame) }
        }
    }

    @Test
    fun `test updating size`() {
        logTestName()

        // Update snowflakes
        snowfallBoxState = snowfallBoxState.updateSnowflakes(10)

        // Check that every snowflakes were updated with the original size
        snowflakes.forEach { snowflake ->
            verify { snowflake.next(Size.Zero, any()) }
        }

        // Change glitter box's size
        val newSize = Size(width = 360f, height = 720f)
        snowfallBoxState = snowfallBoxState.sizeChanged(newSize)
        // Update snowflakes
        snowfallBoxState = snowfallBoxState.updateSnowflakes(20)

        // Check that every snowflakes were updated with the new size
        snowflakes.forEach { snowflake ->
            verify { snowflake.next(newSize, any()) }
        }
    }
}
