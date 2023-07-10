package com.kidor.vigik.ui.widget

import android.content.Context
import androidx.datastore.preferences.core.MutablePreferences
import androidx.glance.GlanceId
import androidx.glance.appwidget.state.updateAppWidgetState
import javax.inject.Inject

/**
 * Updates the state of Diablo widget then run its composition.
 */
class UpdateDiabloWidgetStateUseCase @Inject constructor() {

    /**
     * Executes the use case.
     *
     * @param context  The application context.
     * @param glanceId The ID of Diablo widget.
     * @param update   The operation to perform on preferences state.
     */
    suspend fun execute(context: Context, glanceId: GlanceId, update: (MutablePreferences) -> Unit) {
        DiabloWidget().apply {
            updateAppWidgetState(context, glanceId) { preferences ->
                update(preferences)
            }
            update(context, glanceId)
        }
    }
}
