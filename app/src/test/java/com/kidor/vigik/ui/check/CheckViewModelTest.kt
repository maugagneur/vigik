package com.kidor.vigik.ui.check

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.kidor.vigik.nfc.api.NfcApi
import com.kidor.vigik.utils.AssertUtils.assertEquals
import com.kidor.vigik.utils.Event
import com.kidor.vigik.utils.TestUtils.logTestName
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Ignore
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

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @InjectMocks
    private lateinit var viewModel: CheckViewModel

    @Mock
    private lateinit var nfcApi: NfcApi

    @Mock
    private lateinit var stateObserver: Observer<CheckViewState>
    @Mock
    private lateinit var eventObserver: Observer<Event<CheckViewEvent>>

    @Before
    fun setUp() {
        viewModel.viewState.observeForever(stateObserver)
        viewModel.viewEvent.observeForever(eventObserver)
    }

    @Test
    @Ignore("Find a way to handle coroutine's delay")
    fun goToNextScreenWhenNfcEnableOnStart() = runBlocking {
        logTestName()

        // Given
        `when`(nfcApi.isNfcEnable()).thenReturn(true)

        // When
        viewModel.onActionRefresh()
        Thread.sleep(TIME_BEFORE_NFC_CHECK * 2)

        // Then
        val event = viewModel.viewEvent.value
        assertEquals(CheckViewEvent.NavigateToHub, event?.peekContent(), "Navigation event")
    }

    @Test
    @Ignore("Find a way to handle coroutine's delay")
    fun displayErrorWhenNfcDisableOnStart() {
        logTestName()

        // Given
        `when`(nfcApi.isNfcEnable()).thenReturn(false)

        // When
        viewModel.onActionRefresh()

        // Then
    }

    @Test
    @Ignore("Find a way to handle coroutine's delay")
    fun goToNextScreenWhenNfcEnableOnRefresh() {
        logTestName()

        // Given
        `when`(nfcApi.isNfcEnable()).thenReturn(true)

        // When
        viewModel.onActionRefresh()

        // Then
    }

    @Test
    @Ignore("Find a way to handle coroutine's delay")
    fun displayErrorWhenNfcDisableOnRefresh() {
        logTestName()

        // Given
        `when`(nfcApi.isNfcEnable()).thenReturn(false)

        // When
        viewModel.onActionRefresh()

        // Then
    }

    @Test
    fun openNfcSettingsWhenActionOnSettingsButton() {
        logTestName()

        // When
        viewModel.onActionSettings()

        // Then
        val event = viewModel.viewEvent.value
        assertEquals(CheckViewEvent.NavigateToSettings, event?.peekContent(), "Navigation event")
    }
}