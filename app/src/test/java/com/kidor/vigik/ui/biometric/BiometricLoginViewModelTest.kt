package com.kidor.vigik.ui.biometric

import android.content.Intent
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.biometric.BiometricPrompt.CryptoObject
import androidx.biometric.BiometricPrompt.PromptInfo
import app.cash.turbine.test
import com.kidor.vigik.MainCoroutineRule
import com.kidor.vigik.data.biometric.BiometricRepository
import com.kidor.vigik.data.biometric.model.BiometricAuthenticationStatus
import com.kidor.vigik.data.biometric.model.BiometricInfo
import com.kidor.vigik.data.crypto.model.CryptoPurpose
import com.kidor.vigik.data.user.UserRepository
import com.kidor.vigik.data.user.model.UserLoginError
import com.kidor.vigik.ui.biometric.login.BiometricLoginViewAction
import com.kidor.vigik.ui.biometric.login.BiometricLoginViewEvent
import com.kidor.vigik.ui.biometric.login.BiometricLoginViewModel
import com.kidor.vigik.utils.AssertUtils.assertEquals
import com.kidor.vigik.utils.AssertUtils.assertTrue
import com.kidor.vigik.utils.TestUtils.logTestName
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Unit tests for [BiometricLoginViewModel].
 */
class BiometricLoginViewModelTest {

    @ExperimentalCoroutinesApi
    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: BiometricLoginViewModel

