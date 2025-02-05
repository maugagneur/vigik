package com.kidor.vigik.ui.animations.gauge

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/**
 * View that display the section dedicated to a custom gauge.
 */
@Preview
@Composable
fun GaugeScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Gauge(
            modifier = Modifier.size(200.dp),
            value = 84f
        )
    }
}
