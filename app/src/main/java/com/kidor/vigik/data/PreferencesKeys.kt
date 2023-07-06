package com.kidor.vigik.data

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
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

    /**
     * Diablo IV widget preference to indicate if the widget is currently refreshing its data.
     */
    val WIDGET_DIABLO_IS_LOADING = booleanPreferencesKey("widget_diablo_is_loading_key")

    /**
     * Diablo IV widget preference of the next world boss name.
     */
    val WIDGET_DIABLO_BOSS_NAME = stringPreferencesKey("widget_diablo_boss_name_key")

    /**
     * Diablo IV widget preference of the timestamp of the next world boss spawn date.
     */
    val WIDGET_DIABLO_BOSS_SPAWN_TIMESTAMP = longPreferencesKey("widget_diablo_boss_spawn_timestamp_key")
}
