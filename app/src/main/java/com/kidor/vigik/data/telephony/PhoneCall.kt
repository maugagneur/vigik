package com.kidor.vigik.data.telephony

/**
 * Data model of a phone call.
 *
 * @param phoneNumber The phone number of the phone call.
 * @param status      The status of the phone call.
 */
data class PhoneCall(val phoneNumber: String, val status: PhoneCallStatus)
