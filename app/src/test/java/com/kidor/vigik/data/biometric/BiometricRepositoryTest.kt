package com.kidor.vigik.data.biometric

import android.util.Base64
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt.CryptoObject
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.kidor.vigik.MainCoroutineRule
import com.kidor.vigik.R
import com.kidor.vigik.data.Localization
import com.kidor.vigik.data.PreferencesKeys
import com.kidor.vigik.data.biometric.model.BiometricAuthenticationStatus
import com.kidor.vigik.data.crypto.CryptoApi
import com.kidor.vigik.data.crypto.model.CipherDataWrapper
import com.kidor.vigik.data.crypto.model.CryptoAPIStatus
import com.kidor.vigik.data.crypto.model.CryptoPurpose
import com.kidor.vigik.utils.AssertUtils.assertEquals
import com.kidor.vigik.utils.AssertUtils.assertFalse
import com.kidor.vigik.utils.AssertUtils.assertNull
import com.kidor.vigik.utils.AssertUtils.assertTrue
import com.kidor.vigik.utils.TestUtils.logTestName
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockkStatic
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.crypto.Cipher

/**
 * Unit tests for [BiometricRepositoryImp].
 */
class BiometricRepositoryTest {

    @ExperimentalCoroutinesApi
    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    private lateinit var biometricRepository: BiometricRepository

