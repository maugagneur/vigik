package com.kidor.vigik.ui.animations

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.kidor.vigik.R
import com.kidor.vigik.ui.compose.AppTheme

/**
 * View that display all animation sections.
 */
@Composable
@Preview(widthDp = 400, heightDp = 700)
fun AnimationsHubScreen(
    navigateToGlitterRainbow: () -> Unit = {}
) {
    LazyColumn(
        modifier = Modifier.fillMaxHeight(),
        contentPadding = PaddingValues(all = AppTheme.dimensions.commonSpaceXLarge),
        verticalArrangement = Arrangement.spacedBy(AppTheme.dimensions.commonSpaceLarge),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val animationButtonDataList = listOf(
            AnimationButtonData(R.string.animation_glitter_rainbow_title, navigateToGlitterRainbow)
        )
        items(animationButtonDataList) { buttonData ->
            Button(
                onClick = buttonData.onClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(id = buttonData.textId).uppercase(),
                    fontSize = AppTheme.dimensions.textSizeMedium
                )
            }
        }
    }
}
