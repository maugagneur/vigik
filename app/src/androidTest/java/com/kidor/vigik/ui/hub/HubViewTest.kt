package com.kidor.vigik.ui.hub

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.kidor.vigik.R
import com.kidor.vigik.extensions.onNodeWithText
import com.kidor.vigik.utils.TestUtils.logTestName
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations

/**
 * Integration tests for Hub screen.
 */
@RunWith(AndroidJUnit4::class)
class HubViewTest {

    private lateinit var closeable: AutoCloseable

    @Mock
    private lateinit var viewActionCallback: (HubViewAction) -> Unit

    @Before
    fun setUp() {
        closeable = MockitoAnnotations.openMocks(this)
    }

    @After
    fun tearDown() {
        closeable.close()
    }

    @Test
    @OptIn(ExperimentalTestApi::class)
    fun checkDefaultState() {
        logTestName()

        runComposeUiTest {
            setContent {
                DefaultState(DefaultStateData(viewActionCallback))
            }

            // Check that button to start scanning tag is visible
            onNodeWithText(stringResourceId = R.string.scan_button_label, ignoreCase = true)
                .assertIsDisplayed()

            // Check that button to see the tag history is visible
            onNodeWithText(stringResourceId = R.string.history_button_label, ignoreCase = true)
                .assertIsDisplayed()

            // Check that button to start emulating tag is visible
            onNodeWithText(stringResourceId = R.string.emulate_button_label, ignoreCase = true)
                .assertIsDisplayed()

            // Check that a click on "NFC scan" button generates a RefreshNfcStatus action
            onNodeWithText(stringResourceId = R.string.scan_button_label, ignoreCase = true)
                .performClick()
            verify(viewActionCallback).invoke(HubViewAction.DisplayScanTagView)

            // Check that a click on "Tags history" button generates a DisplayTagHistoryView action
            onNodeWithText(stringResourceId = R.string.history_button_label, ignoreCase = true)
                .performClick()
            verify(viewActionCallback).invoke(HubViewAction.DisplayTagHistoryView)

            // Check that a click on "Emulate NFC tag" button generates a DisplayEmulateTagView action
            onNodeWithText(stringResourceId = R.string.emulate_button_label, ignoreCase = true)
                .performClick()
            verify(viewActionCallback).invoke(HubViewAction.DisplayEmulateTagView)
        }
    }
}
