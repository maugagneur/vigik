package com.kidor.vigik.receivers

import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import com.kidor.vigik.ui.widget.ApesWidget

/**
 * Broadcast receiver listening to APES widget actions.
 */
class ApesWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = ApesWidget()
}
