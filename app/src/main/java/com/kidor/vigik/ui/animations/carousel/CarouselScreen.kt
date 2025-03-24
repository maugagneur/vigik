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
import androidx.compose.ui.graphics.GraphicsLayerScope
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
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
private const val CIRCLE_PARALLAX_COEFFICIENT = 0.4f
private const val RIGHT_ANGLE = 90f
private const val CUBE_VISUAL_ANGLE = 105f // Having an angle upper than 90° make a better perspective render
private const val BLUR_COEFFICIENT = 20f
private const val MINIMUM_BLUR_RADIUS = 0.1f
private const val DECAL_PARALLAX_COEFFICIENT = 0.2f

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

/**
 * A customizable carousel.
 *
 * @param modifier   The modifier to be applied to the carousel.
 * @param mode       The mode of the carousel.
 * @param pagerState The state to control this carousel.
 */
@OptIn(ExperimentalComposeUiApi::class)
@Suppress("LongMethod")
@Composable
private fun Carousel(
    modifier: Modifier = Modifier,
    mode: CarouselMode,
    pagerState: PagerState
) {
    val contentPadding = if (mode == CarouselMode.ZOOM) {
        PaddingValues(horizontal = MaterialTheme.dimensions.commonSpaceXLarge)
    } else {
        PaddingValues(all = 0.dp)
    }
    val beyondViewportPageCount = if (mode == CarouselMode.DECAL) 2 else PagerDefaults.BeyondViewportPageCount
    val pageSpacing = if (mode == CarouselMode.ZOOM) MaterialTheme.dimensions.commonSpaceMedium else 0.dp
    val endPadding = if (mode == CarouselMode.DECAL) MaterialTheme.dimensions.commonSpaceXLarge else 0.dp

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        // Blur effect is only supported on Android 12 and above
        if (mode == CarouselMode.CUBE && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val offsetFromStart = pagerState.offsetForPage(0).absoluteValue
            // Add a shadow under the carousel
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
                    .zIndex(page.toFloat())
                    .padding(
                        start = endPadding * 2,
                        end = endPadding
                    )
                    .graphicsLayer {
                        customizeGraphicLayer(
                            scope = this,
                            mode = mode,
                            pagerState = pagerState,
                            page = page,
                            offsetY = offsetY
                        )
                    }
                    .drawWithContent {
                        customizeDrawWithContent(
                            scope = this,
                            mode = mode,
                            pagerState = pagerState,
                            page = page
                        )
                    }
            )
        }
    }
}

/**
 * Customization of the graphics layer for each of carousel modes.
 *
 * @param scope      The scope which can be used to define the effects to apply for the content.
 * @param mode       The mode of the carousel.
 * @param pagerState The state to control the carousel.
 * @param page       The current page of the carousel.
 */
private fun customizeGraphicLayer(
    scope: GraphicsLayerScope,
    mode: CarouselMode,
    pagerState: PagerState,
    page: Int,
    offsetY: Float
) {
    when (mode) {
        CarouselMode.ZOOM -> {
            val offset = pagerState.offsetForPage(page).absoluteValue
            lerp(
                start = 0.75f,
                stop = 1f,
                fraction = 1f - offset.coerceIn(0f, 1f)
            ).let { scale ->
                scope.scaleY = scale
            }
        }

        CarouselMode.CUBE -> {
            val offset = pagerState.offsetForPage(page)
            val isOnTheRight = offset < 0f
            val degrees = CUBE_VISUAL_ANGLE
            val interpolated = FastOutLinearInEasing.transform(offset.absoluteValue)
            scope.rotationY = min(interpolated * if (isOnTheRight) degrees else -degrees, RIGHT_ANGLE)

            scope.transformOrigin = TransformOrigin(
                pivotFractionX = if (isOnTheRight) 0f else 1f,
                pivotFractionY = 0.5f
            )
        }

        CarouselMode.CIRCLE_REVEAL -> {
            // Make the left page not move
            val offset = pagerState.offsetForPage(page)
            scope.translationX = scope.size.width * offset

            // Add a circular clipping shape on right page
            val endOffset = pagerState.endOffsetForPage(page)
            scope.shape = CirclePath(
                progress = 1f - endOffset.absoluteValue,
                origin = Offset(
                    x = scope.size.width,
                    y = offsetY
                )
            )
            scope.clip = true

            // Add a parallax effect
            val scale = 1f + (offset.absoluteValue * CIRCLE_PARALLAX_COEFFICIENT)
            scope.scaleX = scale
            scope.scaleY = scale

            // Fade the left page away
            val startOffset = pagerState.startOffsetForPage(page)
            scope.alpha = (2f - startOffset) / 2f
        }

        CarouselMode.DECAL -> {
            // Make the left page not move
            val startOffset = pagerState.startOffsetForPage(page)
            scope.translationX = scope.size.width * startOffset

            // Fade the left page away
            scope.alpha = (2f - startOffset) / 2f

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val blur = (startOffset * BLUR_COEFFICIENT).coerceAtLeast(MINIMUM_BLUR_RADIUS)
                scope.renderEffect = RenderEffect
                    .createBlurEffect(blur, blur, Shader.TileMode.DECAL)
                    .asComposeRenderEffect()
            }

            // Add a parallax effect on the left page
            val scale = 1f - (startOffset * DECAL_PARALLAX_COEFFICIENT)
            scope.scaleX = scale
            scope.scaleY = scale
        }

        else -> { /* Do nothing */ }
    }
}

/**
 * Customization of the layout's content depending of the carousel mode.
 *
 * @param scope      The scope for drawing content.
 * @param mode       The mode of the carousel.
 * @param pagerState The state to control the carousel.
 * @param page       The current page of the carousel.
 */
private fun customizeDrawWithContent(
    scope: ContentDrawScope,
    mode: CarouselMode,
    pagerState: PagerState,
    page: Int
) {
    if (mode == CarouselMode.CUBE) {
        // Dim the cube face as it turns away from the center
        val offset = pagerState.offsetForPage(page)
        scope.drawContent()
        scope.drawRect(color = Color.Black.copy(alpha = offset.absoluteValue * 0.7f))
    } else {
        scope.drawContent()
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
