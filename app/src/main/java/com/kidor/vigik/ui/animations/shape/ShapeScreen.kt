package com.kidor.vigik.ui.animations.shape

import androidx.compose.animation.core.InfiniteTransition
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.asComposePath
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.graphics.shapes.CornerRounding
import androidx.graphics.shapes.Morph
import androidx.graphics.shapes.RoundedPolygon
import androidx.graphics.shapes.circle
import androidx.graphics.shapes.star
import androidx.graphics.shapes.toPath
import com.kidor.vigik.ui.theme.dimensions

private const val VERTICES_NUMBER = 12
private const val STAR_INNER_RADIUS = 0.4f
private const val STAR_ROUNDING_RADIUS = 0.05f
private const val PROGRESS_ANIMATION_DURATION = 4000
private const val FULL_CIRCLE_ANGLE = 360f
private val STROKE_WIDTH = 8.dp
@Suppress("MagicNumber")
private val rainbowColors = listOf(
    Color(0xFF3FCEBC),
    Color(0xFF3CBCEB),
    Color(0xFF5F96E7),
    Color(0xFF816FE3),
    Color(0xFF9F5EE2),
    Color(0xFFBD4CE0),
    Color(0xFFDE589F),
    Color(0xFFFF645E),
    Color(0xFFFDA859),
    Color(0xFFFAEC54),
    Color(0xFF9EE671),
    Color(0xFF67E282),
    Color(0xFF3FCEBC)
)

/**
 * View that display the section dedicated to an infinite progress bar using graphics-shape's library.
 */
@Composable
@Preview(widthDp = 400, heightDp = 700)
fun ShapeScreen() {
    val starPolygon = remember {
        RoundedPolygon.star(
            numVerticesPerRadius = VERTICES_NUMBER,
            innerRadius = STAR_INNER_RADIUS,
            rounding = CornerRounding(radius = STAR_ROUNDING_RADIUS)
        )
    }
    val circlePolygon = remember {
        RoundedPolygon.circle(
            numVertices = VERTICES_NUMBER
        )
    }
    val morph = remember { Morph(starPolygon, circlePolygon) }

    val infiniteTransition = rememberInfiniteTransition(label = "Shape infinite transition")
    val progress = createProgressAnimation(transition = infiniteTransition)
    val rotation = createRotationAnimation(transition = infiniteTransition)

    val shapePath = remember { Path() }
    var androidPath = remember { android.graphics.Path() }
    val matrix = remember { Matrix() }
    val pathMeasurer = remember { PathMeasure() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(all = MaterialTheme.dimensions.commonSpaceMedium)
            .drawWithCache {
                androidPath = morph.toPath(progress.value, androidPath)
                val polygonPath = androidPath.asComposePath()
                matrix.reset()
                // Took the maximum space available (depending of screen orientation)
                val matrixSize = size.minDimension / 2f
                matrix.scale(x = matrixSize, y = matrixSize)
                polygonPath.transform(matrix)

                // Draw only a segment of the polygon, depending on progress value
                pathMeasurer.setPath(path = polygonPath, forceClosed = false)
                val totalLength = pathMeasurer.length
                shapePath.reset()
                pathMeasurer.getSegment(
                    startDistance = 0f,
                    stopDistance = totalLength * progress.value,
                    destination = shapePath
                )

                onDrawBehind {
                    rotate(rotation.value) {
                        // Translate to the center of the screen
                        translate(left = size.width / 2f, top = size.height / 2f) {
                            drawPath(
                                path = shapePath,
                                brush = Brush.sweepGradient(
                                    colors = rainbowColors,
                                    center = Offset(x = 0.5f, y = 0.5f)
                                ),
                                style = Stroke(width = STROKE_WIDTH.toPx(), cap = StrokeCap.Round)
                            )
                        }
                    }
                }
            }
    )
}

@Composable
private fun createProgressAnimation(transition: InfiniteTransition): State<Float> = transition.animateFloat(
    initialValue = 0f,
    targetValue = 1f,
    animationSpec = infiniteRepeatable(
        animation = tween(durationMillis = PROGRESS_ANIMATION_DURATION, easing = LinearEasing),
        repeatMode = RepeatMode.Reverse
    ),
    label = "Progress animation"
)

@Composable
private fun createRotationAnimation(transition: InfiniteTransition): State<Float> = transition.animateFloat(
    initialValue = 0f,
    targetValue = FULL_CIRCLE_ANGLE,
    animationSpec = infiniteRepeatable(
        animation = tween(durationMillis = PROGRESS_ANIMATION_DURATION, easing = LinearEasing),
        repeatMode = RepeatMode.Reverse
    ),
    label = "Rotation animation"
)
