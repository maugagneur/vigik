package com.kidor.vigik.data

import androidx.datastore.preferences.core.stringPreferencesKey

/**
 * Shared preference keys used.
 */
object PreferencesKeys {
    /**
     * Key related to the encrypted biometric token.
     */
    val BIOMETRIC_TOKEN = stringPreferencesKey("biometric_token_key")

    /**
     * Key related to the initialization vector used during biometric encryption/decryption.
     */
    val BIOMETRIC_IV = stringPreferencesKey("biometric_iv_key")

    /**
     * The user token used for biometric login.
     */
    val USER_TOKEN = stringPreferencesKey("user_token_key")
}