    @MockK
    private lateinit var userRepository: UserRepository
    private val isUserLoggedInFlow = MutableStateFlow(false)
    @MockK
    private lateinit var biometricRepository: BiometricRepository
    @MockK
    private lateinit var biometricInfo: BiometricInfo
    @MockK
    private lateinit var promptInfo: PromptInfo
    @MockK
    private lateinit var cryptoObject: CryptoObject

    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        every { userRepository.isUserLoggedIn } returns isUserLoggedInFlow
        coEvery { biometricRepository.getBiometricInfo() } returns biometricInfo
        every { biometricInfo.biometricTokenIsPresent } returns false
        every { biometricInfo.biometricAuthenticationStatus } returns BiometricAuthenticationStatus.READY
        every { biometricInfo.isAuthenticationAvailable() } returns true
        every { biometricRepository.getBiometricEnrollIntent() } returns Intent()
        viewModel = BiometricLoginViewModel(userRepository, biometricRepository)
    }

    @Test
    fun `test view initial state`() {
        logTestName()

        val initialState = viewModel.viewState.value

        // Check that initial state has empty username and password
        assertEquals("", initialState?.usernameField, "Username")
        assertEquals("", initialState?.passwordField, "Password")
        assertEquals(false, initialState?.displayLoginFail, "Display login fail")
    }

    @Test
    fun `test biometric prompt view initial state`() {
        logTestName()

        val initialState = viewModel.biometricPromptState.replayCache.firstOrNull()

        // Check that initial state has empty username and password
        assertEquals(null, initialState, "Biometric prompt state")
    }

    @Test
    fun `test username and password fields update`() {
        logTestName()

        // Update username -> UI should display new username value with previous password value
        var usernameValue = "+35+ U53RN4M3"
        var passwordValue = ""
        viewModel.handleAction(BiometricLoginViewAction.UpdateUsername(usernameValue))

        var state = viewModel.viewState.value
        assertEquals(usernameValue, state?.usernameField, "Username")
        assertEquals(passwordValue, state?.passwordField, "Password")

        // Update password -> UI should display new password value with previous username value
        passwordValue = "azerty123"
        viewModel.handleAction(BiometricLoginViewAction.UpdatePassword(passwordValue))

        state = viewModel.viewState.value
        assertEquals(usernameValue, state?.usernameField, "Username")
        assertEquals(passwordValue, state?.passwordField, "Password")

        // Update username -> UI should display new username value with previous password value
        usernameValue = ""
        viewModel.handleAction(BiometricLoginViewAction.UpdateUsername(usernameValue))

        state = viewModel.viewState.value
        assertEquals(usernameValue, state?.usernameField, "Username")
        assertEquals(passwordValue, state?.passwordField, "Password")
    }

    @Test
    fun `test username and password combinations login`() {
        logTestName()

        var state = viewModel.viewState.value

        // Check that initial state is 'Login' with empty username and password
        assertEquals("", state?.usernameField, "Username")
        assertEquals("", state?.passwordField, "Password")
        assertEquals(false, state?.displayLoginFail, "Display login fail")

        // Username and password are empty -> Log in fail
        viewModel.handleAction(BiometricLoginViewAction.Login)
        coVerify(inverse = true) { userRepository.login(any(), any()) }
        state = viewModel.viewState.value
        assertEquals(true, state?.displayLoginFail, "Display login fail")

        // Username is empty -> Log in fail
        viewModel.handleAction(BiometricLoginViewAction.UpdateUsername(""))
        viewModel.handleAction(BiometricLoginViewAction.UpdatePassword("42"))
        viewModel.handleAction(BiometricLoginViewAction.Login)
        coVerify(inverse = true) { userRepository.login(any(), any()) }
        state = viewModel.viewState.value
        assertEquals(true, state?.displayLoginFail, "Display login fail")

        // Username is blank -> Log in fail
        viewModel.handleAction(BiometricLoginViewAction.UpdateUsername(" "))
        viewModel.handleAction(BiometricLoginViewAction.UpdatePassword("42"))
        viewModel.handleAction(BiometricLoginViewAction.Login)
        coVerify(inverse = true) { userRepository.login(any(), any()) }
        state = viewModel.viewState.value
        assertEquals(true, state?.displayLoginFail, "Display login fail")

        // Password is empty -> Log in fail
        viewModel.handleAction(BiometricLoginViewAction.UpdateUsername("Bob"))
        viewModel.handleAction(BiometricLoginViewAction.UpdatePassword(""))
        viewModel.handleAction(BiometricLoginViewAction.Login)
        coVerify(inverse = true) { userRepository.login(any(), any()) }
        state = viewModel.viewState.value
        assertEquals(true, state?.displayLoginFail, "Display login fail")

        // Password is blank -> Log in fail
        viewModel.handleAction(BiometricLoginViewAction.UpdateUsername("Bob"))
        viewModel.handleAction(BiometricLoginViewAction.UpdatePassword(" "))
        viewModel.handleAction(BiometricLoginViewAction.Login)
        coVerify(inverse = true) { userRepository.login(any(), any()) }
        state = viewModel.viewState.value
        assertEquals(true, state?.displayLoginFail, "Display login fail")

        // Username and password are not blank -> Correct log in request to the repository
        coEvery { userRepository.login(any(), any()) } returns null
        viewModel.handleAction(BiometricLoginViewAction.UpdateUsername("Bob"))
        viewModel.handleAction(BiometricLoginViewAction.UpdatePassword("42"))
        viewModel.handleAction(BiometricLoginViewAction.Login)
        coVerify(exactly = 1) { userRepository.login("Bob", "42") }

        // Username and password are not blank -> Correct log in request to the repository but it returns an error
        coEvery { userRepository.login(any(), any()) } returns UserLoginError.INVALID_USERNAME_PASSWORD
        viewModel.handleAction(BiometricLoginViewAction.UpdateUsername("Bob"))
        viewModel.handleAction(BiometricLoginViewAction.UpdatePassword("42"))
        viewModel.handleAction(BiometricLoginViewAction.Login)
        coVerify(exactly = 2) { userRepository.login("Bob", "42") }
        state = viewModel.viewState.value
        assertEquals(true, state?.displayLoginFail, "Display login fail")
    }

    @Test
    fun `test display system biometric enrollment when not enrolled`() {
        logTestName()

        runTest {
            // Simulate biometric not enrolled
            every { biometricInfo.biometricAuthenticationStatus } returns BiometricAuthenticationStatus.AVAILABLE_BUT_NOT_ENROLLED

            viewModel.viewEvent.test {
                // Simulate user login
                isUserLoggedInFlow.emit(true)

                verify { biometricRepository.getBiometricEnrollIntent() }
                assertTrue(awaitItem() is BiometricLoginViewEvent.DisplayBiometricEnrollment, "View event")

                cancelAndIgnoreRemainingEvents()
            }
        }
    }

    @Test
    fun `test show biometric prompt when login`() {
        logTestName()

        runTest {
            // Simulate biometric ready
            every { biometricInfo.biometricAuthenticationStatus } returns BiometricAuthenticationStatus.READY
            // Mock prompt info for encryption
            every { biometricRepository.getBiometricPromptInfo(CryptoPurpose.ENCRYPTION) } returns promptInfo
            // Mock crypto object create for encryption
            coEvery { biometricRepository.getCryptoObject(CryptoPurpose.ENCRYPTION) } returns cryptoObject

            // Simulate user login
            isUserLoggedInFlow.emit(true)

            // Check that biometric prompt is shown for encryption
            var biometricPromptState = viewModel.biometricPromptState.replayCache.lastOrNull()
            assertEquals(true, biometricPromptState?.isVisible, "Is visible")
            assertEquals(promptInfo, biometricPromptState?.promptInfo, "Prompt info")
            assertEquals(cryptoObject, biometricPromptState?.cryptoObject, "Crypto object")
            assertEquals(CryptoPurpose.ENCRYPTION, biometricPromptState?.purpose, "Purpose")

            // Simulate "cancel" action on biometric prompt
            viewModel.handleAction(BiometricLoginViewAction.HideBiometricPrompt)

            // Check that biometric prompt is hidden
            biometricPromptState = viewModel.biometricPromptState.replayCache.lastOrNull()
            assertEquals(false, biometricPromptState?.isVisible, "Is visible")
        }
    }

    @Test
    fun `test show biometric prompt when asking for biometric login`() {
        logTestName()

        runTest {
            // Simulate biometric ready
            every { biometricInfo.biometricAuthenticationStatus } returns BiometricAuthenticationStatus.READY
            // Mock prompt info for encryption
            every { biometricRepository.getBiometricPromptInfo(CryptoPurpose.DECRYPTION) } returns promptInfo
            // Mock crypto object create for encryption
            coEvery { biometricRepository.getCryptoObject(CryptoPurpose.DECRYPTION) } returns cryptoObject

            // Simulate user login with biometric
            viewModel.handleAction(BiometricLoginViewAction.LoginWithBiometric)

            // Check that biometric prompt is shown for encryption
            var biometricPromptState = viewModel.biometricPromptState.replayCache.lastOrNull()
            assertEquals(true, biometricPromptState?.isVisible, "Is visible")
            assertEquals(promptInfo, biometricPromptState?.promptInfo, "Prompt info")
            assertEquals(cryptoObject, biometricPromptState?.cryptoObject, "Crypto object")
            assertEquals(CryptoPurpose.DECRYPTION, biometricPromptState?.purpose, "Purpose")

            // Simulate "cancel" action on biometric prompt
            viewModel.handleAction(BiometricLoginViewAction.HideBiometricPrompt)

            // Check that biometric prompt is hidden
            biometricPromptState = viewModel.biometricPromptState.replayCache.lastOrNull()
            assertEquals(false, biometricPromptState?.isVisible, "Is visible")
        }
    }

    @Test
    fun `test display home screen when login after saving biometric credentials`() {
        logTestName()

        runTest {
            // Simulate biometric credentials saved
            every { biometricInfo.biometricTokenIsPresent } returns true

            viewModel.viewEvent.test {
                // Simulate user login
                isUserLoggedInFlow.emit(true)

                assertEquals(BiometricLoginViewEvent.NavigateToBiometricHome, awaitItem(), "View event")

                cancelAndIgnoreRemainingEvents()
            }
        }
    }

    @Test
    fun `test biometric authentication success action`() {
        logTestName()

        val userToken = "test-user-ID"
        coEvery { userRepository.getUserToken() } returns userToken
        coEvery { biometricRepository.decryptToken(cryptoObject) } returns userToken

        runTest {
            viewModel.viewEvent.test {
                // Simulate user successfully saving her biometric credentials
                viewModel.handleAction(BiometricLoginViewAction.OnBiometricAuthSuccess(
                    cryptoObject = cryptoObject,
                    purpose = CryptoPurpose.ENCRYPTION)
                )

                // Check that the user token is encrypted
                coVerify { biometricRepository.encryptAndStoreToken(userToken, cryptoObject) }
                // Check that the login still works and user is redirected to home screen
                assertTrue(awaitItem() is BiometricLoginViewEvent.NavigateToBiometricHome, "View event")

                // Simulate user successfully login with biometric
                coEvery { userRepository.loginWithToken(userToken) } returns null
                viewModel.handleAction(BiometricLoginViewAction.OnBiometricAuthSuccess(
                    cryptoObject = cryptoObject,
                    purpose = CryptoPurpose.DECRYPTION)
                )

                //Check that the user token is retrieved from repository and used for login
                coVerify { biometricRepository.decryptToken(cryptoObject) }
                coVerify { userRepository.loginWithToken(userToken) }

                // Simulate user successfully login with biometric but encrypted token does not match
                coEvery { userRepository.loginWithToken(userToken) } returns UserLoginError.INVALID_USER_TOKEN
                viewModel.handleAction(BiometricLoginViewAction.OnBiometricAuthSuccess(
                    cryptoObject = cryptoObject,
                    purpose = CryptoPurpose.DECRYPTION)
                )

                //Check that the user token is retrieved from repository and used for login
                coVerify { biometricRepository.decryptToken(cryptoObject) }
                coVerify { userRepository.loginWithToken(userToken) }
                // Check that an error message is displayed
                val viewState = viewModel.viewState.value
                assertEquals(true, viewState?.displayLoginFail, "Display login fail")

                // Check that no event is send
                expectNoEvents()

                cancelAndIgnoreRemainingEvents()
            }
        }
    }

    @Test
    fun `test biometric authentication error action`() {
        logTestName()

        runTest {
            viewModel.viewEvent.test {
                // Simulate user cancel saving her biometric credentials
                viewModel.handleAction(BiometricLoginViewAction.OnBiometricAuthError(purpose = CryptoPurpose.ENCRYPTION))

                // Check that the login still works and user is redirected to home screen
                assertTrue(awaitItem() is BiometricLoginViewEvent.NavigateToBiometricHome, "View event")

                // Simulate user cancel biometric login
                viewModel.handleAction(BiometricLoginViewAction.OnBiometricAuthError(purpose = CryptoPurpose.DECRYPTION))

                // Check that no event is send
                expectNoEvents()

                cancelAndIgnoreRemainingEvents()
            }
        }
    }
}
