package com.kidor.vigik.ui.scan

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.kidor.vigik.R
import com.kidor.vigik.utils.EspressoUtils.checkViewIsNotVisible
import com.kidor.vigik.utils.EspressoUtils.checkViewIsVisible
import com.kidor.vigik.utils.TestUtils.logTestName
import com.kidor.vigik.utils.launchFragmentInHiltContainer
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Integration tests for [ScanFragment].
 */
@RunWith(AndroidJUnit4::class)
class ScanViewTest {

    @Before
    fun setUp() {
        // Load fragment in empty fragment activity
        launchFragmentInHiltContainer<ScanFragment>()
    }

    @Test
    fun checkUiElements() {
        logTestName()

        // Check that loader is visible
        checkViewIsVisible(R.id.progress_bar, "Loader")

        // Check that the textview for tag's data is hidden
        checkViewIsNotVisible(R.id.tag_information_textview, "Tag information")
    }
}