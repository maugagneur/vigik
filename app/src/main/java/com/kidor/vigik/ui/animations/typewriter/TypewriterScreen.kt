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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.kidor.vigik.ui.compose.AppTheme
import kotlinx.coroutines.delay

private const val ADD_CHARACTER_DELAY = 100L
private const val REMOVE_CHARACTER_DELAY = 30L
private const val DELAY_BEFORE_REMOVING_PART = 1000L
private const val DELAY_BEFORE_TYPING_NEW_PART = 500L

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
            base = "Never gonna",
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
 * @param base          Base part of the text which will never change.
 * @param parts         Animated part of the text.
 */
@Composable
private fun TypewriterText(
    base: String,
    parts: List<String>
) {
    var baseText by remember { mutableStateOf("") }
    var partIndex by remember { mutableIntStateOf(0) }
    var partText by remember { mutableStateOf("") }
    val textToDisplay = "$baseText $partText"

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

    Text(
        text = textToDisplay,
        color = MaterialTheme.colorScheme.onBackground,
        fontSize = 40.sp,
        fontWeight = FontWeight.SemiBold
    )
}
