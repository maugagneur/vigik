package com.kidor.vigik.receivers

import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import com.kidor.vigik.ui.widget.DiabloWidget

/**
 * Broadcast receiver listening to widget actions.
 */
class DiabloWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = DiabloWidget()
}
