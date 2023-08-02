package com.kidor.vigik.data.bluetooth

import android.bluetooth.BluetoothDevice
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.content.Intent
import com.kidor.vigik.data.bluetooth.model.BluetoothDeviceType
import com.kidor.vigik.receivers.BluetoothDiscoveryReceiver
import com.kidor.vigik.utils.AssertUtils.assertFalse
import com.kidor.vigik.utils.AssertUtils.assertTrue
import com.kidor.vigik.utils.TestUtils.logTestName
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
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
    private lateinit var bluetoothLeScanner: BluetoothLeScanner
    @MockK
    private lateinit var bluetoothDiscoveryReceiver: BluetoothDiscoveryReceiver
    @MockK
    private lateinit var bluetoothApiCallback: BluetoothApiCallback
    @MockK
    private lateinit var bluetoothScanCallback: BluetoothScanCallback
    @MockK
    private lateinit var scanResult: ScanResult
    @MockK
    private lateinit var bluetoothDevice: BluetoothDevice

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        every { bluetoothAdapter.bluetoothLeScanner } returns bluetoothLeScanner
        every { context.registerReceiver(any(), any()) } returns Intent()
    }

    /**
     * Builds a [BluetoothAdapter] for test purpose.
     *
     * @param testScope The [CoroutineScope] to use during the test.
     */
    private fun buildAdapter(testScope: CoroutineScope) {
        adapter = BluetoothAdapter(
            context = context,
            coroutineScope = testScope,
            bluetoothAdapter = bluetoothAdapter,
            bluetoothDiscoveryReceiver = bluetoothDiscoveryReceiver
        )
        adapter.initialize(bluetoothApiCallback)
    }

    @Test
    fun `check Bluetooth enable status bound to device Bluetooth adapter`() {
        logTestName()

        runTest {
            buildAdapter(backgroundScope)

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
    }

    @Test
    fun `check Bluetooth scan behavior when start discovery fails`() {
        logTestName()

        runTest {
            buildAdapter(backgroundScope)

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
    }

    @Test
    fun `check Bluetooth scan behavior when no error occurs`() {
        logTestName()

        runTest {
            buildAdapter(backgroundScope)

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

    @Test
    fun `check start BLE scan behavior`() {
        logTestName()

        runTest {
            buildAdapter(backgroundScope)

            // Start BLE scan
            adapter.startScan(true, bluetoothScanCallback)

            // Check that startDiscovery() is never called
            verify(inverse = true) { bluetoothAdapter.startDiscovery() }
            // Check that API is notified that a scan is running
            verify { bluetoothApiCallback.onScanningStateChanged(true) }
            // Check that the BLE scan is started
            verify { bluetoothLeScanner.startScan(adapter) }

            // Check that the scan is stopped after 10 seconds
            delay(10_000 + 1)
            // Check that the BLE scan is stopped
            verify { bluetoothLeScanner.stopScan(adapter) }
            // Check that API is notified that the scan is not running anymore
            verify { bluetoothApiCallback.onScanningStateChanged(false) }
        }
    }

    @Test
    fun `check BLE errors handling`() {
        logTestName()

        runTest {
            buildAdapter(backgroundScope)

            // Start BLE scan
            adapter.startScan(true, bluetoothScanCallback)

            // Simulate an error from BLE scan
            adapter.onScanFailed(ScanCallback.SCAN_FAILED_ALREADY_STARTED)

            // Check that the error is forwarded to the scan's initiator
            verify { bluetoothScanCallback.onScanError(any()) }
            // Check that the BLE scan is stopped
            verify { bluetoothLeScanner.stopScan(adapter) }
            // Check that API is notified that the scan is not running anymore
            verify { bluetoothApiCallback.onScanningStateChanged(false) }
        }
    }

    @Test
    fun `check BLE device found handling`() {
        logTestName()

        val deviceName = "Test device"
        val deviceAddress = "FF:EE:AA:77:00:42"
        every { scanResult.device } returns bluetoothDevice
        every { bluetoothDevice.bluetoothClass } returns null
        every { bluetoothDevice.name } returns deviceName
        every { bluetoothDevice.address } returns deviceAddress

        runTest {
            buildAdapter(backgroundScope)

            // Start BLE scan
            adapter.startScan(true, bluetoothScanCallback)

            // Simulate a BLE device found
            adapter.onScanResult(0, scanResult)

            // Check that the Bluetooth device is forwarded to the scan's initiator
            verify {
                bluetoothScanCallback.onDeviceFound(
                    com.kidor.vigik.data.bluetooth.model.BluetoothDevice(
                        type = BluetoothDeviceType.UNKNOWN,
                        name = deviceName,
                        hardwareAddress = deviceAddress
                    )
                )
            }
        }
    }
}
