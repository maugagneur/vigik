package com.kidor.vigik.ui

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runAndroidComposeUiTest
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.kidor.vigik.HiltTestActivity
import com.kidor.vigik.R
import com.kidor.vigik.extensions.onNodeWithText
import com.kidor.vigik.ui.compose.AppNavHost
import com.kidor.vigik.ui.compose.AppScreen
import com.kidor.vigik.utils.AssertUtils.assertEquals
import com.kidor.vigik.utils.TestUtils.logTestName
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Integration tests for the [MainActivity].
 */
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class MainViewTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @ExperimentalMaterial3Api
    @ExperimentalTestApi
    @Test
    fun checkActionBarUiElements() {
        logTestName()

        runAndroidComposeUiTest(HiltTestActivity::class.java) {
            setContent {
                MainComposable()
            }

            // Check that action bar is displayed
            onNodeWithTag(ACTION_BAR_TEST_TAG)
                .assertIsDisplayed()
            // Check that the "stop scan" action menu is not displayed
            onNodeWithTag(ACTION_MENU_STOP_SCAN)
                .assertDoesNotExist()
            // Check that the "invert colors" action menu is not displayed
            onNodeWithTag(ACTION_MENU_INVERT_COLORS)
                .assertIsDisplayed()
        }
    }

    @ExperimentalTestApi
    @Ignore("WIP")
    @Test
    fun checkNavigation() {
        logTestName()

        var navController: NavHostController? = null

        runAndroidComposeUiTest(HiltTestActivity::class.java) {
            setContent {
                navController = rememberNavController()
                AppNavHost(context = LocalContext.current, navController = navController!!)
            }

            Thread.sleep(3000)

            // TODO: Check navigation. Maybe do not "click" on UI elements but call navigate() on nav controller directly then check the destination

            // Perform click on the scan button
            onNodeWithText(stringResourceId = R.string.emulate_button_label, ignoreCase = true)
                .performClick()

            Thread.sleep(3000)
        }

        // Check that the navigation controller moves to the emulate view
        assertEquals(AppScreen.EmulateScreen.route, navController?.currentDestination?.route, "Destination route")
    }
}
