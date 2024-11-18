package com.kidor.vigik.ui.home

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.kidor.vigik.R
import com.kidor.vigik.extensions.onNodeWithText
import com.kidor.vigik.navigation.AppScreen
import com.kidor.vigik.utils.TestUtils.logTestName
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations

/**
 * Integration tests for Home screen.
 */
@RunWith(AndroidJUnit4::class)
class HomeViewTest {

    private lateinit var closeable: AutoCloseable

    @Mock
    private lateinit var navigateToCallback: (AppScreen) -> Unit

    @Before
    fun setUp() {
        closeable = MockitoAnnotations.openMocks(this)
    }

    @After
    fun tearDown() {
        closeable.close()
    }

    @ExperimentalTestApi
    @Test
    fun checkViewState() {
        logTestName()

        runComposeUiTest {
            setContent {
                HomeScreen(navigateTo = navigateToCallback)
            }

            // Check that animations button is visible
            onNodeWithText(stringResourceId = R.string.animations_hub_title, ignoreCase = true)
                .assertIsDisplayed()

            // Check that biometric button is visible
            onNodeWithText(stringResourceId = R.string.biometric_title, ignoreCase = true)
                .assertIsDisplayed()

            // Check that bluetooth button is visible
            onNodeWithText(stringResourceId = R.string.bluetooth_title, ignoreCase = true)
                .assertIsDisplayed()

            // Check that emoji button is visible
            onNodeWithText(stringResourceId = R.string.emoji_title, ignoreCase = true)
                .assertIsDisplayed()

            // Check that NFC button is visible
            onNodeWithText(stringResourceId = R.string.nfc_check_title, ignoreCase = true)
                .assertIsDisplayed()

            // Check that notification button is visible
            onNodeWithText(stringResourceId = R.string.notification_title, ignoreCase = true)
                .assertIsDisplayed()

            // Check that REST API button is visible
            onNodeWithText(stringResourceId = R.string.rest_api_title, ignoreCase = true)
                .assertIsDisplayed()

            // Check that a click on "Animations" button triggers the associated callback
            onNodeWithText(stringResourceId = R.string.animations_hub_title, ignoreCase = true)
                .performClick()
            verify(navigateToCallback).invoke(AppScreen.AnimationsHubScreen)

            // Check that a click on "Biometric" button triggers the associated callback
            onNodeWithText(stringResourceId = R.string.biometric_title, ignoreCase = true)
                .performClick()
            verify(navigateToCallback).invoke(AppScreen.BiometricLoginScreen)

            // Check that a click on "Bluetooth" button triggers the associated callback
            onNodeWithText(stringResourceId = R.string.bluetooth_title, ignoreCase = true)
                .performClick()
            verify(navigateToCallback).invoke(AppScreen.BluetoothScreen)

            // Check that a click on "Emoji" button triggers the associated callback
            onNodeWithText(stringResourceId = R.string.emoji_title, ignoreCase = true)
                .performClick()
            verify(navigateToCallback).invoke(AppScreen.EmojiScreen)

            // Check that a click on "NFC" button triggers the associated callback
            onNodeWithText(stringResourceId = R.string.nfc_check_title, ignoreCase = true)
                .performClick()
            verify(navigateToCallback).invoke(AppScreen.NfcCheckScreen)

            // Check that a click on "Notification" button triggers the associated callback
            onNodeWithText(stringResourceId = R.string.notification_title, ignoreCase = true)
                .performClick()
            verify(navigateToCallback).invoke(AppScreen.NotificationScreen)

            // Check that a click on "REST API" button triggers the associated callback
            onNodeWithText(stringResourceId = R.string.rest_api_title, ignoreCase = true)
                .performClick()
            verify(navigateToCallback).invoke(AppScreen.RestApiScreen)
        }
    }
}
