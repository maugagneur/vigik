package com.kidor.vigik.ui.nfc.history

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.asFlow
import app.cash.turbine.test
import com.kidor.vigik.MainCoroutineRule
import com.kidor.vigik.data.nfc.model.Tag
import com.kidor.vigik.data.tag.TagRepository
import com.kidor.vigik.utils.AssertUtils.assertEquals
import com.kidor.vigik.utils.TestUtils.logTestName
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Unit tests for [HistoryViewModel].
 */
class HistoryViewModelTest {

    @ExperimentalCoroutinesApi
    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: HistoryViewModel

    @MockK
    private lateinit var repository: TagRepository

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
    }

    @Test
    fun initialState() {
        logTestName()

        // Given
        coEvery { repository.allTags } returns flow {
            // Add a little delay so that the start value of flow can be applied
            delay(10)
            emit(emptyList())
        }

        runTest {
            // When
            viewModel = HistoryViewModel(repository)
            viewModel.viewState.asFlow().test {
                // Then
                assertEquals(HistoryViewState.Initializing, awaitItem(), "View state")
                assertEquals(HistoryViewState.NoTag, awaitItem(), "View state")
                cancelAndIgnoreRemainingEvents()
            }
        }
    }

    @Test
    fun displayEmptyHistoryWhenNoTagInRepository() {
        logTestName()

        // Given
        coEvery { repository.allTags } returns flowOf<List<Tag>>(emptyList())
            .map {
                delay(100)
                it
            }

        runTest {
            // When
            viewModel = HistoryViewModel(repository)
            viewModel.viewState.asFlow().test {
                // Then
                assertEquals(HistoryViewState.Initializing, awaitItem(), "View state")
                assertEquals(HistoryViewState.NoTag, awaitItem(), "View state")
                cancelAndIgnoreRemainingEvents()
            }
        }
    }

    @Test
    fun displayOneTag() {
        logTestName()

        // Given
        val tags = listOf(Tag(System.currentTimeMillis(), byteArrayOf(0x13, 0x37), "Tech list", "Data", byteArrayOf(0x42)))
        coEvery { repository.allTags } returns flowOf(tags)
            .map {
                delay(100)
                it
            }

        runTest {
            // When
            viewModel = HistoryViewModel(repository)
            viewModel.viewState.asFlow().test {
                // Then
                assertEquals(HistoryViewState.Initializing, awaitItem(), "View state")
                assertEquals(HistoryViewState.DisplayTags(tags), awaitItem(), "View state")
                cancelAndIgnoreRemainingEvents()
            }
        }
    }

    @Test
    fun `delete tag`() {
        logTestName()

        // Given
        val tagToDelete = Tag()
        coEvery { repository.allTags } returns flow { emit(emptyList()) }
        coEvery { repository.delete(any()) } returns 1

        // When
        viewModel = HistoryViewModel(repository)
        viewModel.handleAction(HistoryViewAction.DeleteTag(tagToDelete))

        // Then
        coVerify { repository.delete(tagToDelete) }
    }
}
