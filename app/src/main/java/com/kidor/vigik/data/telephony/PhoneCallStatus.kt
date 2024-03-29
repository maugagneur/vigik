package com.kidor.vigik.data.telephony

/**
 * Status of a phone call.
 */
enum class PhoneCallStatus {
    /**
     * The phone call was started from the device.
     */
    EMITTED,

    /**
     * The phone call was answer by the user.
     */
    RECEIVED,

    /**
     * The user missed the phone call.
     */
    MISSED,

    /**
     * The phone call was rejected by the user.
     */
    REJECTED
}
