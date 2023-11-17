package com.kidor.vigik.receivers

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothClass
import android.bluetooth.BluetoothDevice
import android.content.Intent
import androidx.core.content.IntentCompat
import com.kidor.vigik.data.bluetooth.BluetoothApiCallback
import com.kidor.vigik.data.bluetooth.BluetoothScanCallback
import com.kidor.vigik.data.bluetooth.model.BluetoothDeviceType
import com.kidor.vigik.utils.AssertUtils.assertEquals
import com.kidor.vigik.utils.TestUtils.logTestName
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockkStatic
import io.mockk.verify
import org.junit.After
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for [BluetoothDiscoveryReceiver].
 */
class BluetoothDiscoveryReceiverTest {

    private lateinit var receiver: BluetoothDiscoveryReceiver

    @MockK
    private lateinit var apiCallback: BluetoothApiCallback
    @MockK
    private lateinit var scanCallback: BluetoothScanCallback
    @MockK
    private lateinit var receivedIntent: Intent
    @MockK
    private lateinit var bluetoothDevice: BluetoothDevice
    @MockK
    private lateinit var deviceClass: BluetoothClass

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        mockkStatic(IntentCompat::class)
        every { IntentCompat.getParcelableExtra(receivedIntent, BluetoothDevice.EXTRA_DEVICE, BluetoothDevice::class.java) } returns bluetoothDevice
        every { IntentCompat.getParcelableExtra(receivedIntent, BluetoothDevice.EXTRA_CLASS, BluetoothClass::class.java) } returns deviceClass

        receiver = BluetoothDiscoveryReceiver().apply {
            registerCallbacks(bluetoothApiCallback = apiCallback, bluetoothScanCallback = scanCallback)
        }
    }

    @After
    fun tearDown() {
        receiver.unregisterCallbacks()
    }

    @Test
    fun `test when receiving invalid intent`() {
        logTestName()

        // Receive an intent without action
        every { receivedIntent.action } returns null
        receiver.onReceive(null, receivedIntent)

        // No notification should occurred
        verify(inverse = true) { apiCallback.onScanningStateChanged(any()) }
        verify(inverse = true) { scanCallback.onDeviceFound(any()) }
    }

    @Test
    fun `test when receiving a ACTION_DISCOVERY_STARTED intent`() {
        logTestName()

        // Receive an BluetoothAdapter.ACTION_DISCOVERY_STARTED intent
        every { receivedIntent.action } returns BluetoothAdapter.ACTION_DISCOVERY_STARTED
        receiver.onReceive(null, receivedIntent)

        // Start scanning notification should occurred
        verify { apiCallback.onScanningStateChanged(isScanning = true) }
        verify(inverse = true) { scanCallback.onDeviceFound(any()) }
    }

    @Test
    fun `test when receiving a ACTION_DISCOVERY_FINISHED intent`() {
        logTestName()

        // Receive an BluetoothAdapter.ACTION_DISCOVERY_FINISHED intent
        every { receivedIntent.action } returns BluetoothAdapter.ACTION_DISCOVERY_FINISHED
        receiver.onReceive(null, receivedIntent)

        // Stop scanning notification should occurred
        verify { apiCallback.onScanningStateChanged(isScanning = false) }
        verify(inverse = true) { scanCallback.onDeviceFound(any()) }
    }

    @Test
    fun `test when receiving a ACTION_FOUND intent`() {
        logTestName()

        // Receive an BluetoothDevice.ACTION_FOUND intent
        val deviceName = "Test Bluetooth device"
        val deviceAddress = "DE:AD:BE:EF"
        val deviceType = BluetoothClass.PROFILE_HEADSET
        every { receivedIntent.action } returns BluetoothDevice.ACTION_FOUND
        every { deviceClass.majorDeviceClass } returns deviceType
        every { bluetoothDevice.name } returns deviceName
        every { bluetoothDevice.address } returns deviceAddress
        receiver.onReceive(null, receivedIntent)

        // Device found notification should occurred
        verify(inverse = true) { apiCallback.onScanningStateChanged(any()) }
        verify {
            scanCallback.onDeviceFound(
                withArg { device ->
                    assertEquals(BluetoothDeviceType.fromMajorBluetoothClass(deviceType), device.type, "Device type")
                    assertEquals(deviceName, device.name, "Device name")
                    assertEquals(deviceAddress, device.hardwareAddress, "Device hardware address")
                }
            )
        }
    }
}
