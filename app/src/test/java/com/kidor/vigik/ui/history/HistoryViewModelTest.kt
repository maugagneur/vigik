package com.kidor.vigik.ui.history

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import androidx.lifecycle.asFlow
import app.cash.turbine.test
import com.kidor.vigik.MainCoroutineRule
import com.kidor.vigik.db.TagDao
import com.kidor.vigik.db.TagRepository
import com.kidor.vigik.nfc.model.Tag
import com.kidor.vigik.utils.AssertUtils.assertEquals
import com.kidor.vigik.utils.TestUtils.logTestName
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

/**
 * Unit tests for [HistoryViewModel].
 */
@RunWith(JUnit4::class)
class HistoryViewModelTest {

    @ExperimentalCoroutinesApi
    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    //@InjectMockKs
    private lateinit var viewModel: HistoryViewModel

    @MockK
    private lateinit var repository: TagRepository

    @RelaxedMockK
    private lateinit var tagDao: TagDao

    @MockK
    private lateinit var stateObserver: Observer<HistoryViewState>

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
    }

    @ExperimentalCoroutinesApi
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

    @ExperimentalCoroutinesApi
    @Test
    fun displayEmptyHistoryWhenNoTagInRepository() {
        logTestName()

        // Given
        coEvery { repository.allTags } returns flowOf(emptyList())

        runTest {
            // When
            viewModel = HistoryViewModel(repository)
            viewModel.viewState.asFlow().test {
                // Then
                assertEquals(HistoryViewState.NoTag, awaitItem(), "View state")
                cancelAndIgnoreRemainingEvents()
            }
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun displayOneTag() {
        logTestName()

        // Given
        val tags = listOf(Tag(System.currentTimeMillis(), byteArrayOf(0x13, 0x37), "Tech list", "Data", byteArrayOf(0x42)))
        coEvery { repository.allTags } returns flowOf(tags)

        runTest {
            // When
            viewModel = HistoryViewModel(repository)
            viewModel.viewState.asFlow().test {
                // Then
                assertEquals(HistoryViewState.DisplayTags(tags), awaitItem(), "View state")
                cancelAndIgnoreRemainingEvents()
            }
        }
    }
}