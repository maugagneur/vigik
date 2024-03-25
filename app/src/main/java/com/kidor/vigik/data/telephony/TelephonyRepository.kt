package com.kidor.vigik.data.telephony

/**
 * Interface of a repository related to telephony features.
 */
interface TelephonyRepository {

    /**
     * Returns all contacts saved on the device.
     */
    suspend fun getAllContact(): List<Contact>

    /**
     * Returns all mobile contacts saved on the device.
     */
    suspend fun getAllMobileContact(): List<Contact>

    /**
     * Returns the number of SMS stored on the device.
     */
    suspend fun getSmsTotalNumber(): Int
}
