package com.kidor.vigik.ui.nfc.hub

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.kidor.vigik.MainCoroutineRule
import com.kidor.vigik.utils.AssertUtils.assertEquals
import com.kidor.vigik.utils.TestUtils.logTestName
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Unit test for [HubViewModel].
 */
class HubViewModelTest {

    @ExperimentalCoroutinesApi
    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: HubViewModel

    @Before
    fun setUp() {
        viewModel = HubViewModel()
    }

    @Test
    fun checkViewStateAtStart() {
        logTestName()

        val state = viewModel.viewState.value
        assertEquals(HubViewState.Default, state, "View state")
    }

    @ExperimentalCoroutinesApi
    @Test
    fun redirectToReadTag() {
        logTestName()

        runTest {
            viewModel.viewEvent.test {
                // When
                viewModel.handleAction(HubViewAction.DisplayScanTagView)

                // Then
                assertEquals(HubViewEvent.NavigateToScanView, awaitItem(), "Navigation event")

                cancelAndIgnoreRemainingEvents()
            }
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun redirectToTagsHistory() {
        logTestName()

        runTest {
            viewModel.viewEvent.test {
                // When
                viewModel.handleAction(HubViewAction.DisplayTagHistoryView)

                // Then
                assertEquals(HubViewEvent.NavigateToHistoryView, awaitItem(), "Navigation event")

                cancelAndIgnoreRemainingEvents()
            }
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun redirectToEmulateTag() {
        logTestName()

        runTest {
            viewModel.viewEvent.test {
                // When
                viewModel.handleAction(HubViewAction.DisplayEmulateTagView)

                // Then
                assertEquals(HubViewEvent.NavigateToEmulateView, awaitItem(), "Navigation event")

                cancelAndIgnoreRemainingEvents()
            }
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun noAutomaticRedirectionAtStart() {
        logTestName()

        runTest {
            viewModel.viewEvent.test {
                // Then
                expectNoEvents()

                cancelAndIgnoreRemainingEvents()
            }
        }
    }
}
