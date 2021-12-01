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
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.doAnswer
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

        // Given
        `when`(repository.allTags).doSuspendableAnswer { flow { emit(emptyList()) } }

        // When
        mainCoroutineRule.testDispatcher.advanceTimeBy(1000)

        // Then
        val state = viewModel.viewState.value
        AssertUtils.assertEquals(HistoryViewState.NoTag, state, "View state")
    }
}