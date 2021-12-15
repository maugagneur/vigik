package com.kidor.vigik.ui.check

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.kidor.vigik.R
import com.kidor.vigik.utils.EspressoUtils.checkViewIsNotVisible
import com.kidor.vigik.utils.EspressoUtils.checkViewIsVisible
import com.kidor.vigik.utils.TestUtils.logTestName
import com.kidor.vigik.extensions.launchFragmentInHiltContainer
import com.kidor.vigik.utils.AssertUtils.assertEquals
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Integration tests for [CheckFragment].
 */
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class CheckViewTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Test
    fun checkUiElementsAtStart() {
        logTestName()

        // Load fragment in empty fragment activity
        launchFragmentInHiltContainer<CheckFragment>()

        // Check that loader is visible
        checkViewIsVisible(R.id.progress_bar, "Loader")

        // Check that refresh button is hidden
        checkViewIsNotVisible(R.id.nfc_refresh_button, "Refresh button")

        // Check that settings button is hidden
        checkViewIsNotVisible(R.id.nfc_settings_button, "Settings button")
    }

    @Test
    fun displayButtonsWhenNfcDisable() {
        logTestName()

        // Load fragment in empty fragment activity and force state `NfcIsDisable`
        launchFragmentInHiltContainer<CheckFragment> { fragment ->
            fragment.forceState(CheckViewState.NfcIsDisable)
        }

        // Check that loader is hidden
        checkViewIsNotVisible(R.id.progress_bar, "Loader")

        // Check that refresh button is visible
        checkViewIsVisible(R.id.nfc_refresh_button, "Refresh button")

        // Check that settings button is visible
        checkViewIsVisible(R.id.nfc_settings_button, "Settings button")
    }
}