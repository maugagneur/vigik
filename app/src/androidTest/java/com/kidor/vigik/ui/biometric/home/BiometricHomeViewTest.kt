package com.kidor.vigik.ui.biometric.home

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithTag
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
 * Integration tests for biometric home screen.
 */
@RunWith(AndroidJUnit4::class)
class BiometricHomeViewTest {

    private lateinit var closeable: AutoCloseable

    @Mock
    private lateinit var removeCredentialCallback: () -> Unit
    @Mock
    private lateinit var logoutCallback: () -> Unit

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
    fun checkViewWhenNoBiometricCredentialsAreSaved() {
        logTestName()

        runComposeUiTest {
            setContent {
                BiometricHome(
                    biometricHomeStateData = BiometricHomeStateData(
                        isBiometricCredentialSaved = false,
                        onRemoveCredentialClick = removeCredentialCallback,
                        onLogoutClick = logoutCallback
                    )
                )
            }

            // Check visibility
            onNodeWithText(stringResourceId = R.string.biometric_home_credential_status_label, ignoreCase = true).assertIsDisplayed()
            onNodeWithTag(BIOMETRIC_CREDENTIALS_STATUS_ICON_TEST_TAG).assertIsDisplayed()
            onNodeWithText(stringResourceId = R.string.biometric_home_credential_remove_button_label, ignoreCase = true).assertDoesNotExist()
            onNodeWithText(stringResourceId = R.string.biometric_home_login_success_label, ignoreCase = true).assertIsDisplayed()
            onNodeWithText(stringResourceId = R.string.biometric_home_logout_button_label, ignoreCase = true).assertIsDisplayed()

            // Check click interactions
            onNodeWithText(stringResourceId = R.string.biometric_home_logout_button_label, ignoreCase = true).performClick()
            verify(logoutCallback).invoke()
        }
    }

    @ExperimentalTestApi
    @Test
    fun checkViewWhenBiometricCredentialsAreSaved() {
        logTestName()

        runComposeUiTest {
            setContent {
                BiometricHome(
                    biometricHomeStateData = BiometricHomeStateData(
                        isBiometricCredentialSaved = true,
                        onRemoveCredentialClick = removeCredentialCallback,
                        onLogoutClick = logoutCallback
                    )
                )
            }

            // Check visibility
            onNodeWithText(stringResourceId = R.string.biometric_home_credential_status_label, ignoreCase = true).assertIsDisplayed()
            onNodeWithTag(BIOMETRIC_CREDENTIALS_STATUS_ICON_TEST_TAG).assertIsDisplayed()
            onNodeWithText(stringResourceId = R.string.biometric_home_credential_remove_button_label, ignoreCase = true).assertIsDisplayed()
            onNodeWithText(stringResourceId = R.string.biometric_home_login_success_label, ignoreCase = true).assertIsDisplayed()
            onNodeWithText(stringResourceId = R.string.biometric_home_logout_button_label, ignoreCase = true).assertIsDisplayed()

            // Check click interactions
            onNodeWithText(stringResourceId = R.string.biometric_home_credential_remove_button_label, ignoreCase = true).performClick()
            verify(removeCredentialCallback).invoke()
            onNodeWithText(stringResourceId = R.string.biometric_home_logout_button_label, ignoreCase = true).performClick()
            verify(logoutCallback).invoke()
        }
    }
}
