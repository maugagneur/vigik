package com.kidor.vigik.data.telephony

/**
 * Interface of a contact repository.
 */
interface ContactRepository {

    /**
     * Returns all contacts saved on the device.
     */
    suspend fun getAllContact(): List<Contact>

    /**
     * Returns all mobile contacts saved on the device.
     */
    suspend fun getAllMobileContact(): List<Contact>
}
