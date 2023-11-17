package com.kidor.vigik.receivers

import android.bluetooth.BluetoothAdapter
import android.content.Intent
import com.kidor.vigik.data.bluetooth.BluetoothApi
import com.kidor.vigik.utils.TestUtils.logTestName
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.junit.Before
import org.junit.Test

/**
 * Unit test for [BluetoothStateReceiver].
 */
class BluetoothStateReceiverTest {

    private lateinit var receiver: BluetoothStateReceiver

    @MockK
    private lateinit var bluetoothApi: BluetoothApi
    @MockK
    private lateinit var receivedIntent: Intent

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        receiver = BluetoothStateReceiver(bluetoothApi)
    }

    @Test
    fun `test when receiving invalid intent`() {
        logTestName()

        // Receive an intent without action
        every { receivedIntent.action } returns null
        receiver.onReceive(null, receivedIntent)

        // No notification should occurred
        verify(inverse = true) { bluetoothApi.onBluetoothStateChanged(any()) }

        // Receive an intent with invalid extra data
        every { receivedIntent.action } returns BluetoothAdapter.ACTION_STATE_CHANGED
        every { receivedIntent.getIntExtra(BluetoothAdapter.EXTRA_STATE, any()) } returns -1
        receiver.onReceive(null, receivedIntent)

        // No notification should occurred
        verify(inverse = true) { bluetoothApi.onBluetoothStateChanged(any()) }
    }

    @Test
    fun `test when Bluetooth state turns ON`() {
        logTestName()

        // Send the intent
        every { receivedIntent.action } returns BluetoothAdapter.ACTION_STATE_CHANGED
        every { receivedIntent.getIntExtra(BluetoothAdapter.EXTRA_STATE, any()) } returns BluetoothAdapter.STATE_ON
        receiver.onReceive(null, receivedIntent)

        // Check that Bluetooth API is notified that Bluetooth turned ON
        verify { bluetoothApi.onBluetoothStateChanged(true) }
    }

    @Test
    fun `test when Bluetooth state turns OFF`() {
        logTestName()

        // Send the intent
        every { receivedIntent.action } returns BluetoothAdapter.ACTION_STATE_CHANGED
        every { receivedIntent.getIntExtra(BluetoothAdapter.EXTRA_STATE, any()) } returns BluetoothAdapter.STATE_OFF
        receiver.onReceive(null, receivedIntent)

        // Check that Bluetooth API is notified that Bluetooth turned OFF
        verify { bluetoothApi.onBluetoothStateChanged(false) }
    }
}
