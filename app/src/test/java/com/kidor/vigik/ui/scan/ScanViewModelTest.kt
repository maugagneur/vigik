package com.kidor.vigik.ui.scan

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.kidor.vigik.MainCoroutineRule
import com.kidor.vigik.db.TagRepository
import com.kidor.vigik.nfc.api.NfcApi
import com.kidor.vigik.nfc.model.Tag
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
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.doSuspendableAnswer

/**
 * Unit test for [ScanViewModel].
 */
@RunWith(MockitoJUnitRunner::class)
class ScanViewModelTest {

    @ExperimentalCoroutinesApi
    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @InjectMocks
    lateinit var viewModel: ScanViewModel

    @Mock
    lateinit var nfcApi: NfcApi
    @Mock
    lateinit var tagRepository: TagRepository

    @Mock
    private lateinit var stateObserver: Observer<ScanViewState>

    @Before
    fun setUp() {
        viewModel.viewState.observeForever(stateObserver)
    }

    @Test
    fun registerViewOnStart() {
        logTestName()

        // Then
        verify(nfcApi).register(viewModel)
        val state = viewModel.viewState.value
        assertEquals(ScanViewState.Loading, state, "View state")
    }

    @Test
    fun unregisterViewOnStart() {
        logTestName()

        // When
        viewModel.onCleared()

        // Then
        verify(nfcApi).unregister(viewModel)
    }

    @Test
    fun forwardGoodTagInfoToView() {
        logTestName()

        // Given
        val tag = Tag(System.currentTimeMillis(), byteArrayOf(0x13, 0x37), "Tech list", "Data", byteArrayOf(0x42))

        // When
        viewModel.onNfcTagRead(tag)

        // Then
        val state = viewModel.viewState.value
        assertEquals(ScanViewState.DisplayTag(tag, true), state, "View state")
    }

    @Test
    fun forwardInvalidTagInfoToView() {
        logTestName()

        // Given
        val tag = Tag()

        // When
        viewModel.onNfcTagRead(tag)

        // Then
        val state = viewModel.viewState.value
        assertEquals(ScanViewState.DisplayTag(tag, false), state, "View state")
    }

    @ExperimentalCoroutinesApi
    @Test
    fun promptErrorWhenTryingToSaveTagBeforeAnyResult() {
        logTestName()

        runTest {
            // Given
            var event: ScanViewEvent? = null
            val job = launch(UnconfinedTestDispatcher()) {
                event = viewModel.viewEvent.first()
            }

            // When
            viewModel.handleAction(ScanViewAction.SaveTag)

            // Then
            assertEquals(ScanViewEvent.SaveTagFailure, event, "Failure event")

            job.cancel()
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun promptSuccessWhenTagIsSaved() {
        logTestName()
        runTest {
            // Given
            val tag = Tag(System.currentTimeMillis(), byteArrayOf(0x13, 0x37), "Tech list", "Data", byteArrayOf(0x42))
            `when`(tagRepository.insert(tag)).doSuspendableAnswer { 1L }
            var event: ScanViewEvent? = null
            val job = launch(UnconfinedTestDispatcher()) {
                event = viewModel.viewEvent.first()
            }

            // When
            viewModel.onNfcTagRead(tag)
            viewModel.handleAction(ScanViewAction.SaveTag)

            // Then
            assertEquals(ScanViewEvent.SaveTagSuccess, event, "Success event")

            job.cancel()
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun promptErrorWhenTagIsNotSaved() {
        logTestName()
        runTest {
            // Given
            val tag = Tag(System.currentTimeMillis(), byteArrayOf(0x13, 0x37), "Tech list", "Data", byteArrayOf(0x42))
            `when`(tagRepository.insert(tag)).doSuspendableAnswer { -1L }
            var event: ScanViewEvent? = null
            val job = launch(UnconfinedTestDispatcher()) {
                event = viewModel.viewEvent.first()
            }

            // When
            viewModel.onNfcTagRead(tag)
            viewModel.handleAction(ScanViewAction.SaveTag)

            // Then
            assertEquals(ScanViewEvent.SaveTagFailure, event, "Failure event")

            job.cancel()
        }
    }
}
