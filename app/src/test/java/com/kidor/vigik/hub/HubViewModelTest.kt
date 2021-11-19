package com.kidor.vigik.hub

import com.kidor.vigik.utils.TestUtils.logTestName
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class HubViewModelTest {

    private lateinit var viewModel: HubContract.HubViewModel

    @Mock
    private lateinit var view: HubContract.HubView

    @Before
    fun setUp() {
        viewModel = HubViewModel()
        viewModel.setView(view)

        `when`(view.isActive()).thenReturn(true)
    }

    @Test
    fun redirectToReadTag() {
        logTestName()

        // Run
        viewModel.onActionReadTag()

        // Verify
        verify(view).goToReadTag()
        verify(view, never()).goToEmulatedTag()
    }

    @Test
    fun redirectToEmulateTag() {
        logTestName()

        // Run
        viewModel.onActionEmulateTag()

        // Verify
        verify(view, never()).goToReadTag()
        verify(view).goToEmulatedTag()
    }

    @Test
    fun noAutomaticRedirectionAtStart() {
        logTestName()

        // Run
        viewModel.onStart()

        // Verify
        verify(view, never()).goToReadTag()
        verify(view, never()).goToEmulatedTag()
    }
}