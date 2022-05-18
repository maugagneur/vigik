package com.kidor.vigik.ui.scan

import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertRangeInfoEquals
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.runComposeUiTest
import androidx.fragment.app.FragmentActivity
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.kidor.vigik.R
import com.kidor.vigik.extensions.launchFragmentInHiltContainer
import com.kidor.vigik.nfc.model.Tag
import com.kidor.vigik.utils.EspressoUtils.checkToastWithTextIsVisible
import com.kidor.vigik.utils.TestUtils.logTestName
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Integration tests for [ScanFragment].
 */
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class ScanViewTest {

    @Test
    @OptIn(ExperimentalTestApi::class)
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

            // Check that the textview for tag's data is hidden
            onNodeWithTag(TAG_DATA_TEXT_TEST_TAG)
                .assertDoesNotExist()

            // Check that the floating action button for saving tag is hidden
            onNodeWithTag(FLOATING_ACTION_BUTTON_TEST_TAG)
                .assertDoesNotExist()
        }
    }

    @Test
    @OptIn(ExperimentalTestApi::class)
    fun checkDisplayTagStateWithValidTag() {
        logTestName()

        val tag = Tag()

        runComposeUiTest {
            setContent {
                DisplayTagState(DisplayTagStateData(ScanViewState.DisplayTag(tag, true)))
            }

            // Check that loader is not visible
            onNodeWithTag(PROGRESS_BAR_TEST_TAG)
                .assertDoesNotExist()

            // Check that the textview for tag's data is visible
            onNodeWithText(tag.toString())
                .assertIsDisplayed()

            // Check that the floating action button for saving tag is visible
            onNodeWithTag(FLOATING_ACTION_BUTTON_TEST_TAG)
                .assertIsDisplayed()
        }
    }

    @Test
    @OptIn(ExperimentalTestApi::class)
    fun checkDisplayTagStateWithInvalidTag() {
        logTestName()

        val tag = Tag()

        runComposeUiTest {
            setContent {
                DisplayTagState(DisplayTagStateData(ScanViewState.DisplayTag(tag, false)))
            }

            // Check that loader is not visible
            onNodeWithTag(PROGRESS_BAR_TEST_TAG)
                .assertDoesNotExist()

            // Check that the textview for tag's data is visible
            onNodeWithText(tag.toString())
                .assertIsDisplayed()

            // Check that the floating action button for saving tag is hidden
            onNodeWithTag(FLOATING_ACTION_BUTTON_TEST_TAG)
                .assertDoesNotExist()
        }
    }

    @Ignore("It seems there are some issues with Toast assertion with API 30 and upper -> https://github.com/android/android-test/issues/803")
    @Test
    fun checkUiElementsWhenTagSavedSuccessfully() {
        logTestName()

        var parentActivity: FragmentActivity? = null

        // Load fragment in empty fragment activity and force state `DisplayTag`
        launchFragmentInHiltContainer<ScanFragment> {
            //fragment.eventRender(ScanViewEvent.SaveTagSuccess)
            parentActivity = activity
        }

        // Check that a toast with successful message is displayed
        checkToastWithTextIsVisible(parentActivity, R.string.save_tag_success, "Toast of tag saving result")
    }

    @Ignore("It seems there are some issues with Toast assertion with API 30 and upper -> https://github.com/android/android-test/issues/803")
    @Test
    fun checkUiElementsWhenTagSavingFailed() {
        logTestName()

        var parentActivity: FragmentActivity? = null

        // Load fragment in empty fragment activity and force state `DisplayTag`
        launchFragmentInHiltContainer<ScanFragment> {
            //fragment.eventRender(ScanViewEvent.SaveTagFailure)
            parentActivity = activity
        }

        // Check that a toast with failure message is displayed
        checkToastWithTextIsVisible(parentActivity, R.string.save_tag_fail, "Toast of tag saving result")
    }
}