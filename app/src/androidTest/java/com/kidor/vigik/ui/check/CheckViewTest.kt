package com.kidor.vigik.ui.check

import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertRangeInfoEquals
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.kidor.vigik.R
import com.kidor.vigik.extensions.onNodeWithText
import com.kidor.vigik.utils.TestUtils.logTestName
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations

/**
 * Integration tests for Check screen.
 */
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class CheckViewTest {

    private lateinit var closeable: AutoCloseable

    @Mock
    private lateinit var refreshCallback: () -> Unit
    @Mock
    private lateinit var settingsCallback: () -> Unit

    @Before
    fun setUp() {
        closeable = MockitoAnnotations.openMocks(this)
    }

    @After
    fun tearDown() {
        closeable.close()
    }

    @ExperimentalTestApi
    @Test
    fun checkLoadingState() {
        logTestName()

        runComposeUiTest {
            setContent {
                LoadingState()
            }

            // Check that loader is visible
            onNodeWithTag(PROGRESS_BAR_TEST_TAG)
                .assertIsDisplayed()
                .assertRangeInfoEquals(ProgressBarRangeInfo.Indeterminate)

            // Check that refresh button is hidden
            onNodeWithText(stringResourceId = R.string.nfc_state_refresh)
                .assertDoesNotExist()

            // Check that settings button is hidden
            onNodeWithText(stringResourceId = R.string.nfc_settings)
                .assertDoesNotExist()
        }
    }

    @ExperimentalTestApi
    @Test
    fun checkNfcDisableState() {
        logTestName()

        runComposeUiTest {
            setContent {
                NfcIsDisableState(NfcIsDisableStateData(refreshCallback, settingsCallback))
            }

            // Check that loader is not visible
            onNodeWithTag(PROGRESS_BAR_TEST_TAG)
                .assertDoesNotExist()

            // Check that refresh button is visible
            onNodeWithText(stringResourceId = R.string.nfc_state_refresh, ignoreCase = true)
                .assertIsDisplayed()

            // Check that settings button is visible
            onNodeWithText(stringResourceId = R.string.nfc_settings, ignoreCase = true)
                .assertIsDisplayed()

            // Check that a click on refresh button generates a RefreshNfcStatus action
            onNodeWithText(stringResourceId = R.string.nfc_state_refresh, ignoreCase = true)
                .performClick()
            verify(refreshCallback).invoke()

            // Check that a click on refresh button generates a DisplayNfcSettings action
            onNodeWithText(stringResourceId = R.string.nfc_settings, ignoreCase = true)
                .performClick()
            verify(settingsCallback).invoke()
        }
    }
}
