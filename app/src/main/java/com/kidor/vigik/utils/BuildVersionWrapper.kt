package com.kidor.vigik.utils

import android.os.Build

/**
 * Wrapper of [Build.VERSION] to allow mocking static calls to [Build.VERSION.SDK_INT] during tests.
 */
object BuildVersionWrapper {

    /**
     * Returns true if the SDK version of the software currently running on this hardware device is equal to Android O
     * (API 26, "Oreo") or above.
     */
    fun isOOrAbove(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
}
