package com.kidor.vigik.data.bluetooth.model

/**
 * Model of Bluetooth device.
 *
 * @param type            The type of the Bluetooth device.
 * @param name            The name of the Bluetooth device.
 * @param hardwareAddress The hardware address (MAC) of the Bluetooth device.
 */
data class BluetoothDevice(val type: BluetoothDeviceType, val name: String, val hardwareAddress: String) {
    constructor(majorDeviceClass: Int?, name: String?, hardwareAddress: String?) : this(
        type = BluetoothDeviceType.fromMajorBluetoothClass(majorDeviceClass),
        name = name ?: "???",
        hardwareAddress = hardwareAddress ?: ""
    )
}
