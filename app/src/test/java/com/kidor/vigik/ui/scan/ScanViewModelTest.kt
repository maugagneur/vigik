package com.kidor.vigik.ui.scan

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.kidor.vigik.db.TagRepository
import com.kidor.vigik.nfc.api.NfcApi
import com.kidor.vigik.nfc.model.Tag
import com.kidor.vigik.utils.AssertUtils.assertEquals
import com.kidor.vigik.ui.base.EventWrapper
import com.kidor.vigik.utils.TestUtils.logTestName
import kotlinx.coroutines.runBlocking
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
 * Unit test for [ScanViewModel]
 */
@RunWith(MockitoJUnitRunner::class)
class ScanViewModelTest {

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
    @Mock
    private lateinit var eventObserver: Observer<EventWrapper<ScanViewEvent>>

    @Before
    fun setUp() {
        viewModel.viewState.observeForever(stateObserver)
        viewModel.viewEvent.observeForever(eventObserver)
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

    @Test
    fun promptErrorWhenTryingToSaveTagBeforeAnyResult() = runBlocking {
        logTestName()

        // When
        viewModel.saveTag()

        // Then
        val event = viewModel.viewEvent.value
        assertEquals(ScanViewEvent.SaveTagFailure, event?.peekEvent(), "Failure event")
    }

    @Test
    fun promptSuccessWhenTagIsSaved() = runBlocking {
        logTestName()

        // Given
        val tag = Tag(System.currentTimeMillis(), byteArrayOf(0x13, 0x37), "Tech list", "Data", byteArrayOf(0x42))
        `when`(tagRepository.insert(tag)).doSuspendableAnswer { 1L }

        // When
        viewModel.onNfcTagRead(tag)
        viewModel.saveTag()

        // Then
        val event = viewModel.viewEvent.value
        assertEquals(ScanViewEvent.SaveTagSuccess, event?.peekEvent(), "Success event")
    }

    @Test
    fun promptErrorWhenTagIsNotSaved() = runBlocking {
        logTestName()

        // Given
        val tag = Tag(System.currentTimeMillis(), byteArrayOf(0x13, 0x37), "Tech list", "Data", byteArrayOf(0x42))
        `when`(tagRepository.insert(tag)).doSuspendableAnswer { -1L }

        // When
        viewModel.onNfcTagRead(tag)
        viewModel.saveTag()

        // Then
        val event = viewModel.viewEvent.value
        assertEquals(ScanViewEvent.SaveTagFailure, event?.peekEvent(), "Failure event")
    }
}