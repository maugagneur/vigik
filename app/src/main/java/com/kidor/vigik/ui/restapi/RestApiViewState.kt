package com.kidor.vigik.ui.restapi

import com.kidor.vigik.ui.base.ViewState

/**
 * State that display the Diablo IV tracker data.
 *
 * @param diablo4TrackerData The tracker's data.
 * @param isRefreshing       Pull-refresh 'isRefreshing' state.
 */
data class RestApiViewState(
    val diablo4TrackerData: Diablo4TrackerData,
    val isRefreshing: Boolean = false
) : ViewState
