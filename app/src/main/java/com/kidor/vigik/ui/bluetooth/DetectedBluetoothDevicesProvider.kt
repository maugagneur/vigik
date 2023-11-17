package com.kidor.vigik.ui.bluetooth

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.kidor.vigik.data.bluetooth.model.BluetoothDevice
import com.kidor.vigik.data.bluetooth.model.BluetoothDeviceType
import com.kidor.vigik.utils.ExcludedFromKoverReport

private const val DEVICE_ADDRESS = "12:34:56:78:90:AB"

/**
 * Provides a set of data for the preview of Bluetooth screen.
 */
@ExcludedFromKoverReport
class DetectedBluetoothDevicesProvider : PreviewParameterProvider<List<BluetoothDevice>> {
    override val values: Sequence<List<BluetoothDevice>> = sequenceOf(
        emptyList(),
        listOf(
            BluetoothDevice(
                type = BluetoothDeviceType.UNKNOWN,
                name = "Bluetooth device",
                hardwareAddress = DEVICE_ADDRESS
            )
        ),
        listOf(
            BluetoothDevice(
                type = BluetoothDeviceType.COMPUTER,
                name = "Computer",
                hardwareAddress = DEVICE_ADDRESS
            ),
            BluetoothDevice(
                type = BluetoothDeviceType.HEADPHONE,
                name = "Headphone",
                hardwareAddress = DEVICE_ADDRESS
            ),
            BluetoothDevice(
                type = BluetoothDeviceType.HEADSET,
                name = "Headset",
                hardwareAddress = DEVICE_ADDRESS
            ),
            BluetoothDevice(
                type = BluetoothDeviceType.IMAGING,
                name = "Printer",
                hardwareAddress = DEVICE_ADDRESS
            ),
            BluetoothDevice(
                type = BluetoothDeviceType.PHONE,
                name = "Phone",
                hardwareAddress = DEVICE_ADDRESS
            ),
            BluetoothDevice(
                type = BluetoothDeviceType.PERIPHERAL,
                name = "Peripheral",
                hardwareAddress = DEVICE_ADDRESS
            ),
            BluetoothDevice(
                type = BluetoothDeviceType.UNKNOWN,
                name = "Unknown device",
                hardwareAddress = DEVICE_ADDRESS
            )
        )
    )
}
