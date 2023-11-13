package com.kidor.vigik.ui.animations.typewriter

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.kidor.vigik.extensions.getBoundingBoxesForRange
import com.kidor.vigik.ui.compose.AppTheme
import kotlinx.coroutines.delay
import kotlin.math.min

private const val ADD_CHARACTER_DELAY = 100L
private const val REMOVE_CHARACTER_DELAY = 30L
private const val DELAY_BEFORE_REMOVING_PART = 1000L
private const val DELAY_BEFORE_TYPING_NEW_PART = 500L
private const val HIGHLIGHT_VERTICAL_TRANSLATION_RATIO = -1.5f

/**
 * View that display the section dedicated to typewriter animation.
 */
@Composable
@Preview(widthDp = 400, heightDp = 700)
fun TypewriterScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(all = AppTheme.dimensions.commonSpaceXLarge),
        contentAlignment = Alignment.CenterStart
    ) {
        TypewriterText(
            base = "Never gonna ",
            highlights = listOf(
                "Never",
                "up",
                "run around",
                "desert you",
                "down",
                "cry",
                "goodbye",
                "lie",
                "hurt you"
            ),
            parts = listOf(
                "give you up",
                "let you down",
                "run around and desert you",
                "make you cry",
                "say goodbye",
                "tell a lie and hurt you"
            )
        )
    }
}

/**
 * Animated [Text] like it is created with typewriter.
 *
 * @param base       Base part of the text which will never change.
 * @param highlights Text to highlight.
 * @param parts      Changing part of the text.
 */
@Composable
private fun TypewriterText(
    base: String,
    highlights: List<String>,
    parts: List<String>
) {
    var baseText by remember { mutableStateOf("") }
    var partIndex by remember { mutableIntStateOf(0) }
    var partText by remember { mutableStateOf("") }
    val fullText = "$base${parts[partIndex]}]"
    val textToDisplay = "$baseText$partText"

    LaunchedEffect(key1 = parts) {
        // Type each character of base text
        base.forEachIndexed { charIndex, _ ->
            baseText = base.substring(startIndex = 0, endIndex = charIndex + 1)
            delay(ADD_CHARACTER_DELAY)
        }

        while (true) {
            val part = parts[partIndex]

            // Type each character of the next part one by one
            part.forEachIndexed { charIndex, _ ->
                partText = part.substring(startIndex = 0, endIndex = charIndex + 1)
                delay(ADD_CHARACTER_DELAY)
            }

            delay(DELAY_BEFORE_REMOVING_PART)

            // Remove part's characters one by one quickly
            part.forEachIndexed { charIndex, _ ->
                partText = part.substring(startIndex = 0, endIndex = part.length - (charIndex + 1))
                delay(REMOVE_CHARACTER_DELAY)
            }

            delay(DELAY_BEFORE_TYPING_NEW_PART)

            partIndex = (partIndex + 1) % parts.size
        }
    }

    val highlightColor = MaterialTheme.colorScheme.secondary
    var highlightPartRectList by remember { mutableStateOf(listOf<Rect>()) }

    Text(
        text = textToDisplay,
        modifier = Modifier.drawBehind {
            val borderSize = 20.sp.toPx()
            highlightPartRectList.forEach { rect ->
                val selectedRect = rect.translate(0f, borderSize / HIGHLIGHT_VERTICAL_TRANSLATION_RATIO)
                drawLine(
                    color = highlightColor,
                    start = selectedRect.bottomLeft,
                    end = selectedRect.bottomRight,
                    strokeWidth = borderSize
                )
            }
        },
        color = MaterialTheme.colorScheme.onBackground,
        fontSize = 40.sp,
        fontWeight = FontWeight.SemiBold,
        lineHeight = 52.sp,
        onTextLayout = { layoutResult ->
            // Reset rect list to remove old highlight areas
            highlightPartRectList = emptyList()

            // Search for highlight parts
            highlights.forEach { highlight ->
                val start = fullText.indexOf(highlight)
                if (start >= 0) {
                    val end = min(start + highlight.length - 1, textToDisplay.length - 1)
                    highlightPartRectList += layoutResult.getBoundingBoxesForRange(start, end)
                }
            }
        }
    )
}
