package com.kidor.vigik.data.telephony

/**
 * Data model of a SMS.
 *
 * @param content The message's body.
 * @param type    Indicates if the message was sent or received on this device.
 */
data class Sms(val content: String, val type: SmsType)
