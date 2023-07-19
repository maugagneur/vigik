package com.kidor.vigik.data.bluetooth.model

import android.bluetooth.le.ScanCallback

/**
 * Potential scan errors.
 */
enum class BluetoothScanError {
    SCAN_FAILED_TO_START,
    SCAN_FAILED_ALREADY_STARTED,
    SCAN_FAILED_APPLICATION_REGISTRATION_FAILED,
    SCAN_FAILED_INTERNAL_ERROR,
    SCAN_FAILED_FEATURE_UNSUPPORTED,
    SCAN_FAILED_OUT_OF_HARDWARE_RESOURCES,
    SCAN_FAILED_SCANNING_TOO_FREQUENTLY,
    SCAN_FAILED_UNKNOWN_REASON;

    companion object {
        /**
         * Returns the [BluetoothScanError] corresponding to the given error code of [SCAN_FAILED_UNKNOWN_REASON] if
         * not found.
         */
        fun fromErrorCode(errorCode: Int): BluetoothScanError {
            return when (errorCode) {
                ScanCallback.SCAN_FAILED_ALREADY_STARTED -> SCAN_FAILED_ALREADY_STARTED
                ScanCallback.SCAN_FAILED_APPLICATION_REGISTRATION_FAILED -> SCAN_FAILED_APPLICATION_REGISTRATION_FAILED
                ScanCallback.SCAN_FAILED_INTERNAL_ERROR -> SCAN_FAILED_INTERNAL_ERROR
                ScanCallback.SCAN_FAILED_FEATURE_UNSUPPORTED -> SCAN_FAILED_FEATURE_UNSUPPORTED
                ScanCallback.SCAN_FAILED_OUT_OF_HARDWARE_RESOURCES -> SCAN_FAILED_OUT_OF_HARDWARE_RESOURCES
                ScanCallback.SCAN_FAILED_SCANNING_TOO_FREQUENTLY -> SCAN_FAILED_SCANNING_TOO_FREQUENTLY
                else -> SCAN_FAILED_UNKNOWN_REASON
            }
        }
    }
}
