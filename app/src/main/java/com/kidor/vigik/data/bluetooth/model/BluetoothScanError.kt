package com.kidor.vigik.data.bluetooth.model

import android.bluetooth.le.ScanCallback

/**
 * Potential scan errors.
 */
enum class BluetoothScanError {
    /** Failed to start. **/
    SCAN_FAILED_TO_START,
    /** Already started. **/
    SCAN_FAILED_ALREADY_STARTED,
    /** Application registration failure. **/
    SCAN_FAILED_APPLICATION_REGISTRATION_FAILED,
    /** Internal error. **/
    SCAN_FAILED_INTERNAL_ERROR,
    /** Unsupported feature. **/
    SCAN_FAILED_FEATURE_UNSUPPORTED,
    /** Out of hardware resources. **/
    SCAN_FAILED_OUT_OF_HARDWARE_RESOURCES,
    /** Scanning too frequently error. **/
    SCAN_FAILED_SCANNING_TOO_FREQUENTLY,
    /** Unknown error. **/
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
