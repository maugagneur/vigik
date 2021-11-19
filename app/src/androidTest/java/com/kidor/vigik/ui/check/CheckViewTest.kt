package com.kidor.vigik.ui.check

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.kidor.vigik.R
import com.kidor.vigik.check.CheckFragment
import com.kidor.vigik.utils.EspressoUtils.checkViewIsNotVisible
import com.kidor.vigik.utils.EspressoUtils.checkViewIsVisible
import com.kidor.vigik.utils.TestUtils.logTestName
import com.kidor.vigik.utils.launchFragmentInHiltContainer
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Integration tests for [CheckFragment].
 */
@RunWith(AndroidJUnit4::class)
class CheckViewTest {

    @Before
    fun setUp() {
        // Load fragment in empty fragment activity
        launchFragmentInHiltContainer<CheckFragment>()
    }

    @Test
    fun checkUiElements() {
        logTestName()

        // Check that loader is visible
        checkViewIsVisible(R.id.progress_bar, "Loader")

        // Check that refresh button is hidden
        checkViewIsNotVisible(R.id.nfc_refresh_button, "Refresh button")

        // Check that settings button is hidden
        checkViewIsNotVisible(R.id.nfc_settings_button, "Settings button")
    }
}