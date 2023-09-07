package com.kidor.vigik.data

import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

/**
 * Shared preference keys used.
 */
object PreferencesKeys {
    /**
     * Key related to the encrypted biometric token.
     */
    val BIOMETRIC_TOKEN: Preferences.Key<String> = stringPreferencesKey("biometric_token_key")

    /**
     * Key related to the initialization vector used during biometric encryption/decryption.
     */
    val BIOMETRIC_IV: Preferences.Key<String> = stringPreferencesKey("biometric_iv_key")

    /**
     * Last Emoji picked with EmojiPicker.
     */
    val EMOJI_PICKED: Preferences.Key<String> = stringPreferencesKey("emoji_picked_key")

    /**
     * The user token used for biometric login.
     */
    val USER_TOKEN: Preferences.Key<String> = stringPreferencesKey("user_token_key")

    /**
     * Diablo IV widget preference to indicate if the widget is currently refreshing its data.
     */
    val WIDGET_DIABLO_IS_LOADING: Preferences.Key<Boolean> = booleanPreferencesKey("widget_diablo_is_loading_key")

    /**
     * Diablo IV widget preference of the next world boss name.
     */
    val WIDGET_DIABLO_BOSS_NAME: Preferences.Key<String> = stringPreferencesKey("widget_diablo_boss_name_key")

    /**
     * Diablo IV widget preference of the readable world boss next spawn date.
     */
    val WIDGET_DIABLO_BOSS_SPAWN_DATE: Preferences.Key<String> =
        stringPreferencesKey("widget_diablo_boss_spawn_date_key")
}
