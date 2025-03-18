package com.kidor.vigik.ui.animations.carousel

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.util.lerp
import coil3.SingletonImageLoader
import coil3.compose.AsyncImage
import com.kidor.vigik.R
import com.kidor.vigik.ui.theme.dimensions
import kotlin.math.absoluteValue

/**
 * View that display the section dedicated to the custom carousel animation.
 */
@Preview
@Composable
fun CarouselScreen() {
    val images = listOf(
        R.drawable.notification_big_picture,
        R.drawable.notification_big_picture,
        R.drawable.notification_big_picture,
        R.drawable.notification_big_picture
    )
    val pagerState = rememberPagerState { images.size }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = MaterialTheme.dimensions.commonSpaceXLarge),
            pageSpacing = MaterialTheme.dimensions.commonSpaceMedium
        ) { page ->
            AsyncImage(
                model = images[page],
                contentDescription = "Image n°$page",
                imageLoader = SingletonImageLoader.get(LocalContext.current),
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer {
                        val offset = (pagerState.currentPage - page + pagerState.currentPageOffsetFraction)
                            .absoluteValue
                        lerp(
                            start = 0.75f,
                            stop = 1f,
                            fraction = 1f - offset.coerceIn(0f, 1f)
                        ).let { scale ->
                            scaleY = scale
                        }
                    }
            )
        }
    }
}
