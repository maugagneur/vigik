package com.kidor.vigik.ui.animations.shape

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asComposePath
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.graphics.shapes.CornerRounding
import androidx.graphics.shapes.Morph
import androidx.graphics.shapes.RoundedPolygon
import androidx.graphics.shapes.circle
import androidx.graphics.shapes.star
import androidx.graphics.shapes.toPath
import com.kidor.vigik.ui.compose.AppTheme

private const val VERTICES_NUMBER = 12
private const val PROGRESS_ANIMATION_DURATION = 4000
private val STROKE_WIDTH = 8.dp

/**
 * View that display the section dedicated to an infinite progress bar using graphics-shape's library.
 */
@Composable
@Preview(widthDp = 400, heightDp = 700)
fun ShapeScreen() {
    val starPolygon = remember {
        RoundedPolygon.star(
            numVerticesPerRadius = VERTICES_NUMBER,
            innerRadius = 1f / 3f,
            rounding = CornerRounding(radius = 1f / 6f)
        )
    }
    val circlePolygon = remember {
        RoundedPolygon.circle(
            numVertices = VERTICES_NUMBER
        )
    }
    val morph = remember { Morph(starPolygon, circlePolygon) }

    val infiniteTransition = rememberInfiniteTransition(label = "Shape infinite transition")
    val progress = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            tween(durationMillis = PROGRESS_ANIMATION_DURATION, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "Progress animation"
    )

    var morphPath = remember { Path() }
    var androidPath = remember { android.graphics.Path() }
    var matrix = remember { Matrix() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(all = AppTheme.dimensions.commonSpaceMedium)
            .drawWithCache {
                androidPath = morph.toPath(progress.value, androidPath)
                morphPath = androidPath.asComposePath()
                matrix.reset()
                // Took the maximum space available (depending of screen orientation)
                val matrixSize = size.minDimension / 2f
                matrix.scale(x = matrixSize, y = matrixSize)
                morphPath.transform(matrix)

                onDrawBehind {
                    // Translate to the center of the screen
                    translate(left = size.width / 2f, top = size.height / 2f) {
                        drawPath(
                            path = morphPath,
                            color = Color.Blue,
                            style = Stroke(width = STROKE_WIDTH.toPx())
                        )
                    }
                }
            }
    )
}
