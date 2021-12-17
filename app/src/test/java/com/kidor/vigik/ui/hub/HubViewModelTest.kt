package com.kidor.vigik.ui.hub

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.kidor.vigik.utils.AssertUtils.assertEquals
import com.kidor.vigik.ui.base.EventWrapper
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
    private lateinit var observer: Observer<EventWrapper<HubViewEvent>>

    @Before
    fun setUp() {
        viewModel = HubViewModel()

        viewModel.viewEvent.observeForever(observer)
    }

    @Test
    fun redirectToReadTag() {
        logTestName()

        // When
        viewModel.handleAction(HubViewAction.DisplayScanTagView)

        // Then
        val event = viewModel.viewEvent.value
        assertEquals(HubViewEvent.NavigateToScanView, event?.peekEvent(), "Navigation event")
    }

    @Test
    fun redirectToTagsHistory() {
        logTestName()

        // When
        viewModel.handleAction(HubViewAction.DisplayTagHistoryView)

        // Then
        val event = viewModel.viewEvent.value
        assertEquals(HubViewEvent.NavigateToHistoryView, event?.peekEvent(), "Navigation event")
    }

    @Test
    fun redirectToEmulateTag() {
        logTestName()

        // When
        viewModel.handleAction(HubViewAction.DisplayEmulateTagView)

        // Then
        val event = viewModel.viewEvent.value
        assertEquals(HubViewEvent.NavigateToEmulateView, event?.peekEvent(), "Navigation event")
    }

    @Test
    fun noAutomaticRedirectionAtStart() {
        logTestName()

        // Then
        val event = viewModel.viewEvent.value
        assertEquals(null, event?.peekEvent(), "Navigation event")
    }
}