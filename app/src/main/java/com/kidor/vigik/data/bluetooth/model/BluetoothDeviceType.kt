package com.kidor.vigik.data.bluetooth.model

import android.bluetooth.BluetoothClass

/**
 * Types of Bluetooth devices.
 */
enum class BluetoothDeviceType {
    COMPUTER,
    HEADPHONE,
    HEADSET,
    IMAGING,
    PHONE,
    PERIPHERAL,
    UNKNOWN;

    companion object {
        /**
         * Returns the [BluetoothDeviceType] corresponding to the given major Bluetooth class of [UNKNOWN] if not found.
         */
        fun fromMajorBluetoothClass(majorClass: Int?): BluetoothDeviceType {
            return when (majorClass) {
                BluetoothClass.Device.Major.COMPUTER -> COMPUTER
                BluetoothClass.Device.Major.PHONE -> PHONE
                BluetoothClass.Device.Major.IMAGING -> IMAGING
                BluetoothClass.Device.Major.PERIPHERAL -> PERIPHERAL
                BluetoothClass.PROFILE_HEADSET -> HEADSET
                BluetoothClass.PROFILE_A2DP -> HEADPHONE
                else -> UNKNOWN
            }
        }
    }
}
