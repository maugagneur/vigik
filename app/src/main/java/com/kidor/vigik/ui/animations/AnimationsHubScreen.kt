package com.kidor.vigik.ui.animations

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.kidor.vigik.navigation.AppScreen
import com.kidor.vigik.ui.common.NavigationButton
import com.kidor.vigik.ui.theme.dimensions

/**
 * View that display all animation sections.
 */
@Composable
@Preview(widthDp = 400, heightDp = 700)
fun AnimationsHubScreen(
    navigateTo: (AppScreen) -> Unit = {}
) {
    LazyColumn(
        modifier = Modifier.fillMaxHeight(),
        contentPadding = PaddingValues(all = MaterialTheme.dimensions.commonSpaceXLarge),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.dimensions.commonSpaceLarge),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val screens = listOf(
            AppScreen.AnimationCircleLoaderScreen,
            AppScreen.AnimationFollowingArrowsScreen,
            AppScreen.AnimationGlitterRainbowScreen,
            AppScreen.AnimationLookaheadScreen,
            AppScreen.AnimationShapeScreen,
            AppScreen.AnimationSnowfallScreen,
            AppScreen.AnimationTypewriterScreen
        )
        items(screens) { screen ->
            NavigationButton(destination = screen) { navigateTo(screen) }
        }
    }
}
