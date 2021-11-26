package com.kidor.vigik.ui.hub

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.kidor.vigik.utils.AssertUtils.assertEquals
import com.kidor.vigik.utils.Event
import com.kidor.vigik.utils.TestUtils.logTestName
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class HubViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: HubViewModel

    @Mock
    private lateinit var observer: Observer<Event<HubViewEvent>>

    @Before
    fun setUp() {
        viewModel = HubViewModel()

        viewModel.viewEvent.observeForever(observer)
    }

    @Test
    fun redirectToReadTag() {
        logTestName()

        // When
        viewModel.onActionReadTag()

        // Then
        val event = viewModel.viewEvent.value
        assertEquals(HubViewEvent.NavigateToScanView, event?.peekContent(), "Navigation event")
    }

    @Test
    fun redirectToTagsHistory() {
        logTestName()

        // When
        viewModel.onActionTagHistory()

        // Then
        val event = viewModel.viewEvent.value
        assertEquals(HubViewEvent.NavigateToHistoryView, event?.peekContent(), "Navigation event")
    }

    @Test
    fun redirectToEmulateTag() {
        logTestName()

        // When
        viewModel.onActionEmulateTag()

        // Then
        val event = viewModel.viewEvent.value
        assertEquals(HubViewEvent.NavigateToEmulateView, event?.peekContent(), "Navigation event")
    }

    @Test
    fun noAutomaticRedirectionAtStart() {
        logTestName()

        // Then
        val event = viewModel.viewEvent.value
        assertEquals(null, event?.peekContent(), "Navigation event")
    }
}