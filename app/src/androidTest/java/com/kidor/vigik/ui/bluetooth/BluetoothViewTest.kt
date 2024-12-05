package com.kidor.vigik.ui.bluetooth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.kidor.vigik.R
import com.kidor.vigik.data.bluetooth.model.BluetoothDevice
import com.kidor.vigik.data.bluetooth.model.BluetoothDeviceType
import com.kidor.vigik.extensions.onNodeWithText
import com.kidor.vigik.ui.theme.dimensions
import com.kidor.vigik.utils.TestUtils.logTestName
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations

/**
 * Integration tests for Bluetooth screen.
 */
@RunWith(AndroidJUnit4::class)
class BluetoothViewTest {

    private lateinit var closeable: AutoCloseable

    @Mock
    private lateinit var startBluetoothScanCallback: () -> Unit

    @Before
    fun setUp() {
        closeable = MockitoAnnotations.openMocks(this)
    }

    @After
    fun tearDown() {
        closeable.close()
    }

    @Composable
    private fun BluetoothViewForTest(viewState: BluetoothViewState) {
        Column(
            modifier = Modifier.padding(MaterialTheme.dimensions.commonSpaceMedium),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            BluetoothAdapterStatus(isEnable = viewState.isBluetoothEnable)
            Spacer(modifier = Modifier.height(MaterialTheme.dimensions.commonSpaceLarge))
            LocationStatus(isEnable = viewState.isLocationEnable)
            Spacer(modifier = Modifier.height(MaterialTheme.dimensions.commonSpaceLarge))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(MaterialTheme.dimensions.commonSpaceXSmall))
            BluetoothScanStatus(
                scanInProgress = viewState.isScanning,
                onRefreshDevicesClick = startBluetoothScanCallback
            )
            val errorMessage = viewState.errorMessage
            if (errorMessage == null) {
                DetectedBluetoothDeviceList(devices = viewState.detectedDevices)
            } else {
                ErrorMessage(errorMessage)
            }
        }
    }

    @ExperimentalTestApi
    @Test
    fun checkViewWithNothingEnable() {
        logTestName()

        runComposeUiTest {
            setContent {
                BluetoothViewForTest(
                    viewState = BluetoothViewState(
                        isBluetoothEnable = false,
                        isLocationEnable = false,
                        isScanning = false,
                        detectedDevices = emptyList(),
                        errorMessage = null
                    )
                )
            }

            // Check visibility
            onNodeWithText(stringResourceId = R.string.bluetooth_adapter_status_label).assertIsDisplayed()
            onNodeWithTag(BLUETOOTH_ICON_DISABLE_TEST_TAG).assertIsDisplayed()
            onNodeWithTag(BLUETOOTH_ICON_ENABLE_TEST_TAG).assertDoesNotExist()
            onNodeWithText(stringResourceId = R.string.bluetooth_location_status_label).assertIsDisplayed()
            onNodeWithTag(LOCATION_ICON_DISABLE_TEST_TAG).assertIsDisplayed()
            onNodeWithTag(LOCATION_ICON_ENABLE_TEST_TAG).assertDoesNotExist()
            onNodeWithText(stringResourceId = R.string.bluetooth_detected_devices_label).assertIsDisplayed()
            onNodeWithTag(REFRESH_DEVICES_ICON_TEST_TAG).assertIsDisplayed()
            onNodeWithTag(CIRCULAR_PROGRESS_TEST_TAG).assertDoesNotExist()
            onNodeWithTag(SCAN_ERROR_MESSAGE_TEST_TAG).assertDoesNotExist()

            // Check click interactions
            onNodeWithTag(REFRESH_DEVICES_ICON_TEST_TAG).performClick()
            verify(startBluetoothScanCallback).invoke()
        }
    }

    @ExperimentalTestApi
    @Test
    fun checkViewWithBluetoothEnable() {
        logTestName()

        runComposeUiTest {
            setContent {
                BluetoothViewForTest(
                    viewState = BluetoothViewState(
                        isBluetoothEnable = true,
                        isLocationEnable = false,
                        isScanning = false,
                        detectedDevices = emptyList(),
                        errorMessage = null
                    )
                )
            }

            // Check visibility
            onNodeWithText(stringResourceId = R.string.bluetooth_adapter_status_label).assertIsDisplayed()
            onNodeWithTag(BLUETOOTH_ICON_DISABLE_TEST_TAG).assertDoesNotExist()
            onNodeWithTag(BLUETOOTH_ICON_ENABLE_TEST_TAG).assertIsDisplayed()
            onNodeWithText(stringResourceId = R.string.bluetooth_location_status_label).assertIsDisplayed()
            onNodeWithTag(LOCATION_ICON_DISABLE_TEST_TAG).assertIsDisplayed()
            onNodeWithTag(LOCATION_ICON_ENABLE_TEST_TAG).assertDoesNotExist()
            onNodeWithText(stringResourceId = R.string.bluetooth_detected_devices_label).assertIsDisplayed()
            onNodeWithTag(REFRESH_DEVICES_ICON_TEST_TAG).assertIsDisplayed()
            onNodeWithTag(CIRCULAR_PROGRESS_TEST_TAG).assertDoesNotExist()
            onNodeWithTag(SCAN_ERROR_MESSAGE_TEST_TAG).assertDoesNotExist()

            // Check click interactions
            onNodeWithTag(REFRESH_DEVICES_ICON_TEST_TAG).performClick()
            verify(startBluetoothScanCallback).invoke()
        }
    }

    @ExperimentalTestApi
    @Test
    fun checkViewWithLocationEnable() {
        logTestName()

        runComposeUiTest {
            setContent {
                BluetoothViewForTest(
                    viewState = BluetoothViewState(
                        isBluetoothEnable = false,
                        isLocationEnable = true,
                        isScanning = false,
                        detectedDevices = emptyList(),
                        errorMessage = null
                    )
                )
            }

            // Check visibility
            onNodeWithText(stringResourceId = R.string.bluetooth_adapter_status_label).assertIsDisplayed()
            onNodeWithTag(BLUETOOTH_ICON_DISABLE_TEST_TAG).assertIsDisplayed()
            onNodeWithTag(BLUETOOTH_ICON_ENABLE_TEST_TAG).assertDoesNotExist()
            onNodeWithText(stringResourceId = R.string.bluetooth_location_status_label).assertIsDisplayed()
            onNodeWithTag(LOCATION_ICON_DISABLE_TEST_TAG).assertDoesNotExist()
            onNodeWithTag(LOCATION_ICON_ENABLE_TEST_TAG).assertIsDisplayed()
            onNodeWithText(stringResourceId = R.string.bluetooth_detected_devices_label).assertIsDisplayed()
            onNodeWithTag(REFRESH_DEVICES_ICON_TEST_TAG).assertIsDisplayed()
            onNodeWithTag(CIRCULAR_PROGRESS_TEST_TAG).assertDoesNotExist()
            onNodeWithTag(SCAN_ERROR_MESSAGE_TEST_TAG).assertDoesNotExist()

            // Check click interactions
            onNodeWithTag(REFRESH_DEVICES_ICON_TEST_TAG).performClick()
            verify(startBluetoothScanCallback).invoke()
        }
    }

    @ExperimentalTestApi
    @Test
    fun checkViewWithScanInProgress() {
        logTestName()

        runComposeUiTest {
            setContent {
                BluetoothViewForTest(
                    viewState = BluetoothViewState(
                        isBluetoothEnable = false,
                        isLocationEnable = false,
                        isScanning = true,
                        detectedDevices = emptyList(),
                        errorMessage = null
                    )
                )
            }

            // Check visibility
            onNodeWithText(stringResourceId = R.string.bluetooth_adapter_status_label).assertIsDisplayed()
            onNodeWithTag(BLUETOOTH_ICON_DISABLE_TEST_TAG).assertIsDisplayed()
            onNodeWithTag(BLUETOOTH_ICON_ENABLE_TEST_TAG).assertDoesNotExist()
            onNodeWithText(stringResourceId = R.string.bluetooth_location_status_label).assertIsDisplayed()
            onNodeWithTag(LOCATION_ICON_DISABLE_TEST_TAG).assertIsDisplayed()
            onNodeWithTag(LOCATION_ICON_ENABLE_TEST_TAG).assertDoesNotExist()
            onNodeWithText(stringResourceId = R.string.bluetooth_detected_devices_label).assertIsDisplayed()
            onNodeWithTag(REFRESH_DEVICES_ICON_TEST_TAG).assertDoesNotExist()
            onNodeWithTag(CIRCULAR_PROGRESS_TEST_TAG).assertIsDisplayed()
            onNodeWithTag(SCAN_ERROR_MESSAGE_TEST_TAG).assertDoesNotExist()
        }
    }

    @ExperimentalTestApi
    @Test
    fun checkViewWithDetectedDevices() {
        logTestName()

        val device1Name = "Device 1"
        val device1Address = "Address 1"
        val device2Name = "Device 2"
        val device2Address = "Address 2"

        runComposeUiTest {
            setContent {
                BluetoothViewForTest(
                    viewState = BluetoothViewState(
                        isBluetoothEnable = false,
                        isLocationEnable = false,
                        isScanning = false,
                        detectedDevices = listOf(
                            BluetoothDevice(
                                type = BluetoothDeviceType.COMPUTER,
                                name = device1Name,
                                hardwareAddress = device1Address
                            ),
                            BluetoothDevice(
                                type = BluetoothDeviceType.UNKNOWN,
                                name = device2Name,
                                hardwareAddress = device2Address
                            )
                        ),
                        errorMessage = null
                    )
                )
            }

            // Check visibility
            onNodeWithText(stringResourceId = R.string.bluetooth_adapter_status_label).assertIsDisplayed()
            onNodeWithTag(BLUETOOTH_ICON_DISABLE_TEST_TAG).assertIsDisplayed()
            onNodeWithTag(BLUETOOTH_ICON_ENABLE_TEST_TAG).assertDoesNotExist()
            onNodeWithText(stringResourceId = R.string.bluetooth_location_status_label).assertIsDisplayed()
            onNodeWithTag(LOCATION_ICON_DISABLE_TEST_TAG).assertIsDisplayed()
            onNodeWithTag(LOCATION_ICON_ENABLE_TEST_TAG).assertDoesNotExist()
            onNodeWithText(stringResourceId = R.string.bluetooth_detected_devices_label).assertIsDisplayed()
            onNodeWithTag(REFRESH_DEVICES_ICON_TEST_TAG).assertIsDisplayed()
            onNodeWithTag(CIRCULAR_PROGRESS_TEST_TAG).assertDoesNotExist()
            onNodeWithText(device1Name).assertIsDisplayed()
            onNodeWithText(device1Address).assertIsDisplayed()
            onNodeWithText(device2Name).assertIsDisplayed()
            onNodeWithText(device2Address).assertIsDisplayed()
            onNodeWithTag(SCAN_ERROR_MESSAGE_TEST_TAG).assertDoesNotExist()

            // Check click interactions
            onNodeWithTag(REFRESH_DEVICES_ICON_TEST_TAG).performClick()
            verify(startBluetoothScanCallback).invoke()
        }
    }

    @ExperimentalTestApi
    @Test
    fun checkViewWithScanErrorMessage() {
        logTestName()

        val errorMessage = "Test error message"

        runComposeUiTest {
            setContent {
                BluetoothViewForTest(
                    viewState = BluetoothViewState(
                        isBluetoothEnable = false,
                        isLocationEnable = false,
                        isScanning = false,
                        detectedDevices = emptyList(),
                        errorMessage = errorMessage
                    )
                )
            }

            // Check visibility
            onNodeWithText(stringResourceId = R.string.bluetooth_adapter_status_label).assertIsDisplayed()
            onNodeWithTag(BLUETOOTH_ICON_DISABLE_TEST_TAG).assertIsDisplayed()
            onNodeWithTag(BLUETOOTH_ICON_ENABLE_TEST_TAG).assertDoesNotExist()
            onNodeWithText(stringResourceId = R.string.bluetooth_location_status_label).assertIsDisplayed()
            onNodeWithTag(LOCATION_ICON_DISABLE_TEST_TAG).assertIsDisplayed()
            onNodeWithTag(LOCATION_ICON_ENABLE_TEST_TAG).assertDoesNotExist()
            onNodeWithText(stringResourceId = R.string.bluetooth_detected_devices_label).assertIsDisplayed()
            onNodeWithTag(REFRESH_DEVICES_ICON_TEST_TAG).assertIsDisplayed()
            onNodeWithTag(CIRCULAR_PROGRESS_TEST_TAG).assertDoesNotExist()
            onNodeWithTag(SCAN_ERROR_MESSAGE_TEST_TAG)
                .assertIsDisplayed()
                .assertTextContains(errorMessage)

            // Check click interactions
            onNodeWithTag(REFRESH_DEVICES_ICON_TEST_TAG).performClick()
            verify(startBluetoothScanCallback).invoke()
        }
    }
}
