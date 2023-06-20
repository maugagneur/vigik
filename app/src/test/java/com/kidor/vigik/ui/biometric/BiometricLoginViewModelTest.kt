package com.kidor.vigik.ui.biometric

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.biometric.BiometricManager
import com.kidor.vigik.MainCoroutineRule
import com.kidor.vigik.ui.biometric.login.BiometricLoginViewAction
import com.kidor.vigik.ui.biometric.login.BiometricLoginViewModel
import com.kidor.vigik.ui.biometric.login.BiometricLoginViewState
import com.kidor.vigik.utils.AssertUtils.assertEquals
import com.kidor.vigik.utils.AssertUtils.assertFalse
import com.kidor.vigik.utils.AssertUtils.assertTrue
import com.kidor.vigik.utils.TestUtils.logTestName
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert.fail
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

    @InjectMockKs
    private lateinit var viewModel: BiometricLoginViewModel

    @MockK
    private lateinit var biometricManager: BiometricManager

    @Before
    fun setup() {
        MockKAnnotations.init(this)
    }

    @Test
    fun `test initial state`() {
        logTestName()

        val initialState = viewModel.viewState.value

        // Check that initial state is 'Login' with empty username and password
        if (initialState is BiometricLoginViewState.Login) {
            assertEquals("", initialState.usernameField, "Username")
            assertEquals("", initialState.passwordField, "Password")
            assertFalse(initialState.displayLoginFail, "Display login fail")
        } else {
            fail()
        }
    }

    @Test
    fun `test username and password fields update`() {
        logTestName()

        // Update username -> UI should display new username value with previous password value
        var usernameValue = "+35+ U53RN4M3"
        var passwordValue = ""
        viewModel.handleAction(BiometricLoginViewAction.UpdateUsername(usernameValue))

        var state = viewModel.viewState.value
        if (state is BiometricLoginViewState.Login) {
            assertEquals(usernameValue, state.usernameField, "Username")
            assertEquals(passwordValue, state.passwordField, "Password")
        } else {
            fail()
        }

        // Update password -> UI should display new password value with previous username value
        passwordValue = "azerty123"
        viewModel.handleAction(BiometricLoginViewAction.UpdatePassword(passwordValue))

        state = viewModel.viewState.value
        if (state is BiometricLoginViewState.Login) {
            assertEquals(usernameValue, state.usernameField, "Username")
            assertEquals(passwordValue, state.passwordField, "Password")
        } else {
            fail()
        }

        // Update username -> UI should display new username value with previous password value
        usernameValue = ""
        viewModel.handleAction(BiometricLoginViewAction.UpdateUsername(usernameValue))

        state = viewModel.viewState.value
        if (state is BiometricLoginViewState.Login) {
            assertEquals(usernameValue, state.usernameField, "Username")
            assertEquals(passwordValue, state.passwordField, "Password")
        } else {
            fail()
        }
    }

    @Test
    fun `test login`() {
        logTestName()

        var state = viewModel.viewState.value

        // Check that initial state is 'Login' with empty username and password
        if (state is BiometricLoginViewState.Login) {
            assertEquals("", state.usernameField, "Username")
            assertEquals("", state.passwordField, "Password")
            assertFalse(state.displayLoginFail, "Display login fail")
        } else {
            fail()
        }

        // Username and password are empty -> Log in fail
        viewModel.handleAction(BiometricLoginViewAction.Login)
        state = viewModel.viewState.value
        assertTrue(state is BiometricLoginViewState.Login, "Current state is Login")

        // Username is empty -> Log in fail
        viewModel.handleAction(BiometricLoginViewAction.UpdateUsername(""))
        viewModel.handleAction(BiometricLoginViewAction.UpdatePassword("42"))
        viewModel.handleAction(BiometricLoginViewAction.Login)
        state = viewModel.viewState.value
        assertTrue(state is BiometricLoginViewState.Login, "Current state is Login")
        assertTrue((state as BiometricLoginViewState.Login).displayLoginFail, "Display login fail")

        // Username is blank -> Log in fail
        viewModel.handleAction(BiometricLoginViewAction.UpdateUsername(" "))
        viewModel.handleAction(BiometricLoginViewAction.UpdatePassword("42"))
        viewModel.handleAction(BiometricLoginViewAction.Login)
        state = viewModel.viewState.value
        assertTrue(state is BiometricLoginViewState.Login, "Current state is Login")
        assertTrue((state as BiometricLoginViewState.Login).displayLoginFail, "Display login fail")

        // Password is empty -> Log in fail
        viewModel.handleAction(BiometricLoginViewAction.UpdateUsername("Bob"))
        viewModel.handleAction(BiometricLoginViewAction.UpdatePassword(""))
        viewModel.handleAction(BiometricLoginViewAction.Login)
        state = viewModel.viewState.value
        assertTrue(state is BiometricLoginViewState.Login, "Current state is Login")
        assertTrue((state as BiometricLoginViewState.Login).displayLoginFail, "Display login fail")

        // Password is blank -> Log in fail
        viewModel.handleAction(BiometricLoginViewAction.UpdateUsername("Bob"))
        viewModel.handleAction(BiometricLoginViewAction.UpdatePassword(" "))
        viewModel.handleAction(BiometricLoginViewAction.Login)
        state = viewModel.viewState.value
        assertTrue(state is BiometricLoginViewState.Login, "Current state is Login")
        assertTrue((state as BiometricLoginViewState.Login).displayLoginFail, "Display login fail")

        // Username and password are not blank -> Log in success
        viewModel.handleAction(BiometricLoginViewAction.UpdateUsername("Bob"))
        viewModel.handleAction(BiometricLoginViewAction.UpdatePassword("42"))
        viewModel.handleAction(BiometricLoginViewAction.Login)
        state = viewModel.viewState.value
        assertTrue(state is BiometricLoginViewState.Logged, "Current state is Logged")
    }

    @Test
    fun `test logout`() {
        // Set username and password to log in
        viewModel.handleAction(BiometricLoginViewAction.UpdateUsername("L33T"))
        viewModel.handleAction(BiometricLoginViewAction.UpdatePassword("GG EZ"))
        viewModel.handleAction(BiometricLoginViewAction.Login)
        var state = viewModel.viewState.value
        assertTrue(state is BiometricLoginViewState.Logged, "Current state is Logged")

        // Simulate a click on "logout" button
        viewModel.handleAction(BiometricLoginViewAction.Logout)
        state = viewModel.viewState.value

        // Check that state is 'Login' with empty username and password
        if (state is BiometricLoginViewState.Login) {
            assertEquals("", state.usernameField, "Username")
            assertEquals("", state.passwordField, "Password")
            assertFalse(state.displayLoginFail, "Display login fail")
        } else {
            fail()
        }
    }
}
