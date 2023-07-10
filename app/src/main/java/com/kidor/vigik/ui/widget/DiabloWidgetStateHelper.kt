package com.kidor.vigik.ui.widget

import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import com.kidor.vigik.R
import com.kidor.vigik.data.Localization
import com.kidor.vigik.data.PreferencesKeys
import com.kidor.vigik.data.diablo.model.Diablo4WorldBoss
import com.kidor.vigik.utils.SystemWrapper
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private const val MINUTES_TO_MILLIS = 60 * 1000

/**
 * Helper to access and edit Diablo IV widget's state.
 */
object DiabloWidgetStateHelper {

    /**
     * Returns a [DiabloWidgetState] based on value found in widget's state store.
     *
     * @param preferences The preferences used by the widget.
     */
    fun getState(preferences: Preferences): DiabloWidgetState {
        return DiabloWidgetState(
            isLoading = preferences[PreferencesKeys.WIDGET_DIABLO_IS_LOADING] ?: false,
            data = DiabloWidgetData(
                worldBoss = Diablo4WorldBoss.fromName(preferences[PreferencesKeys.WIDGET_DIABLO_BOSS_NAME] ?: ""),
                spawnDate = preferences[PreferencesKeys.WIDGET_DIABLO_BOSS_SPAWN_DATE] ?: ""
            )
        )
    }

    /**
     * Updates 'isLoading' widget state.
     *
     * @param preferences The preferences used by the widget.
     * @param isLoading   True if the widget is loading data, otherwise false.
     */
    fun setLoading(preferences: MutablePreferences, isLoading: Boolean) {
        preferences[PreferencesKeys.WIDGET_DIABLO_IS_LOADING] = isLoading
    }

    /**
     * Update 'data' widget state.
     *
     * @param preferences  The preferences used by the widget.
     * @param bossName     The boss name
     * @param spawnDelay   Spawn delay in minutes.
     * @param localization The localization module.
     * @param system       System's wrapper to retrieve current time.
     */
    fun setData(
        preferences: MutablePreferences,
        bossName: String?,
        spawnDelay: Int,
        localization: Localization,
        system: SystemWrapper
    ) {
        preferences[PreferencesKeys.WIDGET_DIABLO_BOSS_NAME] = bossName ?: ""
        preferences[PreferencesKeys.WIDGET_DIABLO_BOSS_SPAWN_DATE] = getFormattedDate(
            Date(system.currentTimeMillis() + (spawnDelay * MINUTES_TO_MILLIS)),
            localization
        )
        preferences[PreferencesKeys.WIDGET_DIABLO_IS_LOADING] = false
    }

    /**
     * Formats and returns date as a String in format [R.string.widget_diablo_boss_spawn_date_pattern].
     *
     * @param date         The date to format.
     * @param localization The localization module.
     */
    private fun getFormattedDate(date: Date, localization: Localization): String {
        val dateFormat = SimpleDateFormat(
            localization.getString(R.string.widget_diablo_boss_spawn_date_pattern),
            Locale.getDefault()
        )
        return dateFormat
            .format(date)
            .replaceFirstChar { firstChar ->
                firstChar.uppercaseChar()
            }
    }
}
