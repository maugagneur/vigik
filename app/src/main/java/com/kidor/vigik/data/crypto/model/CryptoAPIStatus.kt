package com.kidor.vigik.data.crypto.model

/**
 * Status of cryptographic key.
 */
enum class CryptoAPIStatus {
    /**
     * Crypto API is not ready to be used.
     */
    NOT_READY,

    /**
     * Crypto API is ready to be used.
     */
    READY,

    /**
     * The secret key used by crypto API is present but permanently invalidated.
     */
    INVALIDATED,

    /**
     * Fail to initialize crypto API.
     */
    INIT_FAIL
}
