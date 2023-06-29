package com.kidor.vigik.ui.nfc.check

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.kidor.vigik.MainCoroutineRule
import com.kidor.vigik.nfc.api.NfcApi
import com.kidor.vigik.utils.AssertUtils.assertEquals
import com.kidor.vigik.utils.TestUtils.logTestName
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Unit tests for [CheckViewModel].
 */
class CheckViewModelTest {

    @ExperimentalCoroutinesApi
    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @InjectMockKs
    private lateinit var viewModel: CheckViewModel

    @MockK
    private lateinit var nfcApi: NfcApi

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
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
            every { nfcApi.isNfcEnable() } returns true

            viewModel.viewEvent.test {
                // When
                viewModel.handleAction(CheckViewAction.RefreshNfcStatus)

                // Then
                assertEquals(CheckViewEvent.NavigateToHub, awaitItem(), "Navigation event")

                cancelAndIgnoreRemainingEvents()
            }
        }
    }

    @Test
    fun displayErrorWhenNfcDisableOnStart() {
        logTestName()

        // Given
        every { nfcApi.isNfcEnable() } returns false

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
            every { nfcApi.isNfcEnable() } returns true

            viewModel.viewEvent.test {
                // When
                viewModel.handleAction(CheckViewAction.RefreshNfcStatus)

                // Then
                assertEquals(CheckViewEvent.NavigateToHub, awaitItem(), "Navigation event")

                cancelAndIgnoreRemainingEvents()
            }
        }
    }

    @Test
    fun displayErrorWhenNfcDisableOnRefresh() {
        logTestName()

        // Given
        every { nfcApi.isNfcEnable() } returns false

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
            viewModel.viewEvent.test {
                // When
                viewModel.handleAction(CheckViewAction.DisplayNfcSettings)

                // Then
                assertEquals(CheckViewEvent.NavigateToSettings, awaitItem(), "Navigation event")

                cancelAndIgnoreRemainingEvents()
            }
        }
    }
}
