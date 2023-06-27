package com.kidor.vigik.ui.biometric.home

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.kidor.vigik.MainCoroutineRule
import com.kidor.vigik.data.biometric.BiometricRepository
import com.kidor.vigik.data.biometric.model.BiometricInfo
import com.kidor.vigik.data.user.UserRepository
import com.kidor.vigik.utils.AssertUtils.assertFalse
import com.kidor.vigik.utils.AssertUtils.assertTrue
import com.kidor.vigik.utils.TestUtils.logTestName
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Unit tests for [BiometricHomeViewModel].
 */
class BiometricHomeViewModelTest {

    @ExperimentalCoroutinesApi
    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: BiometricHomeViewModel

    @MockK
    private lateinit var userRepository: UserRepository
    private val isUserLoggedInFlow = MutableStateFlow(true)
    @MockK
    private lateinit var biometricRepository: BiometricRepository
    @MockK
    private lateinit var biometricInfo: BiometricInfo

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        every { userRepository.isUserLoggedIn } returns isUserLoggedInFlow
        coEvery { biometricRepository.getBiometricInfo() } returns biometricInfo
        every { biometricInfo.biometricTokenIsPresent } returns false
        viewModel = BiometricHomeViewModel(userRepository, biometricRepository)
    }

    @Test
    fun `test view state reflects if biometric credentials are saved or not`() {
        logTestName()

        // Simulate that biometric credentials are saved
        every { biometricInfo.biometricTokenIsPresent } returns true
        // Create view model
        viewModel = BiometricHomeViewModel(userRepository, biometricRepository)
        // Check that the view state indicates that credentials are saved
        var initialState = viewModel.viewState.value
        assertTrue(initialState?.isBiometricCredentialsSaved, "Is biometric credentials saved")

        // Simulate that biometric credentials are not saved
        every { biometricInfo.biometricTokenIsPresent } returns false
        // Create view model
        viewModel = BiometricHomeViewModel(userRepository, biometricRepository)
        // Check that the view state indicates that credentials are not saved
        initialState = viewModel.viewState.value
        assertFalse(initialState?.isBiometricCredentialsSaved, "Is biometric credentials saved")
    }

    @Test
    fun `test navigate to login screen when user is logged out`() {
        logTestName()

        runTest {
            viewModel.viewEvent.test {
                expectNoEvents()

                // Simulate user logout
                isUserLoggedInFlow.emit(false)

                // Check event received to navigate to login screen
                assertTrue(awaitItem() is BiometricHomeViewEvent.NavigateToBiometricLogin, "View event")

                cancelAndIgnoreRemainingEvents()
            }
        }
    }

    @Test
    fun `test remove credentials button behavior`() {
        logTestName()

        // Simulate a click on "remove credentials" button
        viewModel.handleAction(BiometricHomeViewAction.RemoveCredentials)

        // Check that we ask the biometric repository to remove tokens
        coVerify { biometricRepository.removeToken() }
    }

    @Test
    fun `test logout button behavior`() {
        logTestName()

        // Simulate a click on "logout" button
        viewModel.handleAction(BiometricHomeViewAction.Logout)

        // Check that we ask the user repository to logout
        coVerify { userRepository.logout() }
    }
}
