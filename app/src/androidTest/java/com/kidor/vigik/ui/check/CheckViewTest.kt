package com.kidor.vigik.ui.check

import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertRangeInfoEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.kidor.vigik.R
import com.kidor.vigik.extensions.onNodeWithText
import com.kidor.vigik.utils.TestUtils.logTestName
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations

/**
 * Integration tests for [CheckFragment].
 */
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class CheckViewTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var closeable: AutoCloseable

    @Mock
    private lateinit var viewActionCallback: (CheckViewAction) -> Unit

    @Before
    fun setUp() {
        closeable = MockitoAnnotations.openMocks(this)
    }

    @After
    fun tearDown() {
        closeable.close()
    }

    @Test
    fun checkLoadingState() {
        logTestName()

        composeTestRule.setContent {
            LoadingState()
        }

        // Check that loader is visible
        composeTestRule
            .onNodeWithTag(PROGRESS_BAR_TEST_TAG)
            .assertIsDisplayed()
            .assertRangeInfoEquals(ProgressBarRangeInfo.Indeterminate)

        // Check that refresh button is hidden
        composeTestRule
            .onNodeWithText(stringResourceId = R.string.nfc_state_refresh)
            .assertDoesNotExist()

        // Check that settings button is hidden
        composeTestRule
            .onNodeWithText(stringResourceId = R.string.nfc_settings)
            .assertDoesNotExist()
    }

    @Test
    fun checkNfcDisableState() {
        logTestName()

        composeTestRule.setContent {
            NfcIsDisableState(onViewAction = viewActionCallback)
        }

        // Check that loader is not visible
        composeTestRule
            .onNodeWithTag(PROGRESS_BAR_TEST_TAG)
            .assertDoesNotExist()

        // Check that refresh button is visible
        composeTestRule
            .onNodeWithText(stringResourceId = R.string.nfc_state_refresh, ignoreCase = true)
            .assertIsDisplayed()

        // Check that settings button is visible
        composeTestRule
            .onNodeWithText(stringResourceId = R.string.nfc_settings, ignoreCase = true)
            .assertIsDisplayed()

        // Check that a click on refresh button generates a RefreshNfcStatus action
        composeTestRule
            .onNodeWithText(stringResourceId = R.string.nfc_state_refresh, ignoreCase = true)
            .performClick()
        verify(viewActionCallback).invoke(CheckViewAction.RefreshNfcStatus)

        // Check that a click on refresh button generates a DisplayNfcSettings action
        composeTestRule
            .onNodeWithText(stringResourceId = R.string.nfc_settings, ignoreCase = true)
            .performClick()
        verify(viewActionCallback).invoke(CheckViewAction.DisplayNfcSettings)
    }
}