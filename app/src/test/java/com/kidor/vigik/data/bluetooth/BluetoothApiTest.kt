package com.kidor.vigik.data.bluetooth

import android.location.LocationManager
import app.cash.turbine.test
import com.kidor.vigik.data.bluetooth.model.BluetoothScanError
import com.kidor.vigik.utils.AssertUtils.assertFalse
import com.kidor.vigik.utils.AssertUtils.assertTrue
import com.kidor.vigik.utils.TestUtils.logTestName
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for [BluetoothApi].
 */
class BluetoothApiTest {

    private lateinit var bluetoothApi: BluetoothApi

    @MockK
    private lateinit var bluetoothAdapter: BluetoothAdapter
    @MockK
    private lateinit var locationManager: LocationManager
    @MockK
    private lateinit var bluetoothScanCallback: BluetoothScanCallback

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        every { bluetoothAdapter.isEnabled() } returns false
        every { locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) } returns false
        every { locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) } returns false
        bluetoothApi = BluetoothApi(adapter = bluetoothAdapter, locationManager = locationManager)
    }

    @Test
    fun `check work done at start`() {
        logTestName()

        // Check that API initialized the Bluetooth adapter at start
        verify { bluetoothAdapter.initialize(bluetoothApi) }
    }

    @Test
    fun `check Bluetooth enable state value when Bluetooth state change`() {
        logTestName()

        runTest {
            bluetoothApi.bluetoothEnable.test {
                // Bluetooth: Disable
                bluetoothApi.onBluetoothStateChanged(false)
                // Check that exposed Bluetooth state is disable
                assertFalse(expectMostRecentItem(), "Bluetooth enable")

                // Bluetooth: Disable -> Enable
                bluetoothApi.onBluetoothStateChanged(true)
                // Check that Bluetooth state changes to enable
                assertTrue(awaitItem(), "Bluetooth enable")

                // Bluetooth: Enable -> Enable
                bluetoothApi.onBluetoothStateChanged(true)
                // Check that no new state is emitted
                expectNoEvents()

                // Bluetooth: Enable -> Disable
                bluetoothApi.onBluetoothStateChanged(false)
                // Check that Bluetooth state changes to disable
                assertFalse(awaitItem(), "Bluetooth enable")

                cancelAndIgnoreRemainingEvents()
            }
        }
    }

    @Test
    fun `check location enable state value when location state change`() {
        logTestName()

        runTest {
            bluetoothApi.locationEnable.test {
                // GPS provider -> OFF ; Network provider -> OFF
                every { locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) } returns false
                every { locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) } returns false
                bluetoothApi.onLocationStateChanged()
                // Check that exposed location state is disable
                assertFalse(expectMostRecentItem(), "Location enable")

                // GPS provider -> OFF ; Network provider -> ON
                every { locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) } returns false
                every { locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) } returns true
                bluetoothApi.onLocationStateChanged()
                // Check that location state changes to enable
                assertTrue(awaitItem(), "Location enable")

                // GPS provider -> ON ; Network provider -> ON
                every { locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) } returns true
                every { locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) } returns true
                bluetoothApi.onLocationStateChanged()
                // Check that no new state is emitted
                expectNoEvents()

                // GPS provider -> OFF ; Network provider -> OFF
                every { locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) } returns false
                every { locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) } returns false
                bluetoothApi.onLocationStateChanged()
                // Check that location state changes to disable
                assertFalse(awaitItem(), "Location enable")

                // GPS provider -> ON ; Network provider -> OFF
                every { locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) } returns true
                every { locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) } returns false
                bluetoothApi.onLocationStateChanged()
                // Check that location state changes to enable
                assertTrue(awaitItem(), "Location enable")

                cancelAndIgnoreRemainingEvents()
            }
        }
    }

    @Test
    fun `check scanning state value when scanning state change`() {
        logTestName()

        runTest {
            bluetoothApi.isScanning.test {
                // Scan: ON
                bluetoothApi.onScanningStateChanged(true)
                // Check that exposed 'Is scanning' state is true
                assertTrue(expectMostRecentItem(), "Is scanning")

                // Scan: ON -> OFF
                bluetoothApi.onScanningStateChanged(false)
                // Check that 'Is scanning' state changes to false
                assertFalse(awaitItem(), "Is scanning")

                // Scan: OFF -> OFF
                bluetoothApi.onScanningStateChanged(false)
                // Check that no new state is emitted
                expectNoEvents()

                // Scan: OFF -> ON
                bluetoothApi.onScanningStateChanged(true)
                // Check that 'Is scanning' state changes to true
                assertTrue(awaitItem(), "Is scanning")

                cancelAndIgnoreRemainingEvents()
            }
        }
    }

    @Test
    fun `check that starting a scan when already scanning produces an error`() {
        logTestName()

        // Simulate a running scan
        bluetoothApi.onScanningStateChanged(true)

        // Start new scan
        bluetoothApi.startScan(false, bluetoothScanCallback)

        // Check that the appropriated error is emitted
        verify(exactly = 1) { bluetoothScanCallback.onScanError(BluetoothScanError.SCAN_FAILED_ALREADY_STARTED) }
        // Check that we do not start a new scan
        verify(inverse = true) { bluetoothAdapter.startScan(any(), any()) }

        // Start new LE scan
        bluetoothApi.startScan(true, bluetoothScanCallback)

        // Check that the appropriated error is emitted
        verify(exactly = 2) { bluetoothScanCallback.onScanError(BluetoothScanError.SCAN_FAILED_ALREADY_STARTED) }
        // Check that we do not start a new scan
        verify(inverse = true) { bluetoothAdapter.startScan(any(), any()) }
    }

    @Test
    fun `check that scan request when not scanning start a scan`() {
        logTestName()

        // Start new scan
        bluetoothApi.startScan(false, bluetoothScanCallback)

        // Check that we start a new scan
        verify { bluetoothAdapter.startScan(false, bluetoothScanCallback) }

        // Start new LE scan
        bluetoothApi.startScan(true, bluetoothScanCallback)

        // Check that we start a new LE scan
        verify { bluetoothAdapter.startScan(true, bluetoothScanCallback) }
    }

    @Test
    fun `check stopScan() behavior depending of scan state`() {
        logTestName()

        // When the device is not scanning
        bluetoothApi.onScanningStateChanged(false)
        // Stop scan
        bluetoothApi.stopScan()
        // Then adapter should not be asked to stop scan
        verify(inverse = true) { bluetoothAdapter.stopScan() }

        // When the device is scanning
        bluetoothApi.onScanningStateChanged(true)
        // Stop scan
        bluetoothApi.stopScan()
        // Then adapter should be asked to stop scan
        verify { bluetoothAdapter.stopScan() }
    }
}
