package com.kidor.vigik.ui.animations.followingarrows

import android.content.res.Configuration
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateOffsetAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.toSize
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.max
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sqrt

private const val SHORT_ELEMENT_NUMBER = 5
private const val LARGE_ELEMENT_NUMBER = 10
private const val DISPLACEMENT = 50f
private const val SCALE_COEFFICIENT = 0.9f
private const val MINIMUM_SCALE_VALUE = 0.4f
private const val FULL_CIRCLE_ANGLE = 360f
private const val HALF_CIRCLE_ANGLE = 180f

/**
 * View that display the section dedicated to the following arrows animation.
 */
@Composable
@Preview(widthDp = 400, heightDp = 700)
@Suppress("LongMethod")
fun FollowingArrowsScreen() {
    var size by remember { mutableStateOf(Size.Zero) }
    var position by remember { mutableStateOf(Offset.Zero) }

    val columnNb: Int
    val rowNb: Int
    // Adjust row and columns to orientation
    if (LocalConfiguration.current.orientation == Configuration.ORIENTATION_PORTRAIT) {
        columnNb = SHORT_ELEMENT_NUMBER
        rowNb = LARGE_ELEMENT_NUMBER
    } else {
        columnNb = LARGE_ELEMENT_NUMBER
        rowNb = SHORT_ELEMENT_NUMBER
    }

    Column(
        modifier = Modifier
            .onSizeChanged { size = it.toSize() }
            .fillMaxSize()
            .pointerInput(Unit) {
                detectDragGestures { change, _ ->
                    position = Offset(max(0f, change.position.x), max(0f, change.position.y + (size.height / rowNb)))
                }
            }
    ) {
        repeat(rowNb) {
            Row(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(columnNb) {
                    var layoutCoordinates: LayoutCoordinates? by remember { mutableStateOf(null) }
                    var rotation: Float by remember { mutableFloatStateOf(0f) }
                    var scale: Float by remember { mutableFloatStateOf(1f) }
                    var offset by remember { mutableStateOf(Offset.Zero) }

                    LaunchedEffect(layoutCoordinates, position) {
                        layoutCoordinates?.let {
                            val center = it.boundsInRoot().center
                            val delta = center - position

                            val angle = (atan2(delta.y, delta.x) * HALF_CIRCLE_ANGLE / PI).toFloat()

                            @Suppress("MaximumLineLength", "MaxLineLength")
                            rotation += ((((angle - rotation) % FULL_CIRCLE_ANGLE) + FULL_CIRCLE_ANGLE + HALF_CIRCLE_ANGLE) % FULL_CIRCLE_ANGLE) - HALF_CIRCLE_ANGLE

                            val diagonal = sqrt(size.width.pow(2) + size.height.pow(2))
                            val distance = sqrt(delta.x.pow(2) + delta.y.pow(2))

                            offset = Offset(
                                x = DISPLACEMENT * (delta.x / distance),
                                y = DISPLACEMENT * (delta.y / distance)
                            )
                            scale = max(1f - (distance / (diagonal * SCALE_COEFFICIENT)), MINIMUM_SCALE_VALUE)
                        }
                    }

                    val animatedRotation by animateFloatAsState(
                        targetValue = rotation,
                        animationSpec = spring(
                            stiffness = Spring.StiffnessVeryLow
                        ),
                        label = "Rotation animation"
                    )

                    val animatedScale by animateFloatAsState(
                        targetValue = scale,
                        animationSpec = spring(
                            stiffness = Spring.StiffnessVeryLow,
                            dampingRatio = Spring.DampingRatioLowBouncy
                        ),
                        label = "Scale animation"
                    )

                    val animatedOffset by animateOffsetAsState(
                        targetValue = offset,
                        animationSpec = spring(
                            stiffness = Spring.StiffnessVeryLow,
                            dampingRatio = Spring.DampingRatioMediumBouncy
                        ),
                        label = "Offset animation"
                    )

                    Box(
                        modifier = Modifier
                            .onGloballyPositioned { layoutCoordinates = it }
                            .weight(1f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = null,
                            modifier = Modifier
                                .offset { IntOffset(animatedOffset.x.roundToInt(), animatedOffset.y.roundToInt()) }
                                .rotate(animatedRotation)
                                .scale(animatedScale)
                                .fillMaxSize(),
                            colorFilter = ColorFilter.tint(
                                lerp(
                                    start = MaterialTheme.colorScheme.primary,
                                    stop = MaterialTheme.colorScheme.secondary,
                                    fraction = 1f - scale
                                )
                            )
                        )
                    }
                }
            }
        }
    }
}
