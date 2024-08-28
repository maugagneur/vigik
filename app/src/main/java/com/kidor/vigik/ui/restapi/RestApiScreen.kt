package com.kidor.vigik.ui.restapi

import android.content.res.Configuration
import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.hilt.navigation.compose.hiltViewModel
import com.kidor.vigik.R
import com.kidor.vigik.data.diablo.DIABLO4_API_BASE_URL
import com.kidor.vigik.data.diablo.model.Diablo4WorldBoss
import com.kidor.vigik.ui.base.ObserveViewState
import com.kidor.vigik.ui.compose.dimensions

internal const val WORLD_BOSS_ICON_TEST_TAG = "world_boss_icon"
internal const val WORLD_BOSS_NAME_TEST_TAG = "world_boss_name"
internal const val WORLD_BOSS_TIME_TEST_TAG = "world_boss_waiting_time"
internal const val HELL_TIDE_ICON_TEST_TAG = "hell_tide_icon"
internal const val HELL_TIDE_TIME_TEST_TAG = "hell_tide_waiting_time"

/**
 * View that display the section dedicated to REST API.
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun RestApiScreen(
    viewModel: RestApiViewModel = hiltViewModel()
) {
    ObserveViewState(viewModel) { state ->
        val pullRefreshState = rememberPullRefreshState(
            refreshing = state.isRefreshing,
            onRefresh = { viewModel.handleAction(RestApiViewAction.RefreshData) }
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pullRefresh(pullRefreshState)
                .verticalScroll(rememberScrollState()),
            contentAlignment = Alignment.Center
        ) {
            Diablo4Tracker(state.diablo4TrackerData)
            PullRefreshIndicator(
                refreshing = state.isRefreshing,
                state = pullRefreshState,
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }
    }

    if (LocalConfiguration.current.orientation == Configuration.ORIENTATION_PORTRAIT) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = MaterialTheme.dimensions.commonSpaceMedium),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(id = R.string.diablo_tracker_source_label, DIABLO4_API_BASE_URL),
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = MaterialTheme.dimensions.textSizeSmall
            )
        }
    }
}

@Composable
@Preview(widthDp = 400, heightDp = 700)
@VisibleForTesting
internal fun Diablo4Tracker(
    @PreviewParameter(Diablo4TrackerDataProvider::class) diablo4TrackerData: Diablo4TrackerData
) {
    Column(
        modifier = Modifier.padding(horizontal = MaterialTheme.dimensions.commonSpaceXLarge),
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
            contentDescription = worldBoss.description,
            modifier = Modifier.testTag(WORLD_BOSS_ICON_TEST_TAG)
        )

        Diablo4WorldBoss.AVARICE -> Image(
            painter = painterResource(id = R.drawable.d4_avarice),
            contentDescription = worldBoss.description,
            modifier = Modifier.testTag(WORLD_BOSS_ICON_TEST_TAG)
        )

        Diablo4WorldBoss.WANDERING_DEATH -> Image(
            painter = painterResource(id = R.drawable.d4_wandering_death),
            contentDescription = worldBoss.description,
            modifier = Modifier.testTag(WORLD_BOSS_ICON_TEST_TAG)
        )

        Diablo4WorldBoss.UNKNOWN, null -> Image(
            painter = painterResource(id = R.drawable.d4_monster),
            contentDescription = "World boss",
            modifier = Modifier.testTag(WORLD_BOSS_ICON_TEST_TAG)
        )
    }

    if (worldBoss == null) return

    Spacer(modifier = Modifier.height(MaterialTheme.dimensions.commonSpaceSmall))

    Text(
        text = stringResource(id = worldBoss.resId),
        modifier = Modifier.testTag(WORLD_BOSS_NAME_TEST_TAG),
        color = MaterialTheme.colorScheme.onBackground,
        fontSize = MaterialTheme.dimensions.textSizeMedium,
        textAlign = TextAlign.Center
    )

    val readableTime = timeToWait ?: "???"
    Text(
        text = stringResource(id = R.string.diablo_boss_next_spawn_label, readableTime),
        modifier = Modifier.testTag(WORLD_BOSS_TIME_TEST_TAG),
        color = MaterialTheme.colorScheme.onBackground,
        fontSize = MaterialTheme.dimensions.textSizeMedium,
        textAlign = TextAlign.Center
    )
}

@Composable
private fun Diablo4HellTideTracker(timeToWait: String?) {
    timeToWait?.let {
        Spacer(modifier = Modifier.height(MaterialTheme.dimensions.commonSpaceXXLarge))
        Image(
            painter = painterResource(id = R.drawable.d4_helltide),
            contentDescription = "Hell tide",
            modifier = Modifier.testTag(HELL_TIDE_ICON_TEST_TAG)
        )
        Spacer(modifier = Modifier.height(MaterialTheme.dimensions.commonSpaceSmall))
        Text(
            text = stringResource(id = R.string.diablo_hell_tide_next_rise_label, it),
            modifier = Modifier.testTag(HELL_TIDE_TIME_TEST_TAG),
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = MaterialTheme.dimensions.textSizeMedium,
            textAlign = TextAlign.Center
        )
    }
}
