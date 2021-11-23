package com.kidor.vigik.ui.check

import com.kidor.vigik.nfc.api.NfcApi
import com.kidor.vigik.utils.TestUtils.logTestName
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.timeout
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner

/**
 * Unit tests for [CheckViewModel].
 */
@RunWith(MockitoJUnitRunner::class)
class CheckViewModelTest {

    @InjectMocks
    private lateinit var viewModel: CheckViewModel

    @Mock
    private lateinit var nfcApi: NfcApi
    @Mock
    private lateinit var view: CheckContract.CheckView

    @Before
    fun setUp() {
        viewModel.setView(view)

        `when`(view.isActive()).thenReturn(true)
    }

    @Test
    fun goToNextScreenWhenNfcEnableOnStart() {
        logTestName()

        // When
        `when`(nfcApi.isNfcEnable()).thenReturn(true)

        // Run
        viewModel.onStart()

        // Verify
        verify(view).displayLoader()
        verify(view, timeout(TIME_BEFORE_NFC_CHECK * 2)).goToNextStep()
    }

    @Test
    fun displayErrorWhenNfcDisableOnStart() {
        logTestName()

        // When
        `when`(nfcApi.isNfcEnable()).thenReturn(false)

        // Run
        viewModel.onStart()

        // Verify
        verify(view).displayLoader()
        verify(view, timeout(TIME_BEFORE_NFC_CHECK * 2)).displayNfcDisableMessage()
    }

    @Test
    fun goToNextScreenWhenNfcEnableOnRefresh() {
        logTestName()

        // When
        `when`(nfcApi.isNfcEnable()).thenReturn(true)

        // Run
        viewModel.onActionRefresh()

        // Verify
        verify(view).displayLoader()
        verify(view, timeout(TIME_BEFORE_NFC_CHECK * 2)).goToNextStep()
    }

    @Test
    fun displayErrorWhenNfcDisableOnRefresh() {
        logTestName()

        // When
        `when`(nfcApi.isNfcEnable()).thenReturn(false)

        // Run
        viewModel.onActionRefresh()

        // Verify
        verify(view).displayLoader()
        verify(view, timeout(TIME_BEFORE_NFC_CHECK * 2)).displayNfcDisableMessage()
    }

    @Test
    fun openNfcSettingsWhenActionOnSettingsButton() {
        logTestName()

        // Run
        viewModel.onActionSettings()

        // Verify
        verify(view).displayNfcSettings()
    }
}