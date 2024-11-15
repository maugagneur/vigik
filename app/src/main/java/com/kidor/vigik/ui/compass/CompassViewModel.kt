package com.kidor.vigik.ui.compass

import androidx.annotation.RestrictTo
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.DeviceOrientation
import com.google.android.gms.location.DeviceOrientationListener
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.kidor.vigik.data.orientation.OrientationRepository
import com.kidor.vigik.di.IoDispatcher
import com.kidor.vigik.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val MAP_ZOOM = 10f

/**
 * Business logic of compass screen.
 */
@HiltViewModel
class CompassViewModel @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val orientationRepository: OrientationRepository
) : BaseViewModel<Nothing, CompassViewState, Nothing>(), DeviceOrientationListener {

    init {
        orientationRepository.addListener(this)
    }

    @RestrictTo(RestrictTo.Scope.TESTS)
    public override fun onCleared() {
        orientationRepository.removeListener(this)
        super.onCleared()
    }

    override fun onDeviceOrientationChanged(orientation: DeviceOrientation) {
        viewModelScope.launch(ioDispatcher) {
            _viewState.postValue(
                CompassViewState(
                    cameraPositionState = CameraPositionState(
                        CameraPosition.Builder()
                            .target(LatLng(1.35, 103.87)) // TODO: get position from device's location
                            .bearing(orientation.headingDegrees)
                            .zoom(MAP_ZOOM)
                            .build()
                    )
                )
            )
        }
    }
}
