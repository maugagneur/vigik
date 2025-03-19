package com.kidor.vigik.ui.animations.carousel

import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Build
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.SingletonImageLoader
import coil3.compose.AsyncImage
import com.kidor.vigik.R
import com.kidor.vigik.extensions.endOffsetForPage
import com.kidor.vigik.extensions.offsetForPage
import com.kidor.vigik.extensions.startOffsetForPage
import com.kidor.vigik.ui.base.ObserveViewState
import com.kidor.vigik.ui.theme.dimensions
import kotlin.math.absoluteValue
import kotlin.math.min

private val IMAGES = listOf(
    R.drawable.aker,
    R.drawable.balloons,
    R.drawable.notification_big_picture,
    R.drawable.road
)
private const val PARALLAX_COEFFICIENT = 0.4f
private const val RIGHT_ANGLE = 90f
private const val CUBE_VISUAL_ANGLE = 105f // Having an angle upper than 90° make a better perspective render

/**
 * View that display the section dedicated to the custom carousel animation.
 */
@Composable
fun CarouselScreen(
    viewModel: CarouselViewModel = hiltViewModel()
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        ObserveViewState(viewModel) { viewState ->
            Carousel(
                modifier = Modifier.weight(1f),
                mode = viewState.carouselMode,
                pagerState = rememberPagerState { IMAGES.size }
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(all = MaterialTheme.dimensions.commonSpaceLarge),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                FilledIconButton(
                    onClick = { viewModel.handleAction(CarouselViewAction.SelectPreviousMode) }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
                Text(
                    text = stringResource(id = viewState.carouselMode.displayNameId),
                    fontSize = MaterialTheme.dimensions.textSizeLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
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
}

@OptIn(ExperimentalComposeUiApi::class)
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
                    .rotate(degrees = offsetFromStart * RIGHT_ANGLE)
                    .blur(
                        radius = 110.dp,
                        edgeTreatment = BlurredEdgeTreatment.Unbounded
                    )
                    .background(color = Color.Black.copy(alpha = 0.5f))
            )
        }

        var offsetY by remember { mutableFloatStateOf(0f) }
        val contentPadding = if (mode == CarouselMode.ZOOM) {
            PaddingValues(horizontal = MaterialTheme.dimensions.commonSpaceXLarge)
        } else {
            PaddingValues(all = 0.dp)
        }
        val beyondViewportPageCount = if (mode == CarouselMode.DECAL) 2 else PagerDefaults.BeyondViewportPageCount
        val pageSpacing = if (mode == CarouselMode.ZOOM) MaterialTheme.dimensions.commonSpaceMedium else 0.dp

        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .pointerInteropFilter { event ->
                    offsetY = event.y
                    false
                }
                .clip(RectangleShape),
            contentPadding = contentPadding,
            beyondViewportPageCount = beyondViewportPageCount,
            pageSpacing = pageSpacing
        ) { page ->
            AsyncImage(
                model = IMAGES[page],
                contentDescription = "Image n°$page",
                imageLoader = SingletonImageLoader.get(LocalContext.current),
                modifier = Modifier
                    .fillMaxWidth()
                    .apply {
                        if (mode == CarouselMode.DECAL) {
                            zIndex(page * 10f)
                            padding(
                                start = MaterialTheme.dimensions.commonSpaceXLarge * 2,
                                end = MaterialTheme.dimensions.commonSpaceXLarge
                            )
                        }
                    }
                    .graphicsLayer {
                        when (mode) {
                            CarouselMode.ZOOM -> {
                                val offset = pagerState.offsetForPage(page).absoluteValue
                                lerp(
                                    start = 0.75f,
                                    stop = 1f,
                                    fraction = 1f - offset.coerceIn(0f, 1f)
                                ).let { scale ->
                                    scaleY = scale
                                }
                            }

                            CarouselMode.CUBE -> {
                                val offset = pagerState.offsetForPage(page)
                                val isOnTheRight = offset < 0f
                                val degrees = CUBE_VISUAL_ANGLE
                                val interpolated = FastOutLinearInEasing.transform(offset.absoluteValue)
                                rotationY = min(interpolated * if (isOnTheRight) degrees else -degrees, RIGHT_ANGLE)

                                transformOrigin = TransformOrigin(
                                    pivotFractionX = if (isOnTheRight) 0f else 1f,
                                    pivotFractionY = 0.5f
                                )
                            }

                            CarouselMode.CIRCLE_REVEAL -> {
                                // Make the left page not move
                                val offset = pagerState.offsetForPage(page)
                                translationX = size.width * offset

                                // Add a circular clipping shape on right page
                                val endOffset = pagerState.endOffsetForPage(page)
                                shape = CirclePath(
                                    progress = 1f - endOffset.absoluteValue,
                                    origin = Offset(
                                        x = size.width,
                                        y = offsetY
                                    )
                                )
                                clip = true

                                // Add a parallax effect
                                val scale = 1f + (offset.absoluteValue * PARALLAX_COEFFICIENT)
                                scaleX = scale
                                scaleY = scale

                                // Fade the left page away
                                val startOffset = pagerState.startOffsetForPage(page)
                                alpha = (2f - startOffset) / 2f
                            }

                            CarouselMode.DECAL -> {
                                // Make the left page not move
                                val startOffset = pagerState.startOffsetForPage(page)
                                translationX = size.width * startOffset

                                // Fade the left page away
                                alpha = (2f - startOffset) / 2f

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                                    val blur = (startOffset * 20f).coerceAtLeast(0.1f)
                                    renderEffect = RenderEffect
                                        .createBlurEffect(blur, blur, Shader.TileMode.DECAL)
                                        .asComposeRenderEffect()
                                }

                                // Add a parallax effect on the left page
                                val scale = 1f - (startOffset * 0.2f)
                                scaleX = scale
                                scaleY = scale
                            }

                            else -> { /* Do nothing */
                            }
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

@Preview(widthDp = 400, heightDp = 700)
@Composable
private fun CircleRevealCarouselPreview() {
    Carousel(
        mode = CarouselMode.CIRCLE_REVEAL,
        pagerState = rememberPagerState { IMAGES.size }
    )
}

@Preview(widthDp = 400, heightDp = 700)
@Composable
private fun DecalCarouselPreview() {
    Carousel(
        mode = CarouselMode.DECAL,
        pagerState = rememberPagerState { IMAGES.size }
    )
}
