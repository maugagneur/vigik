package com.kidor.vigik.ui.animations.glitterrainbow

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import com.kidor.vigik.ui.animations.glitterrainbow.GlitterBoxState.Companion.sizeChanged
import com.kidor.vigik.ui.animations.glitterrainbow.GlitterBoxState.Companion.updateLifeTime
import com.kidor.vigik.ui.animations.glitterrainbow.GlitterBoxState.Companion.updateSourceOffset
import com.kidor.vigik.ui.animations.glitterrainbow.GlitterBoxState.Companion.updateSpeedCoefficient
import com.kidor.vigik.utils.AssertUtils.assertEquals
import com.kidor.vigik.utils.TestUtils.logTestName
import io.mockk.MockKAnnotations
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for [GlitterBoxState].
 */
class GlitterBoxStateTest {

    private lateinit var glitterBoxState: GlitterBoxState

    private var speedCoefficient = 1f
    private var lifeTime = 50

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        glitterBoxState = GlitterBoxState(
            colors = listOf(Color.Blue, Color.Green, Color.Yellow),
            glitterShape = Mixed,
            speedCoefficient = speedCoefficient,
            lifeTime = lifeTime
        )
    }

    @Test
    fun `test updating size`() {
        logTestName()

        // Check initial size
        assertEquals(Size.Zero, glitterBoxState.size, "Size")

        // Change glitter box's size
        val newSize = Size(width = 360f, height = 720f)
        glitterBoxState = glitterBoxState.sizeChanged(newSize)

        // Check size after update
        assertEquals(newSize, glitterBoxState.size, "Size")
    }

    @Test
    fun `test updating source offset`() {
        logTestName()

        // Check initial source offset
        assertEquals(Offset.Zero, glitterBoxState.sourceOffset, "Source offset")

        // Change glitter box's source offset
        val offset = Offset(x = 100f, y = 250f)
        glitterBoxState = glitterBoxState.updateSourceOffset(offset)

        // Check source offset after update
        assertEquals(offset, glitterBoxState.sourceOffset, "Source offset")
    }

    @Test
    fun `test updating speed coefficient`() {
        logTestName()

        // Check initial speed coefficient
        assertEquals(speedCoefficient, glitterBoxState.speedCoefficient, "Speed coefficient")

        // Change glitter box's speed coefficient
        speedCoefficient = 4f
        glitterBoxState = glitterBoxState.updateSpeedCoefficient(speedCoefficient)

        // Check speed coefficient after update
        assertEquals(speedCoefficient, glitterBoxState.speedCoefficient, "Speed coefficient")
    }

    @Test
    fun `test updating life time`() {
        logTestName()

        // Check initial life time
        assertEquals(lifeTime, glitterBoxState.lifeTime, "Life time")

        // Change glitter box's life time
        lifeTime = 100
        glitterBoxState = glitterBoxState.updateLifeTime(lifeTime)

        // Check life time after update
        assertEquals(lifeTime, glitterBoxState.lifeTime, "Life time")
    }

    @Test
    fun `test generate next state`() {
        logTestName()

        // Create a glitter box state with multiple glitters
        val size = Size(480f, 800f)
        val glitters = mutableListOf<Glitter>()
        repeat(10) {
            glitters.add(mockk(relaxUnitFun = true))
        }
        glitterBoxState = glitterBoxState.copy(glitters = glitters, size = size)

        // Compute a new state of glitter box
        val duration = 10L
        glitterBoxState.next(duration)

        // Check that each glitter was updated
        glitters.forEach { glitter ->
            verify { glitter.next(size, duration, speedCoefficient) }
        }
    }
}
