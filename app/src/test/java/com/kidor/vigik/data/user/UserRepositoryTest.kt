package com.kidor.vigik.data.user

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import app.cash.turbine.test
import com.kidor.vigik.data.PreferencesKeys
import com.kidor.vigik.data.user.model.UserLoginError
import com.kidor.vigik.utils.AssertUtils.assertEquals
import com.kidor.vigik.utils.AssertUtils.assertFalse
import com.kidor.vigik.utils.AssertUtils.assertNull
import com.kidor.vigik.utils.AssertUtils.assertTrue
import com.kidor.vigik.utils.TestUtils.logTestName
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for [UserRepositoryImp].
 */
class UserRepositoryTest {

    private lateinit var userRepository: UserRepository

    @MockK
    private lateinit var dataStore: DataStore<Preferences>
    @MockK
    private lateinit var preferences: Preferences

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        every { dataStore.data } returns flow { emit(preferences) }
        coEvery { dataStore.updateData(any()) } returns preferences
        userRepository = UserRepositoryImp(dataStore)
    }

    @Test
    fun `test login with username and password behavior`() {
        logTestName()

        runTest {
            userRepository.isUserLoggedIn.test {
                // User is not logged at start
                assertFalse(awaitItem(), "User logged at start")

                // Login with empty username and password
                var result = userRepository.login("", "")

                // Check that an error is received
                assertEquals(UserLoginError.INVALID_USERNAME_PASSWORD, result, "Login error")
                // Check that user is still not logged
                expectNoEvents()

                // Login with empty password
                result = userRepository.login("Username", "")

                // Check that an error is received
                assertEquals(UserLoginError.INVALID_USERNAME_PASSWORD, result, "Login error")
                // Check that user is still not logged
                expectNoEvents()

                // Login with empty username
                result = userRepository.login("", "Password")

                // Check that an error is received
                assertEquals(UserLoginError.INVALID_USERNAME_PASSWORD, result, "Login error")
                // Check that user is still not logged
                expectNoEvents()

                // Login with valid username and password
                result = userRepository.login("Valid username", "Valid password")

                // Check that no error is received
                assertNull(result, "Login error")
                // Check that user is logged
                assertTrue(awaitItem(), "Is user logged in")

                cancelAndIgnoreRemainingEvents()
            }
        }
    }

    @Test
    fun `test login with token behavior`() {
        logTestName()

        runTest {
            userRepository.isUserLoggedIn.test {
                // User is not logged at start
                assertFalse(awaitItem(), "User logged at start")

                // Login with wrong user token
                every { preferences[PreferencesKeys.USER_TOKEN] } returns "valid-user-token"
                var result = userRepository.loginWithToken("wrong-user-token")

                // Check that an error is received
                assertEquals(UserLoginError.INVALID_USER_TOKEN, result, "Login error")
                // Check that user is still not logged
                expectNoEvents()

                // Login with good user token
                every { preferences[PreferencesKeys.USER_TOKEN] } returns "good-user-token"
                result = userRepository.loginWithToken("good-user-token")

                // Check that no error is received
                assertNull(result, "Login error")
                // Check that user is logged
                assertTrue(awaitItem(), "Is user logged in")

                cancelAndIgnoreRemainingEvents()
            }
        }
    }

    @Test
    fun `test user token is retrieved from persistent storage`() {
        logTestName()

        val userToken = "test-user-token"
        every { preferences[PreferencesKeys.USER_TOKEN] } returns userToken

        runTest {
            assertEquals(userToken, userRepository.getUserToken(), "User token")
        }
    }

    @Test
    fun `test logout behavior`() {
        logTestName()

        runTest {
            userRepository.isUserLoggedIn.test {
                // User is not logged at start
                assertFalse(awaitItem(), "User logged at start")

                // Logout
                userRepository.logout()

                // Check that user is still not logged
                expectNoEvents()

                // Login with valid username and password
                userRepository.login("Valid username", "Valid password")

                // Check that user is logged
                assertTrue(awaitItem(), "Is user logged in")

                // Logout
                userRepository.logout()

                // Check that user is logged out
                assertFalse(awaitItem(), "Is user logger in")

                cancelAndIgnoreRemainingEvents()
            }
        }
    }
}