    @MockK
    private lateinit var biometricManager: BiometricManager
    @MockK
    private lateinit var cryptoApi: CryptoApi
    @MockK
    private lateinit var cryptoObject: CryptoObject
    @MockK
    private lateinit var cipher: Cipher
    @MockK
    private lateinit var localization: Localization
    @MockK
    private lateinit var dataStore: DataStore<Preferences>
    @MockK
    private lateinit var preferences: Preferences

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        every { dataStore.data } returns flow { emit(preferences) }
        coEvery { dataStore.updateData(any()) } returns preferences
        biometricRepository = BiometricRepositoryImp(
            biometricManager = biometricManager,
            cryptoApi = cryptoApi,
            localization = localization,
            preferences = dataStore,
            dispatcher = mainCoroutineRule.testDispatcher
        )
    }

    @Test
    fun `test getBiometricInfo() behavior when everything is OK`() {
        logTestName()

        runTest {
            // Simulate biometric authentication available, ready and with credentials already saved
            every { biometricManager.canAuthenticate(any()) } returns BiometricManager.BIOMETRIC_SUCCESS
            every { cryptoApi.cryptoApiStatus } returns CryptoAPIStatus.READY
            every { preferences.contains(PreferencesKeys.BIOMETRIC_TOKEN) } returns true
            every { preferences.contains(PreferencesKeys.BIOMETRIC_IV) } returns true

            // Get biometric info
            val result = biometricRepository.getBiometricInfo()

            // Check that biometric authentication is available and ready with credentials saved
            assertTrue(result.isAuthenticationAvailable(), "Is authentication available")
            assertEquals(BiometricAuthenticationStatus.READY, result.biometricAuthenticationStatus, "Biometric authentication status")
            assertTrue(result.biometricTokenIsPresent, "Biometric token is present")
        }
    }

    @Test
    fun `test getBiometricInfo() behavior when authentication status is NOK`() {
        logTestName()

        every { cryptoApi.cryptoApiStatus } returns CryptoAPIStatus.READY
        every { dataStore.data } returns emptyFlow()

        runTest {
            // Simulate biometric not enrolled
            every { biometricManager.canAuthenticate(any()) } returns BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED
            // Get biometric info
            var result = biometricRepository.getBiometricInfo()
            // Check that biometric authentication is not available because not enrolled yet
            assertFalse(result.isAuthenticationAvailable(), "Is authentication available")
            assertEquals(BiometricAuthenticationStatus.AVAILABLE_BUT_NOT_ENROLLED, result.biometricAuthenticationStatus, "Biometric authentication status")

            // Simulate biometric hardware not available for now
            every { biometricManager.canAuthenticate(any()) } returns BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE
            // Get biometric info
            result = biometricRepository.getBiometricInfo()
            // Check that biometric authentication is temporary not available
            assertFalse(result.isAuthenticationAvailable(), "Is authentication available")
            assertEquals(BiometricAuthenticationStatus.TEMPORARY_NOT_AVAILABLE, result.biometricAuthenticationStatus, "Biometric authentication status")

            // Simulate biometric hardware not available
            every { biometricManager.canAuthenticate(any()) } returns BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE
            // Get biometric info
            result = biometricRepository.getBiometricInfo()
            // Check that biometric authentication is not available
            assertFalse(result.isAuthenticationAvailable(), "Is authentication available")
            assertEquals(BiometricAuthenticationStatus.NOT_AVAILABLE, result.biometricAuthenticationStatus, "Biometric authentication status")

            // Simulate biometric not available because of security vulnerability
            every { biometricManager.canAuthenticate(any()) } returns BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED
            // Get biometric info
            result = biometricRepository.getBiometricInfo()
            // Check that biometric authentication is not available
            assertFalse(result.isAuthenticationAvailable(), "Is authentication available")
            assertEquals(BiometricAuthenticationStatus.NOT_AVAILABLE, result.biometricAuthenticationStatus, "Biometric authentication status")

            // Simulate biometric not compatible with the current Android version
            every { biometricManager.canAuthenticate(any()) } returns BiometricManager.BIOMETRIC_ERROR_UNSUPPORTED
            // Get biometric info
            result = biometricRepository.getBiometricInfo()
            // Check that biometric authentication is not available
            assertFalse(result.isAuthenticationAvailable(), "Is authentication available")
            assertEquals(BiometricAuthenticationStatus.NOT_AVAILABLE, result.biometricAuthenticationStatus, "Biometric authentication status")

            // Simulate biometric not compatible for unknown reason
            every { biometricManager.canAuthenticate(any()) } returns BiometricManager.BIOMETRIC_STATUS_UNKNOWN
            // Get biometric info
            result = biometricRepository.getBiometricInfo()
            // Check that biometric authentication is not available
            assertFalse(result.isAuthenticationAvailable(), "Is authentication available")
            assertEquals(BiometricAuthenticationStatus.NOT_AVAILABLE, result.biometricAuthenticationStatus, "Biometric authentication status")
        }
    }

    @Test
    fun `test getBiometricInfo() behavior when crypto API is not READY`() {
        logTestName()

        // Simulate biometric authentication available and with credentials already saved
        every { biometricManager.canAuthenticate(any()) } returns BiometricManager.BIOMETRIC_SUCCESS
        every { preferences.contains(PreferencesKeys.BIOMETRIC_TOKEN) } returns true
        every { preferences.contains(PreferencesKeys.BIOMETRIC_IV) } returns true

        runTest {
            // Simulate crypto API is not ready yet
            every { cryptoApi.cryptoApiStatus } returns CryptoAPIStatus.NOT_READY
            // Get biometric info
            var result = biometricRepository.getBiometricInfo()
            // Check that biometric authentication is not available
            assertFalse(result.isAuthenticationAvailable(), "Is authentication available")

            // Simulate an error during crypto API initialization
            every { cryptoApi.cryptoApiStatus } returns CryptoAPIStatus.INIT_FAIL
            // Get biometric info
            result = biometricRepository.getBiometricInfo()
            // Check that biometric authentication is not available
            assertFalse(result.isAuthenticationAvailable(), "Is authentication available")

            // Simulate secret key of crypto API is invalidated
            every { cryptoApi.cryptoApiStatus } returns CryptoAPIStatus.INVALIDATED
            // Get biometric info
            result = biometricRepository.getBiometricInfo()
            // Check that biometric authentication is not available
            assertFalse(result.isAuthenticationAvailable(), "Is authentication available")
        }
    }

    @Test
    fun `test getBiometricPromptInfo() behavior`() {
        logTestName()

        every { localization.getString(R.string.biometric_prompt_enroll_title) } returns "Enable Biometric Login?"
        every { localization.getString(R.string.biometric_prompt_enroll_subtitle) } returns "Authenticate yourself to enroll your credential"
        every { localization.getString(R.string.biometric_prompt_negative_button_label) } returns "Cancel"
        every { localization.getString(R.string.biometric_prompt_login_title) } returns "Biometric login"
        every { localization.getString(R.string.biometric_prompt_login_subtitle) } returns "Log in using your biometric credential"

        // Generate biometric prompt info for enrollment (encryption)
        var result = biometricRepository.getBiometricPromptInfo(CryptoPurpose.ENCRYPTION)

        // Check prompt info
        assertEquals("Enable Biometric Login?", result.title, "Biometric prompt title")
        assertEquals("Authenticate yourself to enroll your credential", result.subtitle, "Biometric prompt subtitle")
        assertEquals("Cancel", result.negativeButtonText, "Biometric prompt subtitle")
        assertEquals(BiometricManager.Authenticators.BIOMETRIC_STRONG, result.allowedAuthenticators, "Biometric prompt subtitle")

        // Generate biometric prompt info for login (decryption)
        result = biometricRepository.getBiometricPromptInfo(CryptoPurpose.DECRYPTION)

        // Check prompt info
        assertEquals("Biometric login", result.title, "Biometric prompt title")
        assertEquals("Log in using your biometric credential", result.subtitle, "Biometric prompt subtitle")
        assertEquals("Cancel", result.negativeButtonText, "Biometric prompt subtitle")
        assertEquals(BiometricManager.Authenticators.BIOMETRIC_STRONG, result.allowedAuthenticators, "Biometric prompt subtitle")
    }

    @Test
    fun `test getCryptoObject() behavior for encryption`() {
        logTestName()

        runTest {
            // Simulate that crypto API is not ready
            every { cryptoApi.cryptoApiStatus } returns CryptoAPIStatus.INIT_FAIL
            // Get crypto object
            var result = biometricRepository.getCryptoObject(CryptoPurpose.ENCRYPTION)
            assertNull(result, "Crypto object for encryption")

            // Simulate that crypto API is ready
            every { cryptoApi.cryptoApiStatus } returns CryptoAPIStatus.READY
            every { cryptoApi.getCryptoObjectForEncryption() } returns cryptoObject
            // Get crypto object
            result = biometricRepository.getCryptoObject(CryptoPurpose.ENCRYPTION)
            assertEquals(cryptoObject, result, "Crypto object for encryption")
        }
    }

    @Test
    fun `test getCryptoObject() behavior for decryption`() {
        logTestName()

        runTest {
            // Simulate that crypto API is not ready
            every { cryptoApi.cryptoApiStatus } returns CryptoAPIStatus.INIT_FAIL
            // Get crypto object
            var result = biometricRepository.getCryptoObject(CryptoPurpose.DECRYPTION)
            assertNull(result, "Crypto object for encryption")

            // Simulate that crypto API is ready but no biometric credential is saved
            every { cryptoApi.cryptoApiStatus } returns CryptoAPIStatus.READY
            every { preferences[PreferencesKeys.BIOMETRIC_IV] } returns null
            // Get crypto object
            result = biometricRepository.getCryptoObject(CryptoPurpose.DECRYPTION)
            assertNull(result, "Crypto object for encryption")

            // Simulate that crypto API is ready and biometric credential is saved
            mockkStatic(Base64::class)
            val testIv = "test-IV"
            val testIvByteArray = byteArrayOf(0xDE.toByte(), 0xAD.toByte(), 0xBE.toByte(), 0xEF.toByte())
            every { cryptoApi.cryptoApiStatus } returns CryptoAPIStatus.READY
            every { preferences[PreferencesKeys.BIOMETRIC_IV] } returns testIv
            every { Base64.decode(testIv, Base64.DEFAULT) } returns testIvByteArray
            every { cryptoApi.getCryptoObjectForDecryption(testIvByteArray) } returns cryptoObject
            // Get crypto object
            result = biometricRepository.getCryptoObject(CryptoPurpose.DECRYPTION)
            assertEquals(cryptoObject, result, "Crypto object for encryption")
        }
    }

    @Test
    fun `test encryptAndStoreToken() behavior`() {
        logTestName()

        val testUserToken = "test-user-token"

        runTest {
            // Simulate that crypto API is not ready
            every { cryptoApi.cryptoApiStatus } returns CryptoAPIStatus.INIT_FAIL
            // Encrypt token
            biometricRepository.encryptAndStoreToken(testUserToken, cryptoObject)
            // Check that no encryption is performed and nothing is stored into preferences
            verify(inverse = true) { cryptoApi.encryptData(any(), any()) }
            coVerify(inverse = true) { dataStore.updateData(any()) }

            // Simulate that crypto object provides an invalid cipher
            every { cryptoApi.cryptoApiStatus } returns CryptoAPIStatus.READY
            every { cryptoObject.cipher } returns null
            // Encrypt token
            biometricRepository.encryptAndStoreToken(testUserToken, cryptoObject)
            // Check that no encryption is performed and nothing is stored into preferences
            verify(inverse = true) { cryptoApi.encryptData(any(), any()) }
            coVerify(inverse = true) { dataStore.updateData(any()) }

            // Simulate a correct token encryption
            every { cryptoApi.cryptoApiStatus } returns CryptoAPIStatus.READY
            every { cryptoObject.cipher } returns cipher
            every { cryptoApi.encryptData(testUserToken, cipher) } returns CipherDataWrapper(
                data = byteArrayOf(),
                initializationVector = byteArrayOf()
            )
            // Encrypt token
            biometricRepository.encryptAndStoreToken(testUserToken, cryptoObject)
            // Check that encryption is performed and biometric tokens are stored into preferences
            verify { cryptoApi.encryptData(testUserToken, cipher) }
            coVerify { dataStore.updateData(any()) }
        }
    }

    @Test
    fun `test decryptToken() behavior`() {
        logTestName()

        val encodedEncryptedToken = "test-encoded-encrypted-token"
        val encryptedToken = byteArrayOf()
        val decryptedToken = "test-token"

        runTest {
            // Simulate that crypto API is not ready
            every { cryptoApi.cryptoApiStatus } returns CryptoAPIStatus.INIT_FAIL
            // Decrypt token
            biometricRepository.decryptToken(cryptoObject)
            // Check that no decryption is performed
            verify(inverse = true) { cryptoApi.decryptData(any(), any()) }

            // Simulate that crypto object provides an invalid cipher
            every { cryptoApi.cryptoApiStatus } returns CryptoAPIStatus.READY
            every { cryptoObject.cipher } returns null
            // Decrypt token
            biometricRepository.decryptToken(cryptoObject)
            // Check that no decryption is performed
            verify(inverse = true) { cryptoApi.decryptData(any(), any()) }

            // Simulate a correct token decryption
            every { cryptoApi.cryptoApiStatus } returns CryptoAPIStatus.READY
            every { cryptoObject.cipher } returns cipher
            every { preferences[PreferencesKeys.BIOMETRIC_TOKEN] } returns encodedEncryptedToken
            mockkStatic(Base64::class)
            every { Base64.decode(encodedEncryptedToken, Base64.DEFAULT) } returns encryptedToken
            every { cryptoApi.decryptData(encryptedToken, cipher) } returns decryptedToken
            // Decrypt token
            val result = biometricRepository.decryptToken(cryptoObject)
            // Check that correct decryption is performed
            assertEquals(decryptedToken, result, "Decrypted user token")
        }
    }

    @Test
    fun `test removeToken() behavior`() {
        logTestName()

        runTest {
            // Request to remove biometric tokens
            biometricRepository.removeToken()

            // Check that preferences are updated
            coVerify { dataStore.updateData(any()) }
        }
    }
}
