package com.kidor.vigik.ui.check

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.kidor.vigik.MainCoroutineRule
import com.kidor.vigik.nfc.api.NfcApi
import com.kidor.vigik.ui.base.EventWrapper
import com.kidor.vigik.utils.AssertUtils.assertEquals
import com.kidor.vigik.utils.TestUtils.logTestName
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceTimeBy
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

    @Mock
    private lateinit var eventObserver: Observer<EventWrapper<CheckViewEvent>>

    @Before
    fun setUp() {
        viewModel.viewState.observeForever(stateObserver)
        viewModel.viewEvent.observeForever(eventObserver)
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

        // Given
        `when`(nfcApi.isNfcEnable()).thenReturn(true)

        // When
        runTest {
            viewModel.handleAction(CheckViewAction.RefreshNfcStatus)
            advanceTimeBy(TIME_BEFORE_NFC_CHECK)
        }

        // Then
        val event = viewModel.viewEvent.value
        assertEquals(CheckViewEvent.NavigateToHub, event?.peekEvent(), "Navigation event")
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
            advanceTimeBy(TIME_BEFORE_NFC_CHECK)
        }

        // Then
        val state = viewModel.viewState.value
        assertEquals(CheckViewState.NfcIsDisable, state, "View state")
    }

    @ExperimentalCoroutinesApi
    @Test
    fun goToNextScreenWhenNfcEnableOnRefresh() {
        logTestName()

        // Given
        `when`(nfcApi.isNfcEnable()).thenReturn(true)

        // When
        runTest {
            viewModel.handleAction(CheckViewAction.RefreshNfcStatus)
            advanceTimeBy(TIME_BEFORE_NFC_CHECK)
        }

        // Then
        val event = viewModel.viewEvent.value
        assertEquals(CheckViewEvent.NavigateToHub, event?.peekEvent(), "Navigation event")
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
            advanceTimeBy(TIME_BEFORE_NFC_CHECK)
        }

        // Then
        val state = viewModel.viewState.value
        assertEquals(CheckViewState.NfcIsDisable, state, "View state")
    }

    @Test
    fun openNfcSettingsWhenActionOnSettingsButton() {
        logTestName()

        // When
        viewModel.handleAction(CheckViewAction.DisplayNfcSettings)

        // Then
        val event = viewModel.viewEvent.value
        assertEquals(CheckViewEvent.NavigateToSettings, event?.peekEvent(), "Navigation event")
    }
}