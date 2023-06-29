package com.kidor.vigik.ui.nfc.scan

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.kidor.vigik.MainCoroutineRule
import com.kidor.vigik.data.tag.TagRepository
import com.kidor.vigik.data.nfc.api.NfcApi
import com.kidor.vigik.data.nfc.model.Tag
import com.kidor.vigik.utils.AssertUtils.assertEquals
import com.kidor.vigik.utils.TestUtils.logTestName
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Unit test for [ScanViewModel].
 */
class ScanViewModelTest {

    @ExperimentalCoroutinesApi
    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @InjectMockKs
    private lateinit var viewModel: ScanViewModel

    @MockK
    private lateinit var nfcApi: NfcApi
    @MockK
    private lateinit var tagRepository: TagRepository

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)
    }

    @Test
    fun registerViewOnStart() {
        logTestName()

        // Then
        verify { nfcApi.register(viewModel) }
        val state = viewModel.viewState.value
        assertEquals(ScanViewState.Loading, state, "View state")
    }

    @Test
    fun unregisterViewOnStart() {
        logTestName()

        // When
        viewModel.onCleared()

        // Then
        verify { nfcApi.unregister(viewModel) }
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
            viewModel.viewEvent.test {
                // When
                viewModel.handleAction(ScanViewAction.SaveTag)

                // Then
                assertEquals(ScanViewEvent.SaveTagFailure, awaitItem(), "Failure event")

                cancelAndIgnoreRemainingEvents()
            }
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun promptSuccessWhenTagIsSaved() {
        logTestName()
        runTest {
            // Given
            val tag = Tag(System.currentTimeMillis(), byteArrayOf(0x13, 0x37), "Tech list", "Data", byteArrayOf(0x42))
            coEvery { tagRepository.insert(tag) } returns 1L

            viewModel.viewEvent.test {
                // When
                viewModel.onNfcTagRead(tag)
                viewModel.handleAction(ScanViewAction.SaveTag)

                // Then
                assertEquals(ScanViewEvent.SaveTagSuccess, awaitItem(), "Success event")

                cancelAndIgnoreRemainingEvents()
            }
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun promptErrorWhenTagIsNotSaved() {
        logTestName()
        runTest {
            // Given
            val tag = Tag(System.currentTimeMillis(), byteArrayOf(0x13, 0x37), "Tech list", "Data", byteArrayOf(0x42))
            coEvery { tagRepository.insert(tag) } returns -1L

            viewModel.viewEvent.test {
                // When
                viewModel.onNfcTagRead(tag)
                viewModel.handleAction(ScanViewAction.SaveTag)

                // Then
                assertEquals(ScanViewEvent.SaveTagFailure, awaitItem(), "Failure event")

                cancelAndIgnoreRemainingEvents()
            }
        }
    }
}
