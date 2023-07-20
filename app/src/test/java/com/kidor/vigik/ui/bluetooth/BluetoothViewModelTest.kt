package com.kidor.vigik.ui.bluetooth

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.asFlow
import app.cash.turbine.test
import com.kidor.vigik.MainCoroutineRule
import com.kidor.vigik.R
import com.kidor.vigik.data.Localization
import com.kidor.vigik.data.bluetooth.BluetoothApi
import com.kidor.vigik.data.bluetooth.BluetoothScanCallback
import com.kidor.vigik.data.bluetooth.model.BluetoothDevice
import com.kidor.vigik.data.bluetooth.model.BluetoothDeviceType
import com.kidor.vigik.data.bluetooth.model.BluetoothScanError
import com.kidor.vigik.utils.AssertUtils.assertEquals
import com.kidor.vigik.utils.AssertUtils.assertFalse
import com.kidor.vigik.utils.AssertUtils.assertNull
import com.kidor.vigik.utils.AssertUtils.assertTrue
import com.kidor.vigik.utils.TestUtils.logTestName
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

private const val START_SCAN_ERROR_MESSAGE = "Fail to start Bluetooth scan. Please check that Bluetooth is ON!"

/**
 * Unit tests for [BluetoothViewModel].
 */
class BluetoothViewModelTest {

    @ExperimentalCoroutinesApi
    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: BluetoothViewModel

