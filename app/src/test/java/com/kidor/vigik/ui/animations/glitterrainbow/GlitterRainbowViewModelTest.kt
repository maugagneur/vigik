package com.kidor.vigik.ui.animations.glitterrainbow

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.kidor.vigik.MainCoroutineRule
import com.kidor.vigik.utils.AssertUtils.assertEquals
import com.kidor.vigik.utils.TestUtils.logTestName
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Unit tests for [GlitterRainbowViewModel].
 */
class GlitterRainbowViewModelTest {

    @ExperimentalCoroutinesApi
    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: GlitterRainbowViewModel

    @Before
    fun setUp() {
        viewModel = GlitterRainbowViewModel()
    }

    @Test
    fun `test view initial state`() {
        logTestName()

        val viewState = viewModel.viewState.value
        assertEquals(0.5f, viewState?.speedCoefficient, "Speed coefficient")
        assertEquals(100, viewState?.lifeTime, "Life time")
    }

    @Test
    fun `test changing speed coefficient`() {
        logTestName()

        // Change speed coefficient
        viewModel.handleAction(GlitterRainbowViewAction.ChangeSpeedCoefficient(2f))
        // Check that view state contains the new value
        assertEquals(2f, viewModel.viewState.value?.speedCoefficient, "Speed coefficient")

        // Change speed coefficient
        viewModel.handleAction(GlitterRainbowViewAction.ChangeSpeedCoefficient(0.3f))
        // Check that view state contains the new value
        assertEquals(0.3f, viewModel.viewState.value?.speedCoefficient, "Speed coefficient")

        // Change speed coefficient
        viewModel.handleAction(GlitterRainbowViewAction.ChangeSpeedCoefficient(4.2f))
        // Check that view state contains the new value
        assertEquals(4.2f, viewModel.viewState.value?.speedCoefficient, "Speed coefficient")
    }

    @Test
    fun `test changing life time`() {
        logTestName()

        // Change life time
        viewModel.handleAction(GlitterRainbowViewAction.ChangeLifeTime(200))
        // Check that view state contains the new value
        assertEquals(200, viewModel.viewState.value?.lifeTime, "Life time")

        // Change life time
        viewModel.handleAction(GlitterRainbowViewAction.ChangeLifeTime(42))
        // Check that view state contains the new value
        assertEquals(42, viewModel.viewState.value?.lifeTime, "Life time")

        // Change life time
        viewModel.handleAction(GlitterRainbowViewAction.ChangeLifeTime(1337))
        // Check that view state contains the new value
        assertEquals(1337, viewModel.viewState.value?.lifeTime, "Life time")
    }
}
