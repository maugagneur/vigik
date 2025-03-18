package com.kidor.vigik.ui.animations.carousel

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.util.lerp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.SingletonImageLoader
import coil3.compose.AsyncImage
import com.kidor.vigik.R
import com.kidor.vigik.extensions.offsetForPage
import com.kidor.vigik.ui.base.ObserveViewState
import com.kidor.vigik.ui.theme.dimensions
import kotlin.math.absoluteValue

private val IMAGES = listOf(
    R.drawable.notification_big_picture,
    R.drawable.notification_big_picture,
    R.drawable.notification_big_picture,
    R.drawable.notification_big_picture
)

/**
 * View that display the section dedicated to the custom carousel animation.
 */
@Composable
fun CarouselScreen(
    viewModel: CarouselViewModel = hiltViewModel()
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center
    ) {
        ObserveViewState(viewModel) { viewState ->
            Carousel(
                mode = viewState.carouselMode,
                pagerState = rememberPagerState { IMAGES.size }
            )
        }

        Spacer(modifier = Modifier.height(MaterialTheme.dimensions.commonSpaceLarge))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            FilledIconButton(
                onClick = { viewModel.handleAction(CarouselViewAction.SelectPreviousMode) }
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back"
                )
            }
            Spacer(modifier = Modifier.width(MaterialTheme.dimensions.commonSpaceLarge))
            FilledIconButton(
                onClick = { viewModel.handleAction(CarouselViewAction.SelectNextMode) }
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Forward"
                )
            }
        }
    }
}
@Composable
private fun Carousel(mode: CarouselMode, pagerState: PagerState) {
    HorizontalPager(
        state = pagerState,
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = MaterialTheme.dimensions.commonSpaceXLarge),
        pageSpacing = MaterialTheme.dimensions.commonSpaceMedium
    ) { page ->
        AsyncImage(
            model = IMAGES[page],
            contentDescription = "Image n°$page",
            imageLoader = SingletonImageLoader.get(LocalContext.current),
            modifier = Modifier
                .fillMaxWidth()
                .graphicsLayer {
                    val offset = pagerState.offsetForPage(page).absoluteValue
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

@Preview(widthDp = 400, heightDp = 700)
@Composable
private fun ZoomCarouselPreview() {
    Carousel(
        mode = CarouselMode.ZOOM,
        pagerState = rememberPagerState { IMAGES.size }
    )
}
