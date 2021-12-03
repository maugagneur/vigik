package com.kidor.vigik.ui.history

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.kidor.vigik.MainCoroutineRule
import com.kidor.vigik.db.TagDao
import com.kidor.vigik.db.TagRepository
import com.kidor.vigik.nfc.model.Tag
import com.kidor.vigik.utils.AssertUtils
import com.kidor.vigik.utils.TestUtils.logTestName
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.doSuspendableAnswer

/**
 * Unit tests for [HistoryViewModel].
 */
@RunWith(MockitoJUnitRunner::class)
class HistoryViewModelTest {

    @ExperimentalCoroutinesApi
    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: HistoryViewModel

    @Mock
    private lateinit var repository: TagRepository

    @Mock
    private lateinit var tagDao: TagDao

    @Mock
    private lateinit var stateObserver: Observer<HistoryViewState>

    @Before
    fun setUp() {
        repository = TagRepository(tagDao)
        viewModel = HistoryViewModel(repository)
        viewModel.viewState.observeForever(stateObserver)
    }

    @Test
    fun initialState() {
        logTestName()

        // Then
        val state = viewModel.viewState.value
        AssertUtils.assertEquals(HistoryViewState.Initializing, state, "State at start")
    }

    @Ignore("Not working")
    @ExperimentalCoroutinesApi
    @Test
    fun displayEmptyHistoryWhenNoTagInRepository() {
        logTestName()
        runBlockingTest {
            // Given
            `when`(repository.allTags).doSuspendableAnswer {
                flow {
                    emit(emptyList())
                }
            }

            // Then
            var state = viewModel.viewState.value
            AssertUtils.assertEquals(HistoryViewState.Initializing, state, "View state")

            // When
            mainCoroutineRule.testDispatcher.advanceTimeBy(10)

            // Then
            state = viewModel.viewState.value
            AssertUtils.assertEquals(HistoryViewState.NoTag, state, "View state")
        }
    }

    @Ignore("Not working")
    @ExperimentalCoroutinesApi
    @Test
    fun displayOneTag() {
        logTestName()
        runBlockingTest {
            // Given
            val tags = listOf(Tag())
            val tagFlow = flow {
                emit(emptyList())
                delay(100)
                emit(tags)
            }
            `when`(repository.allTags).doSuspendableAnswer { tagFlow }

            // Then
            var state = viewModel.viewState.value
            AssertUtils.assertEquals(HistoryViewState.Initializing, state, "View state")

            // When
            mainCoroutineRule.testDispatcher.advanceTimeBy(100)

            // Then
            state = viewModel.viewState.value
            AssertUtils.assertEquals(HistoryViewState.DisplayTags(tags), state, "View state")
        }
    }
}