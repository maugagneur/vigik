package com.kidor.vigik.extensions

import androidx.annotation.StringRes
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.onNodeWithText
import androidx.test.platform.app.InstrumentationRegistry

/**
 * Finds a semantics node with the given text resource ID.
 *
 * @param stringResourceId The text resource ID.
 * @param substring        Whether to use substring matching.
 * @param ignoreCase       Whether case should be ignored.
 * @param useUnmergedTree  Find within merged composables like Buttons.
 * @see onNodeWithText
 */
fun SemanticsNodeInteractionsProvider.onNodeWithText(
    @StringRes stringResourceId: Int,
    substring: Boolean = false,
    ignoreCase: Boolean = false,
    useUnmergedTree: Boolean = false
): SemanticsNodeInteraction = onNodeWithText(
    text = InstrumentationRegistry.getInstrumentation().targetContext.getString(stringResourceId),
    substring = substring,
    ignoreCase = ignoreCase,
    useUnmergedTree = useUnmergedTree
)

/**
 * Asserts that the node's text contains the given [stringResourceId].
 *
 * This will also search in [SemanticsProperties.EditableText].
 *
 * @param stringResourceId The text resource ID.
 * @param substring        Whether to use substring matching.
 * @param ignoreCase       Whether case should be ignored.
 * @see assertTextContains
 */
fun SemanticsNodeInteraction.assertTextContains(
    @StringRes stringResourceId: Int,
    substring: Boolean = false,
    ignoreCase: Boolean = false
): SemanticsNodeInteraction = assertTextContains(
    value = InstrumentationRegistry.getInstrumentation().targetContext.getString(stringResourceId),
    substring = substring,
    ignoreCase = ignoreCase
)
