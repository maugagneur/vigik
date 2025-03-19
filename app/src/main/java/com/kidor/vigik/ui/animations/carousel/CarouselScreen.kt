package com.kidor.vigik.ui.animations.carousel

import android.os.Build
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.SingletonImageLoader
import coil3.compose.AsyncImage
import com.kidor.vigik.R
import com.kidor.vigik.extensions.offsetForPage
import com.kidor.vigik.ui.base.ObserveViewState
import com.kidor.vigik.ui.theme.dimensions
import kotlin.math.absoluteValue
import kotlin.math.min

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
private fun Carousel(
    modifier: Modifier = Modifier,
    mode: CarouselMode,
    pagerState: PagerState
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        // Blur effect is only supported on Android 12 and above
        if (mode == CarouselMode.CUBE && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val offsetFromStart = pagerState.offsetForPage(0).absoluteValue
            Box(
                modifier = Modifier
                    .aspectRatio(1f)
                    .offset { IntOffset(x = 0, y = 100.dp.roundToPx()) }
                    .scale(scaleX = 0.6f, scaleY = 0.24f)
                    .rotate(degrees = offsetFromStart * 90f)
                    .blur(
                        radius = 110.dp,
                        edgeTreatment = BlurredEdgeTreatment.Unbounded
                    )
                    .background(color = Color.Black.copy(alpha = 0.5f))
            )
        }
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxWidth(),
            contentPadding = if (mode == CarouselMode.ZOOM) PaddingValues(horizontal = MaterialTheme.dimensions.commonSpaceXLarge) else PaddingValues(all = 0.dp),
            pageSpacing = if (mode == CarouselMode.ZOOM) MaterialTheme.dimensions.commonSpaceMedium else 0.dp
        ) { page ->
            AsyncImage(
                model = IMAGES[page],
                contentDescription = "Image n°$page",
                imageLoader = SingletonImageLoader.get(LocalContext.current),
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer {
                        if (mode == CarouselMode.ZOOM) {
                            val offset = pagerState.offsetForPage(page).absoluteValue
                            lerp(
                                start = 0.75f,
                                stop = 1f,
                                fraction = 1f - offset.coerceIn(0f, 1f)
                            ).let { scale ->
                                scaleY = scale
                            }
                        } else if (mode == CarouselMode.CUBE) {
                            val offset = pagerState.offsetForPage(page)
                            val isOnTheRight = offset < 0f
                            val degrees = 105f
                            val interpolated = FastOutLinearInEasing.transform(offset.absoluteValue)
                            rotationY = min(interpolated * if (isOnTheRight) degrees else -degrees, 90f)

                            transformOrigin = TransformOrigin(
                                pivotFractionX = if (isOnTheRight) 0f else 1f,
                                pivotFractionY = 0.5f
                            )
                        }
                    }
                    .drawWithContent {
                        if (mode == CarouselMode.CUBE) {
                            // Dim the cube face as it turns away from the center
                            val offset = pagerState.offsetForPage(page)
                            drawContent()
                            drawRect(color = Color.Black.copy(alpha = offset.absoluteValue * 0.7f))
                        } else {
                            drawContent()
                        }
                    }
            )
        }
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

@Preview(widthDp = 400, heightDp = 700)
@Composable
private fun CubeCarouselPreview() {
    Carousel(
        mode = CarouselMode.CUBE,
        pagerState = rememberPagerState { IMAGES.size }
    )
}
