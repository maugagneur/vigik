package com.kidor.vigik.ui.restapi

import com.kidor.vigik.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * Business logic of REST API screen.
 */
@HiltViewModel
class RestApiViewModel @Inject constructor() : BaseViewModel<Nothing, Diablo4TrackerData, Nothing>() {

    init {
        _viewState.value = Diablo4TrackerData()
    }
}
