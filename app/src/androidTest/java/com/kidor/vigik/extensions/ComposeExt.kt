package com.kidor.vigik.extensions

import androidx.annotation.StringRes
import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
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
) = onNodeWithText(
    text = InstrumentationRegistry.getInstrumentation().targetContext.getString(stringResourceId),
    substring = substring,
    ignoreCase = ignoreCase,
    useUnmergedTree = useUnmergedTree
)
