package com.kidor.vigik.ui.animations.shimmer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kidor.vigik.extensions.shimmerAnimation
import com.kidor.vigik.ui.theme.dimensions

/**
 * View that display the section dedicated to the shimmer animation.
 */
@Preview
@Composable
fun ShimmerScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(all = MaterialTheme.dimensions.commonSpaceLarge),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        ShimmeredContent(backgroundColor = MaterialTheme.colorScheme.primary)
        ShimmeredContent(backgroundColor = MaterialTheme.colorScheme.secondary)
        ShimmeredContent(backgroundColor = MaterialTheme.colorScheme.tertiary)
        ShimmeredContent(backgroundColor = MaterialTheme.colorScheme.error)
    }
}

@Composable
private fun ShimmeredContent(
    backgroundColor: Color
) {
    Surface(
        shape = RoundedCornerShape(MaterialTheme.dimensions.commonSpaceMedium),
        color = backgroundColor
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = MaterialTheme.dimensions.commonSpaceMedium),
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.dimensions.commonSpaceSmall),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .shimmerAnimation()
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.dimensions.commonSpaceSmall)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(fraction = 0.5f)
                        .height(MaterialTheme.dimensions.commonSpaceMedium)
                        .clip(RoundedCornerShape(MaterialTheme.dimensions.commonSpaceSmall))
                        .shimmerAnimation()
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(MaterialTheme.dimensions.commonSpaceMedium)
                        .clip(RoundedCornerShape(MaterialTheme.dimensions.commonSpaceSmall))
                        .shimmerAnimation()
                )
            }
        }
    }
}
