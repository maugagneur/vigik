package com.kidor.vigik.ui.restapi

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.hilt.navigation.compose.hiltViewModel
import com.kidor.vigik.R
import com.kidor.vigik.data.diablo.model.Diablo4WorldBoss
import com.kidor.vigik.ui.base.ObserveViewState
import com.kidor.vigik.ui.compose.AppTheme

/**
 * View that display the section dedicated to REST API.
 */
@Composable
fun RestApiScreen(
    viewModel: RestApiViewModel = hiltViewModel()
) {
    ObserveViewState(viewModel) { state ->
        Diablo4Tracker(state)
    }
}

@Composable
@Preview(widthDp = 400, heightDp = 700)
private fun Diablo4Tracker(@PreviewParameter(Diablo4TrackerDataProvider::class) diablo4TrackerData: Diablo4TrackerData) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Diablo4WorldBossTracker(
            worldBoss = diablo4TrackerData.nextBoss,
            timeToWait = diablo4TrackerData.timeUntilNextBoss
        )
        Diablo4HellTideTracker(
            timeToWait = diablo4TrackerData.timeUntilNextHellTide
        )
    }
}

@Composable
private fun Diablo4WorldBossTracker(worldBoss: Diablo4WorldBoss?, timeToWait: String?) {
    when (worldBoss) {
        Diablo4WorldBoss.ASHAVA -> Image(
            painter = painterResource(id = R.drawable.d4_ashava),
            contentDescription = worldBoss.description
        )
        Diablo4WorldBoss.AVARICE -> Image(
            painter = painterResource(id = R.drawable.d4_avarice),
            contentDescription = worldBoss.description
        )
        Diablo4WorldBoss.WANDERING_DEATH -> Image(
            painter = painterResource(id = R.drawable.d4_wandering_death),
            contentDescription = worldBoss.description
        )
        null -> Image(
            painter = painterResource(id = R.drawable.d4_monster),
            contentDescription = "World boss"
        )
    }

    if (worldBoss == null) return

    Spacer(modifier = Modifier.height(AppTheme.dimensions.commonSpaceSmall))

    Text(
        text = stringResource(id = worldBoss.resId),
        color = MaterialTheme.colorScheme.onBackground,
        fontSize = AppTheme.dimensions.textSizeMedium
    )

    val readableTime = timeToWait ?: "???"
    Text(
        text = stringResource(id = R.string.diablo_boss_next_spawn_label, readableTime),
        color = MaterialTheme.colorScheme.onBackground,
        fontSize = AppTheme.dimensions.textSizeMedium
    )
}

@Composable
private fun Diablo4HellTideTracker(timeToWait: String?) {
    timeToWait?.let {
        Spacer(modifier = Modifier.height(AppTheme.dimensions.commonSpaceXXLarge))
        Image(
            painter = painterResource(id = R.drawable.d4_helltide),
            contentDescription = "Hell tide"
        )
        Spacer(modifier = Modifier.height(AppTheme.dimensions.commonSpaceSmall))
        Text(
            text = stringResource(id = R.string.diablo_hell_tide_next_rise_label, it),
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = AppTheme.dimensions.textSizeMedium
        )
    }
}
