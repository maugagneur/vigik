package com.kidor.vigik.ui.emulate

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.kidor.vigik.utils.TestUtils.logTestName
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Integration tests for [EmulateFragment]
 */
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class EmulateViewTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun checkUiElements() {
        logTestName()

        val testLog = "Roses are red\n" +
                "Violets are blue\n" +
                "This integration test\n" +
                "Sure must go through"

        composeTestRule.setContent {
            DisplayLogLineState(testLog)
        }

        // Check that the log text is visible
        composeTestRule
            .onNodeWithText(testLog)
            .assertIsDisplayed()
    }
}