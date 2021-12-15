package com.kidor.vigik.ui.emulate

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.kidor.vigik.R
import com.kidor.vigik.utils.EspressoUtils.checkViewIsVisible
import com.kidor.vigik.utils.TestUtils.logTestName
import com.kidor.vigik.extensions.launchFragmentInHiltContainer
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Integration tests for [EmulateFragment]
 */
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class EmulateViewTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

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