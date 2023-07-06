package com.kidor.vigik.ui.widget

/**
 * Represents the UI state of Diablo IV widget.
 *
 * @param isLoading True if the widget is refreshing its data, otherwise false.
 * @param data      The data to display on widget.
 */
data class DiabloWidgetState(val isLoading: Boolean, val data: DiabloWidgetData)
