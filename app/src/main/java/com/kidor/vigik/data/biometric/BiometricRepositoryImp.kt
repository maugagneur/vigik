package com.kidor.vigik.data.biometric

import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.util.Base64
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricPrompt
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.kidor.vigik.R
import com.kidor.vigik.data.Localization
import com.kidor.vigik.data.PreferencesKeys
import com.kidor.vigik.data.biometric.model.BiometricAuthenticationStatus
import com.kidor.vigik.data.biometric.model.BiometricInfo
import com.kidor.vigik.data.crypto.CryptoApi
import com.kidor.vigik.data.crypto.model.CryptoAPIStatus
import com.kidor.vigik.data.crypto.model.CryptoPurpose
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import timber.log.Timber

/**
 * Implementation of [BiometricRepository].
 */
class BiometricRepositoryImp(
    private val biometricManager: BiometricManager,
    private val cryptoApi: CryptoApi,
    private val localization: Localization,
    private val preferences: DataStore<Preferences>,
    private val dispatcher: CoroutineDispatcher
) : BiometricRepository {

    override suspend fun getBiometricInfo(): BiometricInfo = withContext(dispatcher) {
        val biometricAuthenticationStatus =
            when (biometricManager.canAuthenticate(BIOMETRIC_STRONG)) {
                BiometricManager.BIOMETRIC_SUCCESS -> BiometricAuthenticationStatus.READY
                BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> BiometricAuthenticationStatus.NOT_AVAILABLE
                BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> BiometricAuthenticationStatus.TEMPORARY_NOT_AVAILABLE
                BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> BiometricAuthenticationStatus.AVAILABLE_BUT_NOT_ENROLLED
                else -> {
                    Timber.e("Error when checking biometric authentication features")
                    BiometricAuthenticationStatus.NOT_AVAILABLE
                }
            }

        val cryptoApiStatus = cryptoApi.cryptoApiStatus
        if (cryptoApiStatus == CryptoAPIStatus.INVALIDATED && isTokenPresent()) {
            // In case previous secret was invalidated, remove biometric credentials from persistent storage
            preferences.edit {
                it.remove(PreferencesKeys.BIOMETRIC_TOKEN)
                it.remove(PreferencesKeys.BIOMETRIC_IV)
            }
        }

        BiometricInfo(
            biometricTokenIsPresent = isTokenPresent(),
            biometricAuthenticationStatus = biometricAuthenticationStatus,
            cryptoAPIStatus = cryptoApiStatus
        )
    }

    /**
     * Checks persistent storage to know if it contains biometric credentials previously memorized.
     */
    private suspend fun isTokenPresent(): Boolean {
        return preferences.data.firstOrNull()?.let { preferences ->
            preferences.contains(PreferencesKeys.BIOMETRIC_TOKEN) && preferences.contains(PreferencesKeys.BIOMETRIC_IV)
        } ?: false
    }

    @Suppress("DEPRECATION")
    override fun getBiometricEnrollIntent(): Intent = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> Intent(Settings.ACTION_BIOMETRIC_ENROLL)
            .putExtra(Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED, BIOMETRIC_STRONG)

        Build.VERSION.SDK_INT >= Build.VERSION_CODES.P -> Intent(Settings.ACTION_FINGERPRINT_ENROLL)

        else -> Intent(Settings.ACTION_SETTINGS)
    }

    override fun getBiometricPromptInfo(purpose: CryptoPurpose): BiometricPrompt.PromptInfo {
        val builder = BiometricPrompt.PromptInfo.Builder()
        when (purpose) {
            CryptoPurpose.ENCRYPTION -> builder
                .setTitle(localization.getString(R.string.biometric_prompt_enroll_title))
                .setSubtitle(localization.getString(R.string.biometric_prompt_enroll_subtitle))

            CryptoPurpose.DECRYPTION -> builder
                .setTitle(localization.getString(R.string.biometric_prompt_login_title))
                .setSubtitle(localization.getString(R.string.biometric_prompt_login_subtitle))
        }
        return builder
            .setNegativeButtonText(localization.getString(R.string.biometric_prompt_negative_button_label))
            .setAllowedAuthenticators(BIOMETRIC_STRONG)
            .build()
    }

    override suspend fun getCryptoObject(purpose: CryptoPurpose): BiometricPrompt.CryptoObject? {
        // Check crypto API
        if (cryptoApi.cryptoApiStatus != CryptoAPIStatus.READY) {
            Timber.w("Calling getCryptoObjectForEncryption() when crypto API is not ready")
            return null
        }

        return when (purpose) {
            CryptoPurpose.ENCRYPTION -> {
                cryptoApi.getCryptoObjectForEncryption()
            }
            CryptoPurpose.DECRYPTION -> {
                // Get IV from persistent storage
                val encodedInitializationVector = preferences.data.firstOrNull()?.get(PreferencesKeys.BIOMETRIC_IV)
                if (encodedInitializationVector == null) {
                    Timber.e("Trying to get crypto object for decryption with no IV saved")
                    return null
                }
                // Decode IV
                val initializationVector = Base64.decode(encodedInitializationVector, Base64.DEFAULT)
                return cryptoApi.getCryptoObjectForDecryption(initializationVector)
            }
        }
    }

    override suspend fun encryptAndStoreToken(token: String, cryptoObject: BiometricPrompt.CryptoObject) {
        // Check crypto API
        if (cryptoApi.cryptoApiStatus != CryptoAPIStatus.READY) {
            Timber.w("Calling encryptAndStoreToken() when crypto API is not ready")
            return
        }

        // Encrypt token using the cipher inside the given crypto object
        val cipher = cryptoObject.cipher
        if (cipher == null) {
            Timber.e("Cipher associated with given crypto object is null")
            return
        }
        val encryptedToken = cryptoApi.encryptData(token, cipher)

        // Store encrypted token's data and IV
        val encryptedTokenDataBase64 = Base64.encodeToString(encryptedToken.data, Base64.DEFAULT)
        val encryptedTokenIVBase64 = Base64.encodeToString(encryptedToken.initializationVector, Base64.DEFAULT)
        preferences.edit {
            it[PreferencesKeys.BIOMETRIC_TOKEN] = encryptedTokenDataBase64
            it[PreferencesKeys.BIOMETRIC_IV] = encryptedTokenIVBase64
        }
    }

    override suspend fun decryptToken(cryptoObject: BiometricPrompt.CryptoObject): String? {
        // Check crypto API
        if (cryptoApi.cryptoApiStatus != CryptoAPIStatus.READY) {
            Timber.w("Calling decryptToken() when crypto API is not ready")
            return null
        }

        // Decode token from persistent storage
        val cipher = cryptoObject.cipher
        if (cipher == null) {
            Timber.e("Cipher associated with given crypto object is null")
            return null
        }
        val encodedTokenData = preferences.data.firstOrNull()?.get(PreferencesKeys.BIOMETRIC_TOKEN)
        val tokenData = Base64.decode(encodedTokenData, Base64.DEFAULT)
        return cryptoApi.decryptData(tokenData, cipher)
    }

    override suspend fun removeToken() {
        preferences.edit {
            it.remove(PreferencesKeys.BIOMETRIC_TOKEN)
            it.remove(PreferencesKeys.BIOMETRIC_IV)
        }
    }
}
