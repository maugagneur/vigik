package com.kidor.vigik.data.user.model

/**
 * Type of error that can occur during login.
 */
enum class UserLoginError {
    /**
     * The username/password couple is invalid.
     */
    INVALID_USERNAME_PASSWORD,

    /**
     * The user token is invalid.
     */
    INVALID_USER_TOKEN
}
