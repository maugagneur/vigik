package com.kidor.vigik.data.user

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.kidor.vigik.data.PreferencesKeys
import com.kidor.vigik.data.user.model.UserLoginError
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import timber.log.Timber
import java.util.UUID

/**
 * Implementation of [UserRepository].
 */
class UserRepositoryImp(
    private val preferences: DataStore<Preferences>
) : UserRepository {

    private val _isUserLoggedIn = MutableStateFlow(false)
    override val isUserLoggedIn: StateFlow<Boolean> get() = _isUserLoggedIn

    override suspend fun login(username: String, password: String): UserLoginError? {
        // For this application any username/password are valid.
        val isSuccess = username.isNotBlank() && password.isNotBlank()
        if (isSuccess) {
            preferences.edit { it[PreferencesKeys.USER_TOKEN] = fetchUserToken() }
        }
        _isUserLoggedIn.emit(isSuccess)
        return if (isSuccess) {
            null
        } else {
            UserLoginError.INVALID_USERNAME_PASSWORD
        }
    }

    override suspend fun loginWithToken(token: String): UserLoginError? {
        val userToken = getUserToken()
        val isSuccess = userToken == token
        _isUserLoggedIn.emit(isSuccess)
        return if (isSuccess) {
            null
        } else {
            UserLoginError.INVALID_USER_TOKEN
        }
    }

    override suspend fun getUserToken(): String? = preferences.data.firstOrNull()?.get(PreferencesKeys.USER_TOKEN)

    override suspend fun logout() {
        Timber.i("logout()")
        _isUserLoggedIn.emit(false)
    }

    /**
     * Returns a mocked user token as we do not have a real backend for authentication.
     */
    private fun fetchUserToken(): String = UUID.randomUUID().toString().also {
        Timber.d("User token generated -> $it")
    }
}
