package com.kidor.vigik.ui.scan

import com.kidor.vigik.nfc.api.NfcApi
import com.kidor.vigik.nfc.api.TagData
import com.kidor.vigik.utils.TestUtils.logTestName
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner

/**
 * Unit test for [ScanViewModel]
 */
@RunWith(MockitoJUnitRunner::class)
class ScanViewModelTest {

    @InjectMocks
    lateinit var viewModel: ScanViewModel

    @Mock
    lateinit var nfcApi: NfcApi
    @Mock
    lateinit var view: ScanContract.ScanView

    @Before
    fun setUp() {
        viewModel.setView(view)

        `when`(view.isActive()).thenReturn(true)
    }

    @Test
    fun registerViewOnStart() {
        logTestName()

        // Run
        viewModel.onStart()

        // Verify
        verify(nfcApi).register(viewModel)
    }

    @Test
    fun unregisterViewOnStart() {
        logTestName()

        // Run
        viewModel.onStop()

        // Verify
        verify(nfcApi).unregister(viewModel)
    }

    @Test
    fun forwardTagInfoToView() {
        logTestName()

        // Run
        val tagData = TagData()
        viewModel.onNfcTagRead(tagData)

        // Verify
        verify(view).displayScanResult(tagData.toString())
    }
}