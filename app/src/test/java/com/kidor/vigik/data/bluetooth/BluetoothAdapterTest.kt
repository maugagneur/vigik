package com.kidor.vigik.data.bluetooth

import android.content.Context
import android.content.Intent
import com.kidor.vigik.receivers.BluetoothDiscoveryReceiver
import com.kidor.vigik.utils.AssertUtils.assertFalse
import com.kidor.vigik.utils.AssertUtils.assertTrue
import com.kidor.vigik.utils.TestUtils.logTestName
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for [BluetoothAdapter].
 */
class BluetoothAdapterTest {

    private lateinit var adapter: BluetoothAdapter

    @MockK
    private lateinit var context: Context
    @MockK
    private lateinit var bluetoothAdapter: android.bluetooth.BluetoothAdapter
    @MockK
    private lateinit var bluetoothDiscoveryReceiver: BluetoothDiscoveryReceiver
    @MockK
    private lateinit var bluetoothApiCallback: BluetoothApiCallback
    @MockK
    private lateinit var bluetoothScanCallback: BluetoothScanCallback

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        every { context.registerReceiver(any(), any()) } returns Intent()
        adapter = BluetoothAdapter(
            context = context,
            bluetoothAdapter = bluetoothAdapter,
            bluetoothDiscoveryReceiver = bluetoothDiscoveryReceiver
        )
        adapter.initialize(bluetoothApiCallback)
    }

    @Test
    fun `check Bluetooth enable status bound to device Bluetooth adapter`() {
        logTestName()

        // Enable device adapter
        every { bluetoothAdapter.isEnabled } returns true
        // Check that adapter is enable
        assertTrue(adapter.isEnabled(), "Is adapter enable")

        // Disable device adapter
        every { bluetoothAdapter.isEnabled } returns false
        // Check that adapter is disable
        assertFalse(adapter.isEnabled(), "Is adapter enable")

        // Enable device adapter
        every { bluetoothAdapter.isEnabled } returns true
        // Check that adapter is enable
        assertTrue(adapter.isEnabled(), "Is adapter enable")
    }

    @Test
    fun `check Bluetooth scan behavior when start discovery fails`() {
        logTestName()

        // Mock startDiscovery() to simulate an error
        every { bluetoothAdapter.startDiscovery() } returns false

        // Start Bluetooth scan
        adapter.startScan(false, bluetoothScanCallback)

        // Check that LE scan is not started
        verify(inverse = true) { bluetoothAdapter.bluetoothLeScanner.startScan(any()) }
        // Check that receiver is registered and configured
        verify { bluetoothDiscoveryReceiver.registerCallbacks(bluetoothApiCallback, bluetoothScanCallback) }
        verify { context.registerReceiver(bluetoothDiscoveryReceiver, BluetoothDiscoveryReceiver.INTENT_FILTER) }
        // Check that startDiscovery() is called
        verify { bluetoothAdapter.startDiscovery() }
        // Check that error callback is called
        verify { bluetoothScanCallback.onScanError(any()) }
    }

    @Test
    fun `check Bluetooth scan behavior when no error occurs`() {
        logTestName()

        // Mock startDiscovery() to simulate an error
        every { bluetoothAdapter.startDiscovery() } returns true

        // Start Bluetooth scan
        adapter.startScan(false, bluetoothScanCallback)

        // Check that LE scan is not started
        verify(inverse = true) { bluetoothAdapter.bluetoothLeScanner.startScan(any()) }
        // Check that receiver is registered and configured
        verify { bluetoothDiscoveryReceiver.registerCallbacks(bluetoothApiCallback, bluetoothScanCallback) }
        verify { context.registerReceiver(bluetoothDiscoveryReceiver, BluetoothDiscoveryReceiver.INTENT_FILTER) }
        // Check that startDiscovery() is called
        verify { bluetoothAdapter.startDiscovery() }
        // Check that error callback is never called
        verify(inverse = true) { bluetoothScanCallback.onScanError(any()) }
    }
}
