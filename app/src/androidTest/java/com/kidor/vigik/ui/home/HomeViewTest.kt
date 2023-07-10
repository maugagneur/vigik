package com.kidor.vigik.ui.home

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.kidor.vigik.R
import com.kidor.vigik.extensions.onNodeWithText
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
    private lateinit var navigateToBiometricCallback: () -> Unit
    @Mock
    private lateinit var navigateToNfcCallback: () -> Unit
    @Mock
    private lateinit var navigateToRestApiCallback: () -> Unit

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
                HomeScreen(
                    navigateToBiometric = navigateToBiometricCallback,
                    navigateToNfc = navigateToNfcCallback,
                    navigateToRestApi = navigateToRestApiCallback
                )
            }

            // Check that biometric button is visible
            onNodeWithText(stringResourceId = R.string.home_biometric_button_label, ignoreCase = true)
                .assertIsDisplayed()

            // Check that NFC button is visible
            onNodeWithText(stringResourceId = R.string.home_nfc_button_label, ignoreCase = true)
                .assertIsDisplayed()

            // Check that REST API button is visible
            onNodeWithText(stringResourceId = R.string.home_rest_api_button_label, ignoreCase = true)
                .assertIsDisplayed()

            // Check that a click on "Biometric" button triggers the associated callback
            onNodeWithText(stringResourceId = R.string.home_biometric_button_label, ignoreCase = true)
                .performClick()
            verify(navigateToBiometricCallback).invoke()

            // Check that a click on "NFC" button triggers the associated callback
            onNodeWithText(stringResourceId = R.string.home_nfc_button_label, ignoreCase = true)
                .performClick()
            verify(navigateToNfcCallback).invoke()

            // Check that a click on "REST API" button triggers the associated callback
            onNodeWithText(stringResourceId = R.string.home_rest_api_button_label, ignoreCase = true)
                .performClick()
            verify(navigateToRestApiCallback).invoke()
        }
    }
}