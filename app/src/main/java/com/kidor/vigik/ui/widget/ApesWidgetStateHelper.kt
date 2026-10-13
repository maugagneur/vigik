package com.kidor.vigik.ui.widget

import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import com.kidor.vigik.data.PreferencesKeys

/**
 * Helper to access and edit APES widget's state in DataStore preferences.
 */
object ApesWidgetStateHelper {

    /**
     * Returns an [ApesWidgetState] based on values found in widget's preferences.
     */
    fun getState(preferences: Preferences): ApesWidgetState {
        return ApesWidgetState(
            isLoading = preferences[PreferencesKeys.WIDGET_APES_IS_LOADING] ?: false,
            data = ApesWidgetData(
                voiceCount = preferences[PreferencesKeys.WIDGET_APES_VOICE_COUNT] ?: 0,
                voiceMembers = preferences[PreferencesKeys.WIDGET_APES_VOICE_MEMBERS] ?: "",
                eveningSummary = preferences[PreferencesKeys.WIDGET_APES_EVENING_SUMMARY] ?: ""
            )
        )
    }

    /**
     * Updates 'isLoading' widget state.
     */
    fun setLoading(preferences: MutablePreferences, isLoading: Boolean) {
        preferences[PreferencesKeys.WIDGET_APES_IS_LOADING] = isLoading
    }

    /**
     * Updates data widget state.
     */
    fun setData(
        preferences: MutablePreferences,
        voiceCount: Int,
        voiceMembers: String,
        eveningSummary: String
    ) {
        preferences[PreferencesKeys.WIDGET_APES_VOICE_COUNT] = voiceCount
        preferences[PreferencesKeys.WIDGET_APES_VOICE_MEMBERS] = voiceMembers
        preferences[PreferencesKeys.WIDGET_APES_EVENING_SUMMARY] = eveningSummary
        preferences[PreferencesKeys.WIDGET_APES_IS_LOADING] = false
    }
}
