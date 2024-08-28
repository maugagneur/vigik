package com.kidor.vigik.ui.animations

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.kidor.vigik.R
import com.kidor.vigik.ui.compose.dimensions

/**
 * View that display all animation sections.
 */
@Composable
@Preview(widthDp = 400, heightDp = 700)
fun AnimationsHubScreen(
    navigateToFollowingArrows: () -> Unit = {},
    navigateToGlitterRainbow: () -> Unit = {},
    navigateToShape: () -> Unit = {},
    navigateToSnowfall: () -> Unit = {},
    navigateToTypewriter: () -> Unit = {}
) {
    LazyColumn(
        modifier = Modifier.fillMaxHeight(),
        contentPadding = PaddingValues(all = MaterialTheme.dimensions.commonSpaceXLarge),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.dimensions.commonSpaceLarge),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val animationButtonDataList = listOf(
            AnimationButtonData(R.string.animation_following_arrows_title, navigateToFollowingArrows),
            AnimationButtonData(R.string.animation_glitter_rainbow_title, navigateToGlitterRainbow),
            AnimationButtonData(R.string.animation_shape_title, navigateToShape),
            AnimationButtonData(R.string.animation_snowfall_title, navigateToSnowfall),
            AnimationButtonData(R.string.animation_typewriter_title, navigateToTypewriter)
        )
        items(animationButtonDataList) { buttonData ->
            Button(
                onClick = buttonData.onClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(id = buttonData.textId).uppercase(),
                    fontSize = MaterialTheme.dimensions.textSizeMedium
                )
            }
        }
    }
}
