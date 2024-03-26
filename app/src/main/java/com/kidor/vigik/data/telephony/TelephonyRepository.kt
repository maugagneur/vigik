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

    /**
     * Emits a [message] to a given [phoneNumber] as SMS.
     *
     * @param phoneNumber The phone number.
     * @param message     The message to send.
     */
    suspend fun sendSms(phoneNumber: String, message: String)

    /**
     * Returns all phone calls saved on the device.
     */
    suspend fun getAllPhoneCalls(): List<PhoneCall>
}
