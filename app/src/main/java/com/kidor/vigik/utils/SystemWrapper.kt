package com.kidor.vigik.utils

import javax.inject.Inject

/**
 * Wrapper of [System] to allow mocking static calls to [System.currentTimeMillis] during tests.
 */
class SystemWrapper @Inject constructor() {

    /**
     * Returns the current time in milliseconds.
     *
     * @see [System.currentTimeMillis]
     */
    fun currentTimeMillis(): Long = System.currentTimeMillis()
}