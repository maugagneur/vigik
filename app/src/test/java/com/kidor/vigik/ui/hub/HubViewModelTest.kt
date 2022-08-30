package com.kidor.vigik.ui.hub

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.kidor.vigik.MainCoroutineRule
import com.kidor.vigik.utils.AssertUtils.assertEquals
import com.kidor.vigik.utils.TestUtils.logTestName
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

/**
 * Unit test for [HubViewModel].
 */
@RunWith(MockitoJUnitRunner::class)
class HubViewModelTest {

    @ExperimentalCoroutinesApi
    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: HubViewModel

    @Mock
    private lateinit var stateObserver: Observer<HubViewState>

    @Before
    fun setUp() {
        viewModel = HubViewModel()
        viewModel.viewState.observeForever(stateObserver)
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
            // Given
            var event: HubViewEvent? = null
            val job = launch(UnconfinedTestDispatcher()) {
                event = viewModel.viewEvent.first()
            }

            // When
            viewModel.handleAction(HubViewAction.DisplayScanTagView)

            // Then
            assertEquals(HubViewEvent.NavigateToScanView, event, "Navigation event")

            job.cancel()
        }

    }

    @ExperimentalCoroutinesApi
    @Test
    fun redirectToTagsHistory() {
        logTestName()

        runTest {
            // Given
            var event: HubViewEvent? = null
            val job = launch(UnconfinedTestDispatcher()) {
                event = viewModel.viewEvent.first()
            }

            // When
            viewModel.handleAction(HubViewAction.DisplayTagHistoryView)

            // Then
            assertEquals(HubViewEvent.NavigateToHistoryView, event, "Navigation event")

            job.cancel()
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun redirectToEmulateTag() {
        logTestName()

        runTest {
            // Given
            var event: HubViewEvent? = null
            val job = launch(UnconfinedTestDispatcher()) {
                event = viewModel.viewEvent.first()
            }

            // When
            viewModel.handleAction(HubViewAction.DisplayEmulateTagView)

            // Then
            assertEquals(HubViewEvent.NavigateToEmulateView, event, "Navigation event")

            job.cancel()
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun noAutomaticRedirectionAtStart() {
        logTestName()

        runTest {
            // Given
            var event: HubViewEvent? = null
            val job = launch(UnconfinedTestDispatcher()) {
                event = viewModel.viewEvent.first()
            }

            // Then
            assertEquals(null, event, "Navigation event")

            job.cancel()
        }
    }
}
