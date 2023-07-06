package com.kidor.vigik.ui.widget

import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import com.kidor.vigik.data.PreferencesKeys
import com.kidor.vigik.data.diablo.model.Diablo4WorldBoss
import java.util.Date

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
                spawnDate = Date(preferences[PreferencesKeys.WIDGET_DIABLO_BOSS_SPAWN_TIMESTAMP] ?: 0)
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
     * @param preferences The preferences used by the widget.
     * @param bossName    The boss name
     * @param spawnDelay  Spawn delay in minutes.
     */
    fun setData(preferences: MutablePreferences, bossName: String?, spawnDelay: Int) {
        preferences[PreferencesKeys.WIDGET_DIABLO_BOSS_NAME] = bossName ?: ""
        preferences[PreferencesKeys.WIDGET_DIABLO_BOSS_SPAWN_TIMESTAMP] =
            System.currentTimeMillis() + (spawnDelay * MINUTES_TO_MILLIS)
        preferences[PreferencesKeys.WIDGET_DIABLO_IS_LOADING] = false
    }
}
