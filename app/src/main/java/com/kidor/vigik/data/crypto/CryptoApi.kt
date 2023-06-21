package com.kidor.vigik.data.crypto

import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyPermanentlyInvalidatedException
import android.security.keystore.KeyProperties
import androidx.biometric.BiometricPrompt.CryptoObject
import com.kidor.vigik.data.crypto.model.CipherDataWrapper
import com.kidor.vigik.data.crypto.model.CryptoAPIStatus
import timber.log.Timber
import java.security.KeyStore
import java.security.NoSuchAlgorithmException
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.NoSuchPaddingException
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.inject.Inject
import javax.inject.Singleton

private const val ANDROID_KEYSTORE_TYPE = "AndroidKeyStore"
private const val BIOMETRIC_CRYPTO_SECRET_KEY_ALIAS = "VigikBiometricCryptoSecretKey"
private const val ENCRYPTION_BLOCK_MODE = KeyProperties.BLOCK_MODE_GCM
private const val ENCRYPTION_PADDING = KeyProperties.ENCRYPTION_PADDING_NONE
private const val ENCRYPTION_ALGORITHM = KeyProperties.KEY_ALGORITHM_AES
private const val GCM_AUTHENTICATION_TAG_LENGTH = 128

/**
 * API dedicated to cryptography operations.
 */
@Singleton
class CryptoApi @Inject constructor() {

    var cryptoApiStatus: CryptoAPIStatus = CryptoAPIStatus.NOT_READY

    init {
        // Perform a dummy creation of a crypto object.
        // This will trigger cipher creation and secret key fetch/generation. If no exception is risen then we could
        // consider that this API is ready to be used.
        cryptoApiStatus = try {
            getCryptoObjectForEncryption()
            CryptoAPIStatus.READY
        } catch (exception: NoSuchAlgorithmException) {
            CryptoAPIStatus.INIT_FAIL
        } catch (exception: NoSuchPaddingException) {
            CryptoAPIStatus.INIT_FAIL
        } catch (exception: KeyPermanentlyInvalidatedException) {
            removeSecretKey()
            CryptoAPIStatus.INVALIDATED
        } catch (exception: Exception) {
            CryptoAPIStatus.NOT_READY
        }
    }

    /**
     * Encrypts given data and return a [CipherDataWrapper].
     *
     * @param data   The data to encrypt.
     * @param cipher The [Cipher] use for encryption.
     */
    fun encryptData(data: String, cipher: Cipher): CipherDataWrapper {
        val encodedData = data.toByteArray(Charsets.UTF_8)
        val encryptedData = cipher.doFinal(encodedData)
        return CipherDataWrapper(
            data = encryptedData,
            initializationVector = cipher.iv
        )
    }

    /**
     * Creates a new [CryptoObject] instance for encryption purpose.
     */
    fun getCryptoObjectForEncryption(): CryptoObject {
        val cipher = getCipher()
        cipher.init(Cipher.ENCRYPT_MODE, getOrGenerateSecretKey())
        return CryptoObject(cipher)
    }

    /**
     * Creates a new [CryptoObject] instance for decryption purpose.
     *
     * @param initializationVector The initialization vector source buffer.
     */
    fun getCryptoObjectForDecryption(initializationVector: ByteArray): CryptoObject {
        val cipher = getCipher()
        cipher.init(
            Cipher.DECRYPT_MODE,
            getOrGenerateSecretKey(),
            GCMParameterSpec(GCM_AUTHENTICATION_TAG_LENGTH, initializationVector)
        )
        return CryptoObject(cipher)
    }

    /**
     * Returns the [SecretKey] or create it if it does not exist.
     */
    private fun getOrGenerateSecretKey(): SecretKey = getSecretKey() ?: generateSecretKey()

    /**
     * Generates a new [SecretKey] for encrypt and decrypt purpose, then store it in Android keystore.
     *
     * @return The generated key.
     */
    private fun generateSecretKey(): SecretKey {
        Timber.d("Generate secret key")
        val keyGenParameterSpec = KeyGenParameterSpec.Builder(
            BIOMETRIC_CRYPTO_SECRET_KEY_ALIAS,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        ).apply {
            setBlockModes(ENCRYPTION_BLOCK_MODE)
            setEncryptionPaddings(ENCRYPTION_PADDING)
            // This flag indicates that every key usage requires a user authentication.
            setUserAuthenticationRequired(true)
            // Invalidate the keys if the user has registered a new biometric credential, such as a new fingerprint.
            // Can call this method only on Android 7.0 (API level 24) or higher.
            // The variable "invalidatedByBiometricEnrollment" is true by default.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                setInvalidatedByBiometricEnrollment(true)
            }
        }.build()

        val keyGenerator = KeyGenerator.getInstance(ENCRYPTION_ALGORITHM, ANDROID_KEYSTORE_TYPE)
        keyGenerator.init(keyGenParameterSpec)
        return keyGenerator.generateKey()
    }

    /**
     * Retrieves the [SecretKey] from the Android keystore.
     * Returns null if key does not exist.
     */
    private fun getSecretKey(): SecretKey? {
        return getKeystore().getKey(BIOMETRIC_CRYPTO_SECRET_KEY_ALIAS, null) as SecretKey?
    }

    /**
     * Removes the secret key from the Android keystore.
     */
    private fun removeSecretKey() {
        getKeystore().deleteEntry(BIOMETRIC_CRYPTO_SECRET_KEY_ALIAS)
    }

    /**
     * Returns the Android keystore, ready to be used.
     */
    private fun getKeystore(): KeyStore {
        val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE_TYPE)
        // Before the keystore can be accessed, it must be loaded
        keyStore.load(null)
        return keyStore
    }

    /**
     * Returns a Cipher object that implements the algorithm we use.
     *
     * @throws NoSuchAlgorithmException If transformation is null, empty, in an invalid format, or if no Provider
     *                                  supports a CipherSpi implementation for the specified algorithm.
     * @throws NoSuchPaddingException   If transformation contains a padding scheme that is not available.
     */
    private fun getCipher(): Cipher {
        return Cipher.getInstance("$ENCRYPTION_ALGORITHM/$ENCRYPTION_BLOCK_MODE/$ENCRYPTION_PADDING")
    }
}
