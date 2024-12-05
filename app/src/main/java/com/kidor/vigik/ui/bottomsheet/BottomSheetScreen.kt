package com.kidor.vigik.ui.bottomsheet

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import coil3.SingletonImageLoader
import coil3.compose.AsyncImage
import com.kidor.vigik.R
import com.kidor.vigik.ui.theme.dimensions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

/**
 * View that display the section dedicated to the custom bottom sheet.
 */
@Composable
@Preview(widthDp = 400, heightDp = 700)
fun BottomSheetScreen() {
    val coroutineScope = rememberCoroutineScope()
    val bottomSheetState = rememberBottomSheetState(initialValue = BottomSheetStateValue.HIDDEN)

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = MaterialTheme.dimensions.commonSpaceMedium)
                .padding(top = MaterialTheme.dimensions.commonSpaceMedium),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(
                    id = R.string.bottom_sheet_current_state,
                    bottomSheetState.currentValue
                ),
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(MaterialTheme.dimensions.commonSpaceMedium))
            Text(
                text = stringResource(
                    id = R.string.bottom_sheet_target_state,
                    bottomSheetState.targetValue
                ),
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(MaterialTheme.dimensions.commonSpaceLarge))
            Button(
                onClick = {
                    coroutineScope.launch {
                        if (bottomSheetState.isVisible) {
                            bottomSheetState.hide()
                        } else {
                            bottomSheetState.show()
                        }
                    }
                }
            ) {
                Text(
                    text = if (bottomSheetState.isVisible) {
                        stringResource(id = R.string.bottom_sheet_switch_button_hide_label).uppercase()
                    } else {
                        stringResource(id = R.string.bottom_sheet_switch_button_show_label).uppercase()
                    },
                    fontSize = MaterialTheme.dimensions.textSizeLarge
                )
            }
        }
        CustomBottomSheet(bottomSheetState = bottomSheetState) {
            BottomSheetContent(coroutineScope = coroutineScope, bottomSheetState = bottomSheetState)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun CustomBottomSheet(
    bottomSheetState: BottomSheetState,
    content: @Composable (BoxScope.() -> Unit)
) {
    var layoutHeight by remember { mutableIntStateOf(0) }
    var sheetHeight by remember { mutableIntStateOf(0) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = MaterialTheme.dimensions.commonSpaceLarge)
            .onSizeChanged { size ->
                layoutHeight = size.height
                bottomSheetState.updateAnchors(layoutHeight, sheetHeight)
            }
            .offset {
                val yOffset = bottomSheetState
                    .requireOffset()
                    .roundToInt()
                IntOffset(x = 0, y = yOffset)
            }
            .anchoredDraggable(
                state = bottomSheetState.draggableState,
                orientation = Orientation.Vertical
            )
            .background(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(
                    topStart = MaterialTheme.dimensions.commonSpaceMedium,
                    topEnd = MaterialTheme.dimensions.commonSpaceMedium
                )
            )
    ) {
        Column(
            modifier = Modifier
                .padding(vertical = MaterialTheme.dimensions.commonSpaceMedium)
                .onSizeChanged { size ->
                    sheetHeight = size.height
                    bottomSheetState.updateAnchors(layoutHeight, sheetHeight)
                }
        ) {
            // Add a fake modal drag line
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = MaterialTheme.dimensions.commonSpaceXXSmall),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .width(MaterialTheme.dimensions.commonSpaceXLarge)
                        .height(3.dp)
                        .clip(shape = RoundedCornerShape(size = MaterialTheme.dimensions.commonSpaceXSmall))
                        .background(color = MaterialTheme.colorScheme.onPrimaryContainer)
                )
            }
            // The bottom sheet's content
            this@Box.content()
        }
    }
}

@Composable
private fun BottomSheetContent(coroutineScope: CoroutineScope, bottomSheetState: BottomSheetState) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(id = R.string.bottom_sheet_custom_bottom_sheet_title),
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.headlineLarge
        )
        Spacer(modifier = Modifier.height(MaterialTheme.dimensions.commonSpaceMedium))
        Button(
            onClick = {
                coroutineScope.launch {
                    if (bottomSheetState.isHalfExpanded) {
                        bottomSheetState.expand()
                    } else {
                        bottomSheetState.halfExpand()
                    }
                }
            }
        ) {
            Text(
                text = if (bottomSheetState.isExpanded) {
                    stringResource(id = R.string.bottom_sheet_switch_button_reduce_label).uppercase()
                } else {
                    stringResource(id = R.string.bottom_sheet_switch_button_expand_label).uppercase()
                },
                fontSize = MaterialTheme.dimensions.textSizeMedium
            )
        }
        Spacer(modifier = Modifier.height(MaterialTheme.dimensions.commonSpaceMedium))
        AsyncImage(
            model = R.drawable.notification_big_picture,
            contentDescription = "Bottom sheet content",
            imageLoader = SingletonImageLoader.get(LocalContext.current),
            modifier = Modifier.padding(horizontal = MaterialTheme.dimensions.commonSpaceMedium)
        )
        Spacer(modifier = Modifier.height(MaterialTheme.dimensions.commonSpaceXLarge))
    }
}
