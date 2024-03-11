package com.kidor.vigik.ui.bottomsheet

import androidx.annotation.VisibleForTesting
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.animateTo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.key
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable

/**
 * State of the custom bottom sheet component.
 *
 * @param initialValue        The initial value of the state.
 * @param animationSpec       The default animation that will be used to animate to a new state.
 * @param confirmValueChanged Optional callback invoked to confirm or veto a pending state change.
 */
@OptIn(ExperimentalFoundationApi::class)
@Stable
class BottomSheetState(
    initialValue: BottomSheetStateValue,
    animationSpec: AnimationSpec<Float>,
    confirmValueChanged: (BottomSheetStateValue) -> Boolean
) {
    /**
     * The state of ongoing drag or animation of the bottom sheet.
     */
    val draggableState = AnchoredDraggableState(
        initialValue = initialValue,
        animationSpec = animationSpec,
        positionalThreshold = { distance: Float -> distance * 0.5f },
        velocityThreshold = { 125f },
        confirmValueChange = confirmValueChanged
    )

    /**
     * The current value of the bottom sheet state.
     */
    val currentValue: BottomSheetStateValue
        get() = draggableState.currentValue

    /**
     * The target value of the bottom sheet state.
     */
    val targetValue: BottomSheetStateValue
        get() = draggableState.targetValue

    /**
     * Returns true if the bottom sheet is visible (event partially).
     */
    val isVisible: Boolean
        get() = currentValue != BottomSheetStateValue.HIDDEN

    /**
     * Returns true if the bottom sheet is fully expanded.
     */
    val isExpanded: Boolean
        get() = currentValue == BottomSheetStateValue.EXPANDED

    /**
     * Returns true if the bottom sheet is half expanded.
     */
    val isHalfExpanded: Boolean
        get() = currentValue == BottomSheetStateValue.HALF_EXPANDED

    private val hasHalfExpandedState: Boolean
        get() = draggableState.anchors.hasAnchorFor(BottomSheetStateValue.HALF_EXPANDED)

    /**
     * Show the bottom sheet with animation and suspend until it's shown.
     * If the sheet is taller than 50% of the parent's height, the bottom sheet will be half expanded.
     * Otherwise, it will be fully expanded.
     */
    suspend fun show() {
        val targetValue = when {
            hasHalfExpandedState -> BottomSheetStateValue.HALF_EXPANDED
            else -> BottomSheetStateValue.EXPANDED
        }
        animateTo(targetValue)
    }

    /**
     * Expands the bottom sheet with an animation and suspend it until the animation finishes or is cancelled.
     */
    suspend fun expand() {
        if (draggableState.anchors.hasAnchorFor(BottomSheetStateValue.EXPANDED)) {
            animateTo(BottomSheetStateValue.EXPANDED)
        }
    }

    /**
     * Half expands the bottom sheet with an animation and suspend it until the animation finishes or is cancelled.
     */
    suspend fun halfExpand() {
        if (draggableState.anchors.hasAnchorFor(BottomSheetStateValue.HALF_EXPANDED)) {
            animateTo(BottomSheetStateValue.HALF_EXPANDED)
        }
    }

    /**
     * Hides the bottom sheet with an animation and suspend it until the animation finishes or is cancelled.
     */
    suspend fun hide() {
        animateTo(BottomSheetStateValue.HIDDEN)
    }

    /**
     * Requires the current offset.
     */
    fun requireOffset() = draggableState.requireOffset()

    @VisibleForTesting
    internal suspend fun animateTo(
        targetValue: BottomSheetStateValue,
        velocity: Float = draggableState.lastVelocity
    ) = draggableState.animateTo(targetValue, velocity)

    /**
     * Updates bottom sheet's anchors based on it's height and the screen's height.
     *
     * @param layoutHeight The layout's height.
     * @param sheetHeight  The bottom sheet's height.
     */
    fun updateAnchors(layoutHeight: Int, sheetHeight: Int) {
        if (layoutHeight <= 0 || sheetHeight <= 0) return
        val newAnchors = DraggableAnchors {
            BottomSheetStateValue.entries
                .forEach { anchor ->
                    val dragEndPoint = layoutHeight - (sheetHeight * anchor.draggableFraction)
                    anchor at dragEndPoint
                }
        }
        draggableState.updateAnchors(newAnchors)
    }

    companion object {
        /**
         * The default [Saver] implementation for [BottomSheetState].
         */
        fun Saver(
            animationSpec: AnimationSpec<Float>,
            confirmValueChanged: (BottomSheetStateValue) -> Boolean
        ): Saver<BottomSheetState, BottomSheetStateValue> = Saver(
            save = { currentState -> currentState.currentValue },
            restore = { savedState ->
                BottomSheetState(
                    initialValue = savedState,
                    animationSpec = animationSpec,
                    confirmValueChanged = confirmValueChanged
                )
            }
        )
    }
}

/**
 * Remember the value of a [BottomSheetState].
 *
 * @param initialValue        The initial value of the state.
 * @param animationSpec       The default animation that will be used to animate to a new state.
 * @param confirmValueChanged Optional callback invoked to confirm or veto a pending state change.
 */
@Composable
fun rememberBottomSheetState(
    initialValue: BottomSheetStateValue,
    animationSpec: AnimationSpec<Float> = spring(
        dampingRatio = Spring.DampingRatioLowBouncy,
        stiffness = Spring.StiffnessMedium
    ),
    confirmValueChanged: (BottomSheetStateValue) -> Boolean = { true },
): BottomSheetState {
    return key(initialValue) {
        rememberSaveable(
            initialValue,
            animationSpec,
            confirmValueChanged,
            saver = BottomSheetState.Saver(
                animationSpec = animationSpec,
                confirmValueChanged = confirmValueChanged
            )
        ) {
            BottomSheetState(
                initialValue = initialValue,
                animationSpec = animationSpec,
                confirmValueChanged = confirmValueChanged
            )
        }
    }
}
