package com.kidor.vigik.ui.animations.snowfall

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Canvas
import com.kidor.vigik.utils.AssertUtils.assertEquals
import com.kidor.vigik.utils.AssertUtils.assertTrue
import com.kidor.vigik.utils.TestUtils.logTestName
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for [Snowflake].
 */
class SnowflakeTest {

    @MockK
    private lateinit var canvas: Canvas

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)
    }

    @Test
    fun `test snowflake creation`() {
        logTestName()

        // Check that the random generation method produces different objects
        val firstSnowflake = Snowflake.generateRandom()
        val secondSnowflake = Snowflake.generateRandom()
        assertTrue(firstSnowflake != secondSnowflake, "Compare two random creations")
    }

    @Test
    fun `test compute next state`() {
        logTestName()

        val snowflake = Snowflake.generateRandom()
        val initialPosition = snowflake.position
        val initialRadius = snowflake.radius
        val initialSpeed = snowflake.speed

        // Compute next state
        snowflake.next(container = Size(width = 400f, height = 700f), durationMillis = 10)

        // Check that only position changed
        assertTrue(initialPosition != snowflake.position, "Position")
        assertEquals(initialRadius, snowflake.radius, "Radius")
        assertEquals(initialSpeed, snowflake.speed, "Speed")
    }

    @Test
    fun `test draw snowflake`() {
        logTestName()

        val snowflake = Snowflake.generateRandom()
        snowflake.draw(canvas = canvas, container = Size(width = 400f, height = 700f))

        verify { canvas.drawCircle(center = any(), radius = snowflake.radius, any()) }
    }
}
