package com.kidor.vigik.ui.widget

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import androidx.glance.layout.fillMaxSize
import com.kidor.vigik.ui.MainActivity
/**
 * Diablo IV event tracker widget.
 */
class DiabloWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            Box(
                modifier = GlanceModifier.fillMaxSize()
                    .clickable(onClick = actionStartActivity(activity = MainActivity::class.java)),
            ) {
            }
        }
}
