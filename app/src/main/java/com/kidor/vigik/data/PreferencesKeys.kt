package com.kidor.vigik.data

import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
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
     * APES user Bearer token used for writing availability.
     */
    val APES_USER_TOKEN: Preferences.Key<String> = stringPreferencesKey("apes_user_token_key")

    /**
     * APES widget preference to indicate if the widget is currently refreshing its data.
     */
    val WIDGET_APES_IS_LOADING: Preferences.Key<Boolean> = booleanPreferencesKey("widget_apes_is_loading_key")

    /**
     * APES widget preference of count of members in voice.
     */
    val WIDGET_APES_VOICE_COUNT: Preferences.Key<Int> = intPreferencesKey("widget_apes_voice_count_key")

    /**
     * APES widget preference of formatted names of members in voice.
     */
    val WIDGET_APES_VOICE_MEMBERS: Preferences.Key<String> = stringPreferencesKey("widget_apes_voice_members_key")

    /**
     * APES widget preference of evening availability summary.
     */
    val WIDGET_APES_EVENING_SUMMARY: Preferences.Key<String> = stringPreferencesKey("widget_apes_evening_summary_key")
}
