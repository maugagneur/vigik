package com.kidor.vigik.ui

import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.kidor.vigik.R
import com.kidor.vigik.utils.EspressoUtils.checkTextViewInParentIsVisibleWithText
import com.kidor.vigik.utils.TestUtils.logTestName
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Integration tests for the [MainActivity].
 */
@RunWith(AndroidJUnit4::class)
class MainViewTest {

    @get:Rule
    val activityScenarioRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun checkUiElements() {
        logTestName()

        // Title in action bar
        checkTextViewInParentIsVisibleWithText(R.id.toolbar, R.string.check_nfc_title, "Action bar title")
    }
}