package com.kidor.vigik.ui.widget

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager

private val TAG = DiabloWidgetRefreshAction::class.java.simpleName

/**
 * [ActionCallback] to refresh the state of [DiabloWidget].
 */
class DiabloWidgetRefreshAction : ActionCallback {
    override suspend fun onAction(context: Context, glanceId: GlanceId, parameters: ActionParameters) {
        // Schedule immediate execution of DiabloWidgetWorker
        val oneTimeWorkRequest = OneTimeWorkRequestBuilder<DiabloWidgetWorker>()
            .addTag(TAG)
            .build()
        WorkManager.getInstance(context)
            .enqueueUniqueWork(TAG, ExistingWorkPolicy.REPLACE, oneTimeWorkRequest)
    }
}
