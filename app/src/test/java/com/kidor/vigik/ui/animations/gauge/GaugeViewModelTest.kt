package com.kidor.vigik.ui.animations.gauge

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.asFlow
import app.cash.turbine.test
import com.kidor.vigik.MainCoroutineRule
import com.kidor.vigik.utils.AssertUtils.assertEquals
import com.kidor.vigik.utils.AssertUtils.assertNotNull
import com.kidor.vigik.utils.TestUtils.logTestName
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.math.max
import kotlin.math.min

/**
 * Unit tests for [GaugeViewModel].
 */
class GaugeViewModelTest {

    @ExperimentalCoroutinesApi
    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: GaugeViewModel

    @Before
    fun setUp() {
        viewModel = GaugeViewModel()
    }

    @Test
    fun `check initial view state`() {
        logTestName()

        val initialState = viewModel.viewState.value
        assertNotNull(initialState, "Initial view state")
        assertEquals(50f, initialState?.gaugeValue, "Gauge value")
    }

    @Test
    fun `test decrease gauge value`() {
        logTestName()

        runTest {
            viewModel.viewState.asFlow().test {
                // Skip initial state
                skipItems(1)

                // Check that the gauge value decreases each time we require a decrease until it reaches the minimum
                for (index in 1..30) {
                    val expectedValue = max(50f - (2 * index), 0f)
                    viewModel.handleAction(GaugeViewAction.DecreaseValue)
                    assertEquals(expectedValue, awaitItem().gaugeValue, "Gauge value")
                }

                cancelAndIgnoreRemainingEvents()
            }
        }
    }

    @Test
    fun `test increase gauge value`() {
        logTestName()

        runTest {
            viewModel.viewState.asFlow().test {
                // Skip initial state
                skipItems(1)

                // Check that the gauge value increases each time we require an increase until it reaches the maximum
                for (index in 1..30) {
                    val expectedValue = min(50f + (2 * index), 100f)
                    viewModel.handleAction(GaugeViewAction.IncreaseValue)
                    assertEquals(expectedValue, awaitItem().gaugeValue, "Gauge value")
                }

                cancelAndIgnoreRemainingEvents()
            }
        }
    }
}
