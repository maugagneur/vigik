package com.kidor.vigik.ui.scan

import com.kidor.vigik.db.TagRepository
import com.kidor.vigik.nfc.api.NfcApi
import com.kidor.vigik.nfc.model.Tag
import com.kidor.vigik.utils.TestUtils.logTestName
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.doSuspendableAnswer

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
    lateinit var tagRepository: TagRepository
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
        val tag = Tag()
        viewModel.onNfcTagRead(tag)

        // Verify
        verify(view).displayScanResult(tag.toString())
    }

    @Test
    fun promptSuccessWhenTagIsSaved() = runBlocking {
        logTestName()

        // When
        val tag = Tag(System.currentTimeMillis(), byteArrayOf(0x13, 0x37), "Tech list", "Data", byteArrayOf(0x42))
        `when`(tagRepository.insert(tag)).doSuspendableAnswer { 1L }

        // Run
        viewModel.onNfcTagRead(tag)
        viewModel.saveTag()

        // Verify
        verify(view).promptSaveSuccess()
    }

    @Test
    fun promptErrorWhenTagIsNotSaved() = runBlocking {
        logTestName()

        // When
        val tag = Tag(System.currentTimeMillis(), byteArrayOf(0x13, 0x37), "Tech list", "Data", byteArrayOf(0x42))
        `when`(tagRepository.insert(tag)).doSuspendableAnswer { -1L }

        // Run
        viewModel.onNfcTagRead(tag)
        viewModel.saveTag()

        // Verify
        verify(view).promptSaveFail()
    }
}