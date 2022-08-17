package com.kidor.vigik.ui.emulate

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.runComposeUiTest
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.kidor.vigik.utils.TestUtils.logTestName
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Integration tests for Emulate screen.
 */
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class EmulateViewTest {

    @Test
    @OptIn(ExperimentalTestApi::class)
    fun checkUiElements() {
        logTestName()

        val testLog = "Roses are red\n" +
                "Violets are blue\n" +
                "This integration test\n" +
                "Sure must go through"

        runComposeUiTest {
            setContent {
                DisplayLogLineState(testLog)
            }

            // Check that the log text is visible
            onNodeWithText(testLog)
                .assertIsDisplayed()
        }
    }
}