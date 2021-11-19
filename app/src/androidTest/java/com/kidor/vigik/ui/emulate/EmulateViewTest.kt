package com.kidor.vigik.ui.emulate

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.kidor.vigik.R
import com.kidor.vigik.emulate.EmulateFragment
import com.kidor.vigik.utils.EspressoUtils.checkViewIsVisible
import com.kidor.vigik.utils.TestUtils.logTestName
import com.kidor.vigik.utils.launchFragmentInHiltContainer
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Integration tests for [EmulateFragment]
 */
@RunWith(AndroidJUnit4::class)
class EmulateViewTest {

    @Before
    fun setUp() {
        // Load fragment in empty fragment activity
        launchFragmentInHiltContainer<EmulateFragment>()
    }

    @Test
    fun checkUiElements() {
        logTestName()

        // Check that the log textview is visible
        checkViewIsVisible(R.id.log_textview, "Log TextView")
    }
}