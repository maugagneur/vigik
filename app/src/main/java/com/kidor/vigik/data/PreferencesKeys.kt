package com.kidor.vigik.data

import androidx.datastore.preferences.core.stringPreferencesKey

/**
 * Shared preference keys used.
 */
object PreferencesKeys {
    val BIOMETRIC_TOKEN = stringPreferencesKey("biometric_token_key")
    val BIOMETRIC_IV = stringPreferencesKey("biometric_iv_key")
    val USER_TOKEN = stringPreferencesKey("user_token_key")
}
