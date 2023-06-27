package com.kidor.vigik.data.user

import com.kidor.vigik.data.user.model.UserLoginError
import kotlinx.coroutines.flow.StateFlow

/**
 * Interface of a user repository.
 */
interface UserRepository {

    /**
     * Flow that indicates if the user is logged in or not.
     */
    val isUserLoggedIn: StateFlow<Boolean>

    /**
     * Login with a username and a password.
     * You should observe [isUserLoggedIn] flow to know if login succeeded.
     *
     * @param username The username.
     * @param password The password.
     * @return The login error if happens, otherwise null.
     */
    suspend fun login(username: String, password: String): UserLoginError?

    /**
     * Login with a token.
     * You should observe [isUserLoggedIn] flow to know if login succeeded.
     *
     * @param token The token.
     * @return The login error if happens, otherwise null.
     */
    suspend fun loginWithToken(token: String): UserLoginError?

    /**
     * Returns the user's token if logged, otherwise null.
     */
    suspend fun getUserToken(): String?

    /**
     * Logout current user.
     * You should observe [isUserLoggedIn] flow to know if logout succeeded.
     */
    suspend fun logout()
}