    @MockK
    private lateinit var bluetoothApi: BluetoothApi
    private val bluetoothEnableFlow = MutableStateFlow(false)
    private val locationEnableFlow = MutableStateFlow(false)
    private val isScanningFlow = MutableStateFlow(false)
    @MockK
    private lateinit var localization: Localization

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        every { bluetoothApi.bluetoothEnable } returns bluetoothEnableFlow
        every { bluetoothApi.locationEnable } returns locationEnableFlow
        every { bluetoothApi.isScanning } returns isScanningFlow
        every { localization.getString(R.string.bluetooth_start_scan_error_message) } returns START_SCAN_ERROR_MESSAGE
        viewModel = BluetoothViewModel(
            ioDispatcher = mainCoroutineRule.testDispatcher,
            bluetoothApi = bluetoothApi,
            localization = localization
        )
    }

    @Test
    fun `check Bluetooth enable status bound to Bluetooth API`() {
        logTestName()

        runTest {
            // Disable Bluetooth
            bluetoothEnableFlow.emit(false)
            // Check that Bluetooth is not enable in view state
            var isBluetoothEnable = viewModel.viewState.value?.isBluetoothEnable
            assertFalse(isBluetoothEnable, "Is Bluetooth enable")

            // Enable Bluetooth
            bluetoothEnableFlow.emit(true)
            // Check that Bluetooth is enable in view state
            isBluetoothEnable = viewModel.viewState.value?.isBluetoothEnable
            assertTrue(isBluetoothEnable, "Is Bluetooth enable")

            // Disable Bluetooth
            bluetoothEnableFlow.emit(false)
            // Check that Bluetooth is not enable in view state
            isBluetoothEnable = viewModel.viewState.value?.isBluetoothEnable
            assertFalse(isBluetoothEnable, "Is Bluetooth enable")
        }
    }

    @Test
    fun `check location enable status bound to Bluetooth API`() {
        logTestName()

        runTest {
            // Disable location
            locationEnableFlow.emit(false)
            // Check that location is not enable in view state
            var isLocationEnable = viewModel.viewState.value?.isLocationEnable
            assertFalse(isLocationEnable, "Is location enable")

            // Enable location
            locationEnableFlow.emit(true)
            // Check that location is enable in view state
            isLocationEnable = viewModel.viewState.value?.isLocationEnable
            assertTrue(isLocationEnable, "Is location enable")

            // Disable location
            locationEnableFlow.emit(false)
            // Check that location is not enable in view state
            isLocationEnable = viewModel.viewState.value?.isLocationEnable
            assertFalse(isLocationEnable, "Is location enable")
        }
    }

    @Test
    fun `check scanning status bound to Bluetooth API`() {
        logTestName()

        runTest {
            // Scanning
            isScanningFlow.emit(false)
            // Check that the scan is running in view state
            var isScanning = viewModel.viewState.value?.isScanning
            assertFalse(isScanning, "Is scanning")

            // Not scanning
            isScanningFlow.emit(true)
            // Check that the scan stopped in view state
            isScanning = viewModel.viewState.value?.isScanning
            assertTrue(isScanning, "Is scanning")

            // Scanning
            isScanningFlow.emit(false)
            // Check that the scan is running in view state
            isScanning = viewModel.viewState.value?.isScanning
            assertFalse(isScanning, "Is scanning")
        }
    }

    @Test
    fun `check view state when Bluetooth scan produces an error`() {
        logTestName()

        val scanCallback = slot<BluetoothScanCallback>()

        runTest {
            viewModel.viewState.asFlow().test {
                // Mock startScan() call -> SCAN_FAILED_TO_START
                every { bluetoothApi.startScan(capture(scanCallback)) } answers {
                    scanCallback.captured.onScanError(BluetoothScanError.SCAN_FAILED_TO_START)
                }

                // Start Bluetooth scan
                viewModel.handleAction(BluetoothViewAction.StartBluetoothScan)

                // Check Bluetooth API is called
                verify(exactly = 1) { bluetoothApi.startScan(any()) }
                // Check UI state
                expectMostRecentItem().let { viewState ->
                    assertTrue(viewState.detectedDevices.isEmpty(), "Detected device list is empty")
                    assertEquals(START_SCAN_ERROR_MESSAGE, viewState.errorMessage, "Error message")
                }

                // Mock startScan() call -> SCAN_FAILED_UNKNOWN_REASON
                every { bluetoothApi.startScan(capture(scanCallback)) } answers {
                    scanCallback.captured.onScanError(BluetoothScanError.SCAN_FAILED_UNKNOWN_REASON)
                }

                // Start Bluetooth scan
                viewModel.handleAction(BluetoothViewAction.StartBluetoothScan)

                // Check that detected devices list and error message are clean
                awaitItem().let { viewState ->
                    assertTrue(viewState.detectedDevices.isEmpty(), "Detected device list is empty")
                    assertNull(viewState.errorMessage, "Error message")
                }

                // Check Bluetooth API is called
                verify(exactly = 2) { bluetoothApi.startScan(any()) }
                // Check UI state
                awaitItem().let { viewState ->
                    assertTrue(viewState.detectedDevices.isEmpty(), "Detected device list is empty")
                    assertEquals(BluetoothScanError.SCAN_FAILED_UNKNOWN_REASON.name, viewState.errorMessage, "Error message")
                }

                cancelAndIgnoreRemainingEvents()
            }
        }
    }

    @Test
    fun `check view state when Bluetooth scan detects some devices`() {
        logTestName()

        // Mock startScan() call
        val scanCallback = slot<BluetoothScanCallback>()
        every { bluetoothApi.startScan(capture(scanCallback)) } returns Unit

        val bluetoothDevice1 = BluetoothDevice(BluetoothDeviceType.COMPUTER, "Computer", "00:11:22:33:44:55:66")
        val bluetoothDevice2 = BluetoothDevice(BluetoothDeviceType.PHONE, "Phone", "01:23:45:67:89:AB:CD:EF")
        val bluetoothDevice3 = BluetoothDevice(BluetoothDeviceType.UNKNOWN, "Unknown device", "DE:AD:BE:EF")

        runTest {
            viewModel.viewState.asFlow().test {
                // Start Bluetooth scan
                viewModel.handleAction(BluetoothViewAction.StartBluetoothScan)

                // 3 devices detected (one of them is detected twice)
                scanCallback.captured.onDeviceFound(bluetoothDevice1)
                scanCallback.captured.onDeviceFound(bluetoothDevice2)
                scanCallback.captured.onDeviceFound(bluetoothDevice3)
                scanCallback.captured.onDeviceFound(bluetoothDevice2)

                // Check Bluetooth API is called
                verify(exactly = 1) { bluetoothApi.startScan(any()) }
                // Check UI state
                expectMostRecentItem().let { viewState ->
                    assertEquals(3, viewState.detectedDevices.size, "Detected device list size")
                    assertTrue(viewState.detectedDevices.contains(bluetoothDevice1), "Detected device list contains device 1")
                    assertTrue(viewState.detectedDevices.contains(bluetoothDevice2), "Detected device list contains device 2")
                    assertTrue(viewState.detectedDevices.contains(bluetoothDevice3), "Detected device list contains device 3")
                    assertNull(viewState.errorMessage, "Error message")
                }

                // Start Bluetooth scan
                viewModel.handleAction(BluetoothViewAction.StartBluetoothScan)

                // Check that detected devices list and error message are clean
                awaitItem().let { viewState ->
                    assertTrue(viewState.detectedDevices.isEmpty(), "Detected device list is empty")
                    assertNull(viewState.errorMessage, "Error message")
                }

                // Only 1 device detected
                scanCallback.captured.onDeviceFound(bluetoothDevice1)

                // Check Bluetooth API is called
                verify(exactly = 2) { bluetoothApi.startScan(any()) }
                // Check UI state
                awaitItem().let { viewState ->
                    assertEquals(1, viewState.detectedDevices.size, "Detected device list size")
                    assertTrue(viewState.detectedDevices.contains(bluetoothDevice1), "Detected device list contains device 1")
                    assertNull(viewState.errorMessage, "Error message")
                }

                cancelAndIgnoreRemainingEvents()
            }
        }
    }
}
