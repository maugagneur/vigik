package com.kidor.vigik.emulate

import android.nfc.cardemulation.HostApduService
import android.os.RemoteException
import com.kidor.vigik.nfc.HostApduManager
import com.kidor.vigik.utils.TestUtils.logTestName
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.anyString
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.atLeastOnce
import org.mockito.kotlin.doAnswer

@RunWith(MockitoJUnitRunner::class)
class EmulateViewModelTest {

    @InjectMocks
    private lateinit var viewModel: EmulateViewModel

    @Mock
    private lateinit var view: EmulateContract.EmulateView
    @Mock
    private lateinit var hostApduManager: HostApduManager

    @Before
    fun setUp() {
        viewModel.setView(view)

        `when`(view.isActive()).thenReturn(true)
    }

    @Test
    fun registerOnStart() {
        logTestName()

        // Run
        viewModel.onStart()

        // Verify
        verify(hostApduManager).register(viewModel)
    }

    @Test
    fun unregisterOnStop() {
        logTestName()

        // Run
        viewModel.onStop()

        // Verify
        verify(hostApduManager).unregister(viewModel)
    }

    @Test
    fun displayErrorWhenUnexpectedConnectionLost() {
        logTestName()

        // Run
        viewModel.onConnectionLost(-1)

        // Verify
        verify(view).addLogLine(anyString())
    }

    @Test
    fun displayErrorWhenConnectionLost() {
        logTestName()

        // Run
        viewModel.onConnectionLost(HostApduService.DEACTIVATION_LINK_LOSS)

        // Verify
        verify(view).addLogLine(anyString())
    }

    @Test
    fun displayErrorWhenWrongAid() {
        logTestName()

        // Run
        viewModel.onConnectionLost(HostApduService.DEACTIVATION_DESELECTED)

        // Verify
        verify(view).addLogLine(anyString())
    }

    @Test
    fun sendApduResponseWhenReceiveAidSelect() {
        logTestName()

        // When
        val receivedApduCommand = byteArrayOf(0x00, 0xA4.toByte(), 0x04, 0x00, 0x42, 0x13, 0x37, 0x42)

        // Run
        viewModel.onApduCommandReceived(receivedApduCommand)

        // Verify
        verify(hostApduManager).sendApduResponse(any())
    }

    @Test
    fun displayLogWhenErrorOccursWhenSendingApduResponse() {
        logTestName()

        // When
        val receivedApduCommand = byteArrayOf(0x00, 0xA4.toByte(), 0x04, 0x00, 0x42, 0x13, 0x37, 0x42)
        `when`(hostApduManager.sendApduResponse(any())).doAnswer { throw RemoteException("Test exception") }

        // Run
        viewModel.onApduCommandReceived(receivedApduCommand)

        // Verify
        verify(hostApduManager).sendApduResponse(any())
        verify(view, atLeastOnce()).addLogLine(anyString())
    }
}