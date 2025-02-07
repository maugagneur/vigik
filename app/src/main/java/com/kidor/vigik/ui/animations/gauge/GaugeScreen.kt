package com.kidor.vigik.ui.animations.gauge

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kidor.vigik.ui.base.ObserveViewState

/**
 * View that display the section dedicated to a custom gauge.
 */
@Preview
@Composable
fun GaugeScreen(
    viewModel: GaugeViewModel = hiltViewModel()
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        ObserveViewState(viewModel = viewModel) { viewState ->
            Column(
                modifier = Modifier.width(intrinsicSize = IntrinsicSize.Min)
            ) {
                Gauge(
                    modifier = Modifier.size(200.dp),
                    value = viewState.gaugeValue
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    FilledIconButton(
                        onClick = { viewModel.handleAction(GaugeViewAction.DecreaseValue) }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Remove,
                            contentDescription = null
                        )
                    }
                    FilledIconButton(
                        onClick = { viewModel.handleAction(GaugeViewAction.IncreaseValue) }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null
                        )
                    }
                }
            }
        }
    }
}
