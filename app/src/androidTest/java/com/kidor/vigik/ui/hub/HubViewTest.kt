package com.kidor.vigik.ui.hub

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.performClick
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.kidor.vigik.R
import com.kidor.vigik.extensions.onNodeWithText
import com.kidor.vigik.utils.AssertUtils.assertEquals
import com.kidor.vigik.utils.TestUtils.logTestName
import org.junit.After
import org.junit.Before
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations

/**
 * Integration tests for [HubFragment].
 */
@RunWith(AndroidJUnit4::class)
class HubViewTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var closeable: AutoCloseable

    @Mock
    private lateinit var viewActionCallback: (HubViewAction) -> Unit

    @Before
    fun setUp() {
        closeable = MockitoAnnotations.openMocks(this)
    }

    @After
    fun tearDown() {
        closeable.close()
    }

    @Test
    fun checkDefaultState() {
        logTestName()

        composeTestRule.setContent {
            DefaultState(DefaultStateData(viewActionCallback))
        }

        // Check that button to start scanning tag is visible
        composeTestRule
            .onNodeWithText(stringResourceId = R.string.scan_button_label, ignoreCase = true)
            .assertIsDisplayed()

        // Check that button to see the tag history is visible
        composeTestRule
            .onNodeWithText(stringResourceId = R.string.history_button_label, ignoreCase = true)
            .assertIsDisplayed()

        // Check that button to start emulating tag is visible
        composeTestRule
            .onNodeWithText(stringResourceId = R.string.emulate_button_label, ignoreCase = true)
            .assertIsDisplayed()

        // Check that a click on "NFC scan" button generates a RefreshNfcStatus action
        composeTestRule
            .onNodeWithText(stringResourceId = R.string.scan_button_label, ignoreCase = true)
            .performClick()
        verify(viewActionCallback).invoke(HubViewAction.DisplayScanTagView)

        // Check that a click on "Tags history" button generates a DisplayTagHistoryView action
        composeTestRule
            .onNodeWithText(stringResourceId = R.string.history_button_label, ignoreCase = true)
            .performClick()
        verify(viewActionCallback).invoke(HubViewAction.DisplayTagHistoryView)

        // Check that a click on "Emulate NFC tag" button generates a DisplayEmulateTagView action
        composeTestRule
            .onNodeWithText(stringResourceId = R.string.emulate_button_label, ignoreCase = true)
            .performClick()
        verify(viewActionCallback).invoke(HubViewAction.DisplayEmulateTagView)
    }

    @Ignore("Broken. Will be update when navigation will be rework with compose.")
    @Test
    fun navigateToScanView() {
        logTestName()

        // Launch Hub fragment
        val testNavHostController = launchFragment()

        // Perform click on the scan button
        composeTestRule
            .onNodeWithText(stringResourceId = R.string.scan_button_label, ignoreCase = true)
            .performClick()

        // Check that the navigation controller moves to the scan view
        assertEquals(R.id.scanFragment, testNavHostController.currentDestination?.id, "Destination ID")
    }

    @Ignore("Broken. Will be update when navigation will be rework with compose.")
    @Test
    fun navigateToEmulateView() {
        logTestName()

        // Launch Hub fragment
        val testNavHostController = launchFragment()

        // Perform click on the scan button
        composeTestRule
            .onNodeWithText(stringResourceId = R.string.emulate_button_label, ignoreCase = true)
            .performClick()

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
