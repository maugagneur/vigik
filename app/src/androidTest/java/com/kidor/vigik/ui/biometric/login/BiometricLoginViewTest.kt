package com.kidor.vigik.ui.biometric.login

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.runComposeUiTest
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.kidor.vigik.R
import com.kidor.vigik.extensions.assertTextContains
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
 * Integration tests for biometric login screen.
 */
@RunWith(AndroidJUnit4::class)
class BiometricLoginViewTest {

    private lateinit var closeable: AutoCloseable

    @Mock
    private lateinit var updateUsernameCallback: (String) -> Unit
    @Mock
    private lateinit var updatePasswordCallback: (String) -> Unit
    @Mock
    private lateinit var loginClickCallback: () -> Unit
    @Mock
    private lateinit var biometricLoginClickCallback: () -> Unit

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
    fun checkDefaultView() {
        logTestName()

        runComposeUiTest {
            setContent {
                LoginState(
                    loginStateData = LoginStateData(
                        onUpdateUsername = updateUsernameCallback,
                        onUpdatePassword = updatePasswordCallback,
                        loginState = BiometricLoginViewState(
                            usernameField = "",
                            passwordField = "",
                            isBiometricLoginAvailable = false,
                            displayLoginFail = false
                        ),
                        onLoginClick = loginClickCallback,
                        onBiometricLoginClick = biometricLoginClickCallback
                    )
                )
            }

            // Check visibility
            onNodeWithTag(USERNAME_TEXT_FIELD_TEST_TAG)
                .assertIsDisplayed()
                .assertTextContains(R.string.biometric_username_hint)
            onNodeWithTag(PASSWORD_TEXT_FIELD_TEST_TAG)
                .assertIsDisplayed()
                .assertTextContains(R.string.biometric_password_hint)
            onNodeWithText(stringResourceId = R.string.biometric_login_button_label, ignoreCase = true).assertIsDisplayed()
            onNodeWithText(stringResourceId = R.string.biometric_login_with_biometric_button_label, ignoreCase = true).assertDoesNotExist()
            onNodeWithText(stringResourceId = R.string.biometric_login_error_label, ignoreCase = true).assertDoesNotExist()

            // Check click interactions
            onNodeWithText(stringResourceId = R.string.biometric_login_button_label, ignoreCase = true).performClick()
            verify(loginClickCallback).invoke()

            // Check text field interactions
            val username = "Jean-Michel"
            onNodeWithTag(USERNAME_TEXT_FIELD_TEST_TAG).performTextInput(username)
            verify(updateUsernameCallback).invoke(username)
            val password = "azerty123"
            onNodeWithTag(PASSWORD_TEXT_FIELD_TEST_TAG).performTextInput(password)
            verify(updatePasswordCallback).invoke(password)
        }
    }

    @ExperimentalTestApi
    @Test
    fun checkViewWhenLoginFailed() {
        logTestName()

        runComposeUiTest {
            setContent {
                LoginState(
                    loginStateData = LoginStateData(
                        onUpdateUsername = updateUsernameCallback,
                        onUpdatePassword = updatePasswordCallback,
                        loginState = BiometricLoginViewState(
                            usernameField = "Bad ID",
                            passwordField = "wrong password",
                            isBiometricLoginAvailable = false,
                            displayLoginFail = true
                        ),
                        onLoginClick = loginClickCallback,
                        onBiometricLoginClick = biometricLoginClickCallback
                    )
                )
            }

            // Check visibility
            onNodeWithTag(USERNAME_TEXT_FIELD_TEST_TAG)
                .assertIsDisplayed()
                .assertTextContains("Bad ID")
            onNodeWithTag(PASSWORD_TEXT_FIELD_TEST_TAG)
                .assertIsDisplayed()
                .assertTextContains("••••••••••••••")
            onNodeWithText(stringResourceId = R.string.biometric_login_button_label, ignoreCase = true).assertIsDisplayed()
            onNodeWithText(stringResourceId = R.string.biometric_login_with_biometric_button_label, ignoreCase = true).assertDoesNotExist()
            onNodeWithText(stringResourceId = R.string.biometric_login_error_label, ignoreCase = true).assertIsDisplayed()
        }
    }

    @ExperimentalTestApi
    @Test
    fun checkViewWhenBiometricLoginIsAvailable() {
        logTestName()

        runComposeUiTest {
            setContent {
                LoginState(
                    loginStateData = LoginStateData(
                        onUpdateUsername = updateUsernameCallback,
                        onUpdatePassword = updatePasswordCallback,
                        loginState = BiometricLoginViewState(
                            usernameField = "",
                            passwordField = "",
                            isBiometricLoginAvailable = true,
                            displayLoginFail = false
                        ),
                        onLoginClick = loginClickCallback,
                        onBiometricLoginClick = biometricLoginClickCallback
                    )
                )
            }

            // Check visibility
            onNodeWithTag(USERNAME_TEXT_FIELD_TEST_TAG).assertIsDisplayed()
            onNodeWithTag(PASSWORD_TEXT_FIELD_TEST_TAG).assertIsDisplayed()
            onNodeWithText(stringResourceId = R.string.biometric_login_button_label, ignoreCase = true).assertIsDisplayed()
            onNodeWithText(stringResourceId = R.string.biometric_login_with_biometric_button_label, ignoreCase = true).assertIsDisplayed()
            onNodeWithText(stringResourceId = R.string.biometric_login_error_label, ignoreCase = true).assertDoesNotExist()
        }
    }
}
