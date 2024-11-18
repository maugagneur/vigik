package com.kidor.vigik.ui.animations.lookahead

import androidx.compose.animation.core.DeferredTargetAnimation
import androidx.compose.animation.core.ExperimentalAnimatableApi
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.movableContentWithReceiverOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.LookaheadScope
import androidx.compose.ui.layout.approachLayout
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.round
import com.kidor.vigik.ui.theme.dimensions

private const val OFFSET_ANIMATION_DURATION = 1000
private const val SIZE_ANIMATION_DURATION = 500
private val colors = listOf(
    Color.Red,
    Color.Yellow,
    Color.Blue,
    Color.Green
)

/**
 * View that display the section dedicated to the Lookahead demo animation.
 */
@Composable
@Preview(widthDp = 400, heightDp = 700)
fun LookaheadScreen() {
    var isInColumn by remember { mutableStateOf(true) }
    val items = remember {
        movableContentWithReceiverOf<LookaheadScope> {
            colors.forEach { color ->
                Box(
                    modifier = Modifier
                        .padding(all = MaterialTheme.dimensions.commonSpaceSmall)
                        .size(if (isInColumn) 80.dp else 20.dp)
                        .animateBounds()
                        .background(color = color, shape = RoundedCornerShape(16.dp))
                )
            }
        }
    }

    LookaheadScope {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable { isInColumn = !isInColumn },
            contentAlignment = Alignment.Center
        ) {
            if (isInColumn) {
                Column { items() }
            } else {
                Row { items() }
            }
        }
    }
}

context(LookaheadScope)
@OptIn(ExperimentalAnimatableApi::class)
private fun Modifier.animateBounds(): Modifier = composed {
    val offsetAnim = remember { DeferredTargetAnimation(IntOffset.VectorConverter) }
    val sizeAnim = remember { DeferredTargetAnimation(IntSize.VectorConverter) }
    val scope = rememberCoroutineScope()
    this.approachLayout(
        isMeasurementApproachInProgress = {
            sizeAnim.updateTarget(it, scope, tween(durationMillis = SIZE_ANIMATION_DURATION))
            !sizeAnim.isIdle
        },
        isPlacementApproachInProgress = {
            val target = lookaheadScopeCoordinates.localLookaheadPositionOf(it)
            offsetAnim.updateTarget(target.round(), scope, tween(durationMillis = OFFSET_ANIMATION_DURATION))
            !offsetAnim.isIdle
        }
    ) { measurable, _ ->
        val (animWidth, animHeight) = sizeAnim.updateTarget(lookaheadSize, scope)
        measurable.measure(Constraints.fixed(animWidth, animHeight))
            .run {
                layout(width, height) {
                    coordinates?.let {
                        val target = lookaheadScopeCoordinates.localLookaheadPositionOf(it).round()
                        val animOffset = offsetAnim.updateTarget(target, scope)
                        val current = lookaheadScopeCoordinates.localPositionOf(it, Offset.Zero).round()
                        val (x, y) = animOffset - current
                        place(x, y)
                    } ?: place(0, 0)
                }
            }
    }
}
