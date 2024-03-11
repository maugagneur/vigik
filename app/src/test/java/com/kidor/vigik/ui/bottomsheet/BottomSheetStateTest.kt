package com.kidor.vigik.ui.bottomsheet

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.foundation.ExperimentalFoundationApi
import com.kidor.vigik.utils.AssertUtils.assertEquals
import com.kidor.vigik.utils.AssertUtils.assertFalse
import com.kidor.vigik.utils.AssertUtils.assertTrue
import com.kidor.vigik.utils.TestUtils.logTestName
import io.mockk.EqMatcher
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockkConstructor
import io.mockk.spyk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for [BottomSheetState].
 */
@OptIn(ExperimentalFoundationApi::class)
class BottomSheetStateTest {

    private lateinit var bottomSheetState: BottomSheetState

    @MockK
    private lateinit var animationSpec: AnimationSpec<Float>
    @MockK
    private lateinit var confirmValueChanged: (BottomSheetStateValue) -> Boolean

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        every { confirmValueChanged(any()) } returns true
    }

    @Test
    fun `test state creation`() {
        logTestName()

        bottomSheetState = BottomSheetState(
            initialValue = BottomSheetStateValue.HIDDEN,
            animationSpec = animationSpec,
            confirmValueChanged = confirmValueChanged
        )

        assertEquals(animationSpec, bottomSheetState.draggableState.animationSpec, "Animation spec")
        assertEquals(BottomSheetStateValue.HIDDEN, bottomSheetState.currentValue, "Current value")
        assertEquals(BottomSheetStateValue.HIDDEN, bottomSheetState.targetValue, "Target value")
    }

    @Test
    fun `test update anchors`() {
        logTestName()

        bottomSheetState = BottomSheetState(
            initialValue = BottomSheetStateValue.HIDDEN,
            animationSpec = animationSpec,
            confirmValueChanged = confirmValueChanged
        )

        bottomSheetState.updateAnchors(1000, 200)

        // Requiring offset when anchors are not set throws an exception
        bottomSheetState.requireOffset()
    }

    @Test
    fun `test visibility flags depends on state`() {
        logTestName()

        bottomSheetState = spyk<BottomSheetState>(
            BottomSheetState(
                initialValue = BottomSheetStateValue.HIDDEN,
                animationSpec = animationSpec,
                confirmValueChanged = confirmValueChanged
            )
        )
        coEvery { bottomSheetState.animateTo(any(), any()) } answers {
            val target = it.invocation.args[0] as BottomSheetStateValue
            every { bottomSheetState.currentValue } returns target
            every { bottomSheetState.targetValue } returns target
        }

        // At start, the bottom sheet state is HIDDEN
        assertEquals(BottomSheetStateValue.HIDDEN, bottomSheetState.currentValue, "Current value")
        assertEquals(BottomSheetStateValue.HIDDEN, bottomSheetState.targetValue, "Target value")
        assertFalse(bottomSheetState.isVisible, "Is visible")
        assertFalse(bottomSheetState.isExpanded, "Is expanded")
        assertFalse(bottomSheetState.isHalfExpanded, "Is half expanded")

        runTest {
            // State: HIDDEN -> EXPANDED
            bottomSheetState.show()

            assertEquals(BottomSheetStateValue.EXPANDED, bottomSheetState.currentValue, "Current value")
            assertEquals(BottomSheetStateValue.EXPANDED, bottomSheetState.targetValue, "Target value")
            assertTrue(bottomSheetState.isVisible, "Is visible")
            assertTrue(bottomSheetState.isExpanded, "Is expanded")
            assertFalse(bottomSheetState.isHalfExpanded, "Is half expanded")

            // Set anchors so now the component has a HALF_EXPANDED anchor
            bottomSheetState.updateAnchors(1000, 200)

            // State: EXPANDED -> HALF_EXPANDED
            bottomSheetState.show()

            assertEquals(BottomSheetStateValue.HALF_EXPANDED, bottomSheetState.currentValue, "Current value")
            assertEquals(BottomSheetStateValue.HALF_EXPANDED, bottomSheetState.targetValue, "Target value")
            assertTrue(bottomSheetState.isVisible, "Is visible")
            assertFalse(bottomSheetState.isExpanded, "Is expanded")
            assertTrue(bottomSheetState.isHalfExpanded, "Is half expanded")

            // State: HALF_EXPANDED -> EXPANDED
            bottomSheetState.expand()

            assertEquals(BottomSheetStateValue.EXPANDED, bottomSheetState.currentValue, "Current value")
            assertEquals(BottomSheetStateValue.EXPANDED, bottomSheetState.targetValue, "Target value")
            assertTrue(bottomSheetState.isVisible, "Is visible")
            assertTrue(bottomSheetState.isExpanded, "Is expanded")
            assertFalse(bottomSheetState.isHalfExpanded, "Is half expanded")

            // State: EXPANDED -> HIDDEN
            bottomSheetState.hide()

            assertEquals(BottomSheetStateValue.HIDDEN, bottomSheetState.currentValue, "Current value")
            assertEquals(BottomSheetStateValue.HIDDEN, bottomSheetState.targetValue, "Target value")
            assertFalse(bottomSheetState.isVisible, "Is visible")
            assertFalse(bottomSheetState.isExpanded, "Is expanded")
            assertFalse(bottomSheetState.isHalfExpanded, "Is half expanded")

            // State: HIDDEN -> HALF_EXPANDED
            bottomSheetState.halfExpand()

            assertEquals(BottomSheetStateValue.HALF_EXPANDED, bottomSheetState.currentValue, "Current value")
            assertEquals(BottomSheetStateValue.HALF_EXPANDED, bottomSheetState.targetValue, "Target value")
            assertTrue(bottomSheetState.isVisible, "Is visible")
            assertFalse(bottomSheetState.isExpanded, "Is expanded")
            assertTrue(bottomSheetState.isHalfExpanded, "Is half expanded")
        }
    }

    @Test
    fun `test Saver implementation`() {
        logTestName()

        mockkConstructor(BottomSheetState::class)

        val saver = BottomSheetState.Saver(
            animationSpec = animationSpec,
            confirmValueChanged = confirmValueChanged
        )
        val stateValue = BottomSheetStateValue.HALF_EXPANDED

        // Check that the restored object has the correct value
        val original = saver.restore(stateValue)
        if (original is BottomSheetState) {
            assertEquals(BottomSheetStateValue.HALF_EXPANDED, original.currentValue, "Current value")
        } else {
            fail()
        }

        // Check that the restored BottomSheetState has been created with the correct parameters
        verify {
            constructedWith<BottomSheetState>(
                EqMatcher(stateValue),
                EqMatcher(animationSpec),
                EqMatcher(confirmValueChanged)
            )
        }
    }
}
