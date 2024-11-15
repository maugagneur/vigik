package com.kidor.vigik.ui.compass

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.kidor.vigik.ui.base.ObserveViewState
import com.kidor.vigik.ui.theme.dimensions

/**
 * View that display the section dedicated to compass.
 */
@Composable
fun CompassScreen(viewModel: CompassViewModel = hiltViewModel()) {
    var uiSettings by remember { mutableStateOf(MapUiSettings(compassEnabled = false)) }

    Box(modifier = Modifier.fillMaxSize()) {
        ObserveViewState(viewModel) { viewState ->
            GoogleMap(
                modifier = Modifier.matchParentSize(),
                cameraPositionState = viewState.cameraPositionState,
                uiSettings = uiSettings
            )
            Switch(
                checked = uiSettings.zoomControlsEnabled,
                onCheckedChange = {
                    uiSettings = uiSettings.copy(zoomControlsEnabled = it)
                },
                modifier = Modifier.padding(all = MaterialTheme.dimensions.commonSpaceMedium)
            )
        }
    }
}
