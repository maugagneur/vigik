package com.kidor.vigik.ui.emulate

import android.nfc.cardemulation.HostApduService
import android.os.RemoteException
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.kidor.vigik.nfc.hostapdu.HostApduManager
import com.kidor.vigik.utils.AssertUtils.assertTrue
import com.kidor.vigik.utils.TestUtils.logTestName
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Unit tests for [EmulateViewModel].
 */
class EmulateViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @InjectMockKs
    private lateinit var viewModel: EmulateViewModel

    @MockK
    private lateinit var hostApduManager: HostApduManager

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        every { hostApduManager.unregister(any()) } returns true
    }

    @Test
    fun registerOnInit() {
        logTestName()

        // Then
        verify { hostApduManager.register(viewModel) }
    }

    @Test
    fun unregisterOnCleared() {
        logTestName()

        // When
        viewModel.onCleared()

        // Then
        verify { hostApduManager.unregister(viewModel) }
    }

    @Test
    fun displayErrorWhenUnexpectedConnectionLost() {
        logTestName()

        // When
        viewModel.onConnectionLost(-1)

        // Then
        val viewState = viewModel.viewState.value
        assertTrue(viewState is EmulateViewState.DisplayLogLines, "View state")
    }

    @Test
    fun displayErrorWhenConnectionLost() {
        logTestName()

        // When
        viewModel.onConnectionLost(HostApduService.DEACTIVATION_LINK_LOSS)

        // Then
        val viewState = viewModel.viewState.value
        assertTrue(viewState is EmulateViewState.DisplayLogLines, "View state")
    }

    @Test
    fun displayErrorWhenWrongAid() {
        logTestName()

        // When
        viewModel.onConnectionLost(HostApduService.DEACTIVATION_DESELECTED)

        // Then
        val viewState = viewModel.viewState.value
        assertTrue(viewState is EmulateViewState.DisplayLogLines, "View state")
    }

    @Test
    fun sendApduResponseWhenReceiveAidSelect() {
        logTestName()

        // Given
        val receivedApduCommand = byteArrayOf(0x00, 0xA4.toByte(), 0x04, 0x00, 0x42, 0x13, 0x37, 0x42)

        // When
        viewModel.onApduCommandReceived(receivedApduCommand)

        // Then
        verify { hostApduManager.sendApduResponse(any()) }
        val viewState = viewModel.viewState.value
        assertTrue(viewState is EmulateViewState.DisplayLogLines, "View state")
    }

    @Test
    fun sendApduResponseWhenReceiveUnknownCommand() {
        logTestName()

        // Given
        val receivedApduCommand = byteArrayOf(0x42, 0x42, 0x42, 0x42, 0x42)

        // When
        viewModel.onApduCommandReceived(receivedApduCommand)

        // Then
        verify { hostApduManager.sendApduResponse(any()) }
        val viewState = viewModel.viewState.value
        assertTrue(viewState is EmulateViewState.DisplayLogLines, "View state")
    }

    @Test
    fun displayLogWhenErrorOccursWhenSendingApduResponse() {
        logTestName()

        // Given
        val receivedApduCommand = byteArrayOf(0x00, 0xA4.toByte(), 0x04, 0x00, 0x42, 0x13, 0x37, 0x42)
        every { hostApduManager.sendApduResponse(any()) } answers { throw RemoteException("Test exception") }

        // When
        viewModel.onApduCommandReceived(receivedApduCommand)

        // Then
        verify { hostApduManager.sendApduResponse(any()) }
        val viewState = viewModel.viewState.value
        assertTrue(viewState is EmulateViewState.DisplayLogLines, "View state")
    }
}
