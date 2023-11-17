package com.kidor.vigik.ui.animations.glitterrainbow

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import com.kidor.vigik.utils.AssertUtils.assertEquals
import com.kidor.vigik.utils.TestUtils.logTestName
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for [Glitter].
 */
class GlitterTest {

    @MockK
    private lateinit var canvas: Canvas

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)
    }

    @Test
    fun `test glitter creation`() {
        logTestName()

        var lifeTime = 100
        // Try creating triangle
        var glitter = Glitter.create(color = Color.Blue, glitterShape = GeometricShape.Triangle, source = Offset.Zero, lifeTime = lifeTime)

        assertEquals(lifeTime, glitter.lifeCount, "Time to live")

        lifeTime = 50
        // Try creating glitter with random shape.
        glitter = Glitter.create(color = Color.Green, glitterShape = Mixed, source = Offset.Zero, lifeTime = lifeTime)

        assertEquals(lifeTime, glitter.lifeCount, "Time to live")
    }

    @Test
    fun `test compute next state`() {
        logTestName()

        val lifeTime = 100
        val glitter = Glitter.create(color = Color.Blue, glitterShape = Mixed, source = Offset.Zero, lifeTime = lifeTime)

        // Compute the next step
        glitter.next(borders = Size(width = 400f, height = 700f), durationMillis = 10, speedCoefficient = 1f)

        // Check that life count as decrease
        assertEquals(lifeTime - 1, glitter.lifeCount, "Time to live")
    }

    @Test
    fun `test draw circle glitter`() {
        logTestName()

        val color = Color.Blue
        val source = Offset(x = 200f, y = 350f)

        // Draw a circle
        Glitter.create(color, GeometricShape.Circle, source, 100).draw(canvas)

        verify { canvas.drawCircle(source, any(), any()) }
    }

    @Test
    fun `test draw triangle glitter`() {
        logTestName()

        val color = Color.Blue
        val source = Offset(x = 200f, y = 350f)

        // Draw a triangle
        Glitter.create(color, GeometricShape.Triangle, source, 100).draw(canvas)

        verify { canvas.drawPath(any(), any()) }
    }

    @Test
    fun `test draw rectangle glitter`() {
        logTestName()

        val color = Color.Blue
        val source = Offset(x = 200f, y = 350f)

        // Draw a rectangle
        Glitter.create(color, GeometricShape.Rectangle, source, 100).draw(canvas)

        verify { canvas.drawRect(any(), any()) }
    }
}
