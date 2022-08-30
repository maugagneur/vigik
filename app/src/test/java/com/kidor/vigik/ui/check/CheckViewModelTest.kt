package com.kidor.vigik.ui.check

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.kidor.vigik.MainCoroutineRule
import com.kidor.vigik.nfc.api.NfcApi
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
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

/**
 * Unit tests for [CheckViewModel].
 */
@RunWith(MockitoJUnitRunner::class)
class CheckViewModelTest {

    @ExperimentalCoroutinesApi
    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @InjectMocks
    private lateinit var viewModel: CheckViewModel

    @Mock
    private lateinit var nfcApi: NfcApi

    @Mock
    private lateinit var stateObserver: Observer<CheckViewState>

    @Before
    fun setUp() {
        viewModel.viewState.observeForever(stateObserver)
    }

    @Test
    fun checkViewStateAtStart() {
        logTestName()

        val state = viewModel.viewState.value
        assertEquals(CheckViewState.Loading, state, "View state")
    }

    @ExperimentalCoroutinesApi
    @Test
    fun goToNextScreenWhenNfcEnableOnStart() {
        logTestName()

        runTest {
            // Given
            `when`(nfcApi.isNfcEnable()).thenReturn(true)
            var event: CheckViewEvent? = null
            val job = launch(UnconfinedTestDispatcher()) {
                event = viewModel.viewEvent.first()
            }

            // When
            viewModel.handleAction(CheckViewAction.RefreshNfcStatus)

            // Then
            assertEquals(CheckViewEvent.NavigateToHub, event, "Navigation event")

            job.cancel()
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun displayErrorWhenNfcDisableOnStart() {
        logTestName()

        // Given
        `when`(nfcApi.isNfcEnable()).thenReturn(false)

        // When
        runTest {
            viewModel.handleAction(CheckViewAction.RefreshNfcStatus)
        }

        // Then
        val state = viewModel.viewState.value
        assertEquals(CheckViewState.NfcIsDisable, state, "View state")
    }

    @ExperimentalCoroutinesApi
    @Test
    fun goToNextScreenWhenNfcEnableOnRefresh() {
        logTestName()

        runTest {
            // Given
            `when`(nfcApi.isNfcEnable()).thenReturn(true)
            var event: CheckViewEvent? = null
            val job = launch(UnconfinedTestDispatcher()) {
                event = viewModel.viewEvent.first()
            }

            // When
            viewModel.handleAction(CheckViewAction.RefreshNfcStatus)

            // Then
            assertEquals(CheckViewEvent.NavigateToHub, event, "Navigation event")

            job.cancel()
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun displayErrorWhenNfcDisableOnRefresh() {
        logTestName()

        // Given
        `when`(nfcApi.isNfcEnable()).thenReturn(false)

        // When
        runTest {
            viewModel.handleAction(CheckViewAction.RefreshNfcStatus)
        }

        // Then
        val state = viewModel.viewState.value
        assertEquals(CheckViewState.NfcIsDisable, state, "View state")
    }

    @ExperimentalCoroutinesApi
    @Test
    fun openNfcSettingsWhenActionOnSettingsButton() {
        logTestName()

        runTest {
            // Given
            var event: CheckViewEvent? = null
            val job = launch(UnconfinedTestDispatcher()) {
                event = viewModel.viewEvent.first()
            }

            // When
            viewModel.handleAction(CheckViewAction.DisplayNfcSettings)

            // Then
            assertEquals(CheckViewEvent.NavigateToSettings, event, "Navigation event")

            job.cancel()
        }
    }
}
