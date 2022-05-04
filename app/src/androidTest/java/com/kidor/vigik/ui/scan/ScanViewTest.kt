package com.kidor.vigik.ui.scan

import androidx.fragment.app.FragmentActivity
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.kidor.vigik.R
import com.kidor.vigik.extensions.launchFragmentInHiltContainer
import com.kidor.vigik.nfc.model.Tag
import com.kidor.vigik.utils.EspressoUtils.checkToastWithTextIsVisible
import com.kidor.vigik.utils.EspressoUtils.checkViewIsNotVisible
import com.kidor.vigik.utils.EspressoUtils.checkViewIsVisible
import com.kidor.vigik.utils.TestUtils.logTestName
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Integration tests for [ScanFragment].
 */
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class ScanViewTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Test
    fun checkUiElementsAtStart() {
        logTestName()

        // Load fragment in empty fragment activity
        launchFragmentInHiltContainer<ScanFragment>()

        // Check that loader is visible
        checkViewIsVisible(R.id.progress_bar, "Loader")

        // Check that the textview for tag's data is hidden
        checkViewIsNotVisible(R.id.tag_information_textview, "Tag information")

        // Check that the floating action button for saving tag is hidden
        checkViewIsNotVisible(R.id.save_fab, "Save FAB")
    }

    @Test
    fun checkUiElementsWhenDisplayingValidTag() {
        logTestName()

        // Load fragment in empty fragment activity and force state `DisplayTag`
        launchFragmentInHiltContainer<ScanFragment> { fragment ->
            val tag = Tag()
            fragment.stateRender(ScanViewState.DisplayTag(tag, true))
        }

        // Check that loader is visible
        checkViewIsNotVisible(R.id.progress_bar, "Loader")

        // Check that the textview for tag's data is hidden
        checkViewIsVisible(R.id.tag_information_textview, "Tag information")

        // Check that the floating action button for saving tag is hidden
        checkViewIsVisible(R.id.save_fab, "Save FAB")
    }

    @Test
    fun checkUiElementsWhenDisplayingInvalidTag() {
        logTestName()

        // Load fragment in empty fragment activity and force state `DisplayTag`
        launchFragmentInHiltContainer<ScanFragment> { fragment ->
            val tag = Tag()
            fragment.stateRender(ScanViewState.DisplayTag(tag, false))
        }

        // Check that loader is visible
        checkViewIsNotVisible(R.id.progress_bar, "Loader")

        // Check that the textview for tag's data is hidden
        checkViewIsVisible(R.id.tag_information_textview, "Tag information")

        // Check that the floating action button for saving tag is hidden
        checkViewIsNotVisible(R.id.save_fab, "Save FAB")
    }

    @Ignore("It seems there are some issues with Toast assertion with API 30 and upper -> https://github.com/android/android-test/issues/803")
    @Test
    fun checkUiElementsWhenTagSavedSuccessfully() {
        logTestName()

        var parentActivity: FragmentActivity? = null

        // Load fragment in empty fragment activity and force state `DisplayTag`
        launchFragmentInHiltContainer<ScanFragment> { fragment ->
            fragment.eventRender(ScanViewEvent.SaveTagSuccess)
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
        launchFragmentInHiltContainer<ScanFragment> { fragment ->
            fragment.eventRender(ScanViewEvent.SaveTagFailure)
            parentActivity = activity
        }

        // Check that a toast with failure message is displayed
        checkToastWithTextIsVisible(parentActivity, R.string.save_tag_fail, "Toast of tag saving result")
    }
}