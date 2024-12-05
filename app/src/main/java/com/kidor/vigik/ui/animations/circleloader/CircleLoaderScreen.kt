package com.kidor.vigik.ui.animations.circleloader

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp

private val LOADER_SIZE = 80.dp

/**
 * View that display the section dedicated to the circle loader animation.
 */
@Composable
fun CircleLoaderScreen() {
    val orientation = LocalConfiguration.current.orientation
    if (orientation == Configuration.ORIENTATION_PORTRAIT) {
        PortraitView()
    } else {
        LandscapeView()
    }
}

@Composable
private fun LandscapeView() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Loaders(animatedTails = false)
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Loaders(animatedTails = true)
        }
    }
}

@Composable
private fun PortraitView() {
    Row(
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Loaders(animatedTails = false)
        }
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Loaders(animatedTails = true)
        }
    }
}

@Composable
private fun Loaders(animatedTails: Boolean) {
    // Single long tail
    CircleLoader(
        modifier = Modifier.size(LOADER_SIZE),
        color = MaterialTheme.colorScheme.secondary,
        tailLength = 280f,
        tailAnimation = animatedTails
    )

    // Two tails with same color
    CircleLoader(
        modifier = Modifier.size(LOADER_SIZE),
        color = MaterialTheme.colorScheme.primary,
        secondColor = MaterialTheme.colorScheme.primary,
        tailAnimation = animatedTails
    )

    // Two tails with secondary color
    CircleLoader(
        modifier = Modifier.size(LOADER_SIZE),
        color = MaterialTheme.colorScheme.secondary,
        secondColor = MaterialTheme.colorScheme.primary,
        tailAnimation = animatedTails
    )
}
