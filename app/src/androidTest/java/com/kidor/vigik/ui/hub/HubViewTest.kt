package com.kidor.vigik.ui.hub

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.kidor.vigik.R
import com.kidor.vigik.hub.HubFragment
import com.kidor.vigik.utils.AssertUtils.assertEquals
import com.kidor.vigik.utils.EspressoUtils.checkViewIsVisibleWithText
import com.kidor.vigik.utils.EspressoUtils.performClickOnView
import com.kidor.vigik.utils.TestUtils.logTestName
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Integration tests for [HubFragment].
 */
@RunWith(AndroidJUnit4::class)
class HubViewTest {

    @Test
    fun checkUiElements() {
        logTestName()

        // Launch Hub fragment
        launchFragment()

        // Check that button to start scanning tag is visible
        checkViewIsVisibleWithText(R.id.scan_button, R.string.scan_button_label, "Scan button")

        // Check that button to start emulating tag is visible
        checkViewIsVisibleWithText(R.id.emulate_button, R.string.emulate_button_label, "Emulate button")
    }

    @Test
    fun navigateToScanView() {
        logTestName()

        // Launch Hub fragment
        val testNavHostController = launchFragment()

        // Perform click on the scan button
        performClickOnView(R.id.scan_button, "Click on Scan button")

        // Check that the navigation controller moves to the scan view
        assertEquals(R.id.scanFragment, testNavHostController.currentDestination?.id, "Destination ID")
    }

    @Test
    fun navigateToEmulateView() {
        logTestName()

        // Launch Hub fragment
        val testNavHostController = launchFragment()

        // Perform click on the scan button
        performClickOnView(R.id.emulate_button, "Click on Emulate button")

        // Check that the navigation controller moves to the emulate view
        assertEquals(R.id.emulateFragment, testNavHostController.currentDestination?.id, "Destination ID")
    }

    /**
     * Launches an instance of [HubFragment] in an empty activity.
     *
     * @return The [TestNavHostController] bind to the fragment's container.
     */
    private fun launchFragment(): TestNavHostController =
        TestNavHostController(ApplicationProvider.getApplicationContext()).also { navController ->
            launchFragmentInContainer<HubFragment>().run {
                onFragment { fragment ->
                    // Initialize navigation graph and navigate to Hub fragment
                    navController.setGraph(R.navigation.nav_graph)
                    navController.setCurrentDestination(R.id.hubFragment)
                    Navigation.setViewNavController(fragment.requireView(), navController)
                }
            }
        }
}