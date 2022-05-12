package com.kidor.vigik.ui.emulate

import android.nfc.cardemulation.HostApduService
import android.os.RemoteException
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.kidor.vigik.nfc.hostapdu.HostApduManager
import com.kidor.vigik.utils.AssertUtils.assertEquals
import com.kidor.vigik.utils.TestUtils.logTestName
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer

/**
 * Unit tests for [EmulateViewModel].
 */
@RunWith(MockitoJUnitRunner::class)
class EmulateViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @InjectMocks
    private lateinit var viewModel: EmulateViewModel

    @Mock
    private lateinit var hostApduManager: HostApduManager

    @Mock
    private lateinit var stateObserver: Observer<EmulateViewState>

    @Before
    fun setUp() {
        viewModel.viewState.observeForever(stateObserver)
    }

    @Test
    fun registerOnInit() {
        logTestName()

        // Then
        verify(hostApduManager).register(viewModel)
    }

    @Test
    fun unregisterOnCleared() {
        logTestName()

        // When
        viewModel.onCleared()

        // Then
        verify(hostApduManager).unregister(viewModel)
    }

    @Test
    fun displayErrorWhenUnexpectedConnectionLost() {
        logTestName()

        // When
        viewModel.onConnectionLost(-1)

        // Then
        val viewState = viewModel.viewState.value
        assertEquals(EmulateViewState.DisplayLogLines::class.simpleName, viewState!!::class.simpleName, "View state")
    }

    @Test
    fun displayErrorWhenConnectionLost() {
        logTestName()

        // When
        viewModel.onConnectionLost(HostApduService.DEACTIVATION_LINK_LOSS)

        // Then
        val viewState = viewModel.viewState.value
        assertEquals(EmulateViewState.DisplayLogLines::class.simpleName, viewState!!::class.simpleName, "View state")
    }

    @Test
    fun displayErrorWhenWrongAid() {
        logTestName()

        // When
        viewModel.onConnectionLost(HostApduService.DEACTIVATION_DESELECTED)

        // Then
        val viewState = viewModel.viewState.value
        assertEquals(EmulateViewState.DisplayLogLines::class.simpleName, viewState!!::class.simpleName, "View state")
    }

    @Test
    fun sendApduResponseWhenReceiveAidSelect() {
        logTestName()

        // Given
        val receivedApduCommand = byteArrayOf(0x00, 0xA4.toByte(), 0x04, 0x00, 0x42, 0x13, 0x37, 0x42)

        // When
        viewModel.onApduCommandReceived(receivedApduCommand)

        // Then
        verify(hostApduManager).sendApduResponse(any())
        val viewState = viewModel.viewState.value
        assertEquals(EmulateViewState.DisplayLogLines::class.simpleName, viewState!!::class.simpleName, "View state")
    }

    @Test
    fun sendApduResponseWhenReceiveUnknownCommand() {
        logTestName()

        // Given
        val receivedApduCommand = byteArrayOf(0x42, 0x42, 0x42, 0x42, 0x42)

        // When
        viewModel.onApduCommandReceived(receivedApduCommand)

        // Then
        verify(hostApduManager).sendApduResponse(any())
        val viewState = viewModel.viewState.value
        assertEquals(EmulateViewState.DisplayLogLines::class.simpleName, viewState!!::class.simpleName, "View state")
    }

    @Test
    fun displayLogWhenErrorOccursWhenSendingApduResponse() {
        logTestName()

        // Given
        val receivedApduCommand = byteArrayOf(0x00, 0xA4.toByte(), 0x04, 0x00, 0x42, 0x13, 0x37, 0x42)
        `when`(hostApduManager.sendApduResponse(any())).doAnswer { throw RemoteException("Test exception") }

        // When
        viewModel.onApduCommandReceived(receivedApduCommand)

        // Then
        verify(hostApduManager).sendApduResponse(any())
        val viewState = viewModel.viewState.value
        assertEquals(EmulateViewState.DisplayLogLines::class.simpleName, viewState!!::class.simpleName, "View state")
    }
}
