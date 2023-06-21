package com.kidor.vigik.data.biometric

import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricPrompt
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.kidor.vigik.R
import com.kidor.vigik.data.Localization
import com.kidor.vigik.data.PreferencesKeys
import com.kidor.vigik.data.biometric.model.BiometricAuthenticationStatus
import com.kidor.vigik.data.biometric.model.BiometricInfo
import com.kidor.vigik.data.crypto.CryptoApi
import com.kidor.vigik.data.crypto.model.CryptoKeyStatus
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
        val isTokenPresent: Boolean = preferences.data.firstOrNull()?.let { preferences ->
            preferences.contains(PreferencesKeys.BIOMETRIC_TOKEN) && preferences.contains(PreferencesKeys.BIOMETRIC_IV)
        } ?: false

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

        BiometricInfo(
            biometricTokenIsPresent = isTokenPresent,
            biometricAuthenticationStatus = biometricAuthenticationStatus,
            cryptoKeyStatus = CryptoKeyStatus.READY // FIXME
        )
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

    override fun getBiometricAuthenticationCallback(): BiometricPrompt.AuthenticationCallback {
        return object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                when (errorCode) {
                    BiometricPrompt.ERROR_USER_CANCELED,
                    BiometricPrompt.ERROR_CANCELED,
                    BiometricPrompt.ERROR_NEGATIVE_BUTTON ->
                        Timber.e("Biometric operation cancelled by user interaction")
                    else ->
                        Timber.e("Biometric error -> Code: $errorCode, Message: $errString")
                }
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                Timber.e("User biometric rejected")
            }

            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                Timber.d("Biometric authentication succeeded")
            }
        }
    }

    override fun getCryptoObjectForEncryption(): BiometricPrompt.CryptoObject =
        cryptoApi.getCryptoObjectForEncryption()

    override fun getCryptoObjectForDecryption(initializationVector: ByteArray): BiometricPrompt.CryptoObject =
        cryptoApi.getCryptoObjectForDecryption(initializationVector)
}
