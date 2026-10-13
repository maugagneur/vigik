package com.kidor.vigik.ui.widget

import android.content.Context
import androidx.datastore.preferences.core.MutablePreferences
import androidx.glance.GlanceId
import androidx.glance.appwidget.state.updateAppWidgetState
import javax.inject.Inject

/**
 * Updates the state of APES widget then runs its composition.
 */
class UpdateApesWidgetStateUseCase @Inject constructor() {

    /**
     * Executes the use case.
     *
     * @param context  The application context.
     * @param glanceId The ID of APES widget.
     * @param update   The operation to perform on preferences state.
     */
    suspend fun execute(context: Context, glanceId: GlanceId, update: (MutablePreferences) -> Unit) {
        ApesWidget().apply {
            updateAppWidgetState(context, glanceId) { preferences ->
                update(preferences)
            }
            update(context, glanceId)
        }
    }
}
