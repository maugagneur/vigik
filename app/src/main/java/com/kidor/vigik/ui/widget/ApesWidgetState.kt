package com.kidor.vigik.ui.widget

/**
 * State of APES widget.
 *
 * @param isLoading True if widget is refreshing data, otherwise false.
 * @param data      Data displayed by the widget.
 */
data class ApesWidgetState(
    val isLoading: Boolean = false,
    val data: ApesWidgetData = ApesWidgetData()
)
