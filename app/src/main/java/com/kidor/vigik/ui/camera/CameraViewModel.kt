package com.kidor.vigik.ui.camera

import androidx.lifecycle.viewModelScope
import com.kidor.vigik.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Business logic of camera screen.
 */
@HiltViewModel
class CameraViewModel @Inject constructor() : BaseViewModel<CameraViewAction, CameraViewState, Nothing>() {

    init {
        _viewState.value = CameraViewState.CheckPermission
    }

    override fun handleAction(viewAction: CameraViewAction) {
        when (viewAction) {
            CameraViewAction.PermissionGranted -> _viewState.value = CameraViewState.ShowCamera
            is CameraViewAction.PhotoCaptured -> viewModelScope.launch {
                _viewState.value = CameraViewState.ShowCapturedPhoto(viewAction.uri)
            }
        }
    }
}
