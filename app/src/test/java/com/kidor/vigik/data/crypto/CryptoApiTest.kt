package com.kidor.vigik.data.crypto

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyPermanentlyInvalidatedException
import android.security.keystore.KeyProperties
import com.kidor.vigik.data.crypto.model.CryptoAPIStatus
import com.kidor.vigik.utils.AssertUtils.assertArrayEquals
import com.kidor.vigik.utils.AssertUtils.assertEquals
import com.kidor.vigik.utils.TestUtils.logTestName
import io.mockk.EqMatcher
import io.mockk.MockKAnnotations
import io.mockk.OfTypeMatcher
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockkConstructor
import io.mockk.mockkStatic
import io.mockk.verify
import org.junit.Before
import org.junit.Test
import java.security.KeyStore
import java.security.NoSuchAlgorithmException
import java.security.cert.CertificateException
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.NoSuchPaddingException
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

/**
 * Unit tests for [CryptoApi].
 */
class CryptoApiTest {

    private lateinit var cryptoApi: CryptoApi

    @MockK
    private lateinit var cipher: Cipher
    @MockK
    private lateinit var keyStore: KeyStore
    @MockK
    private lateinit var secretKey: SecretKey
    @MockK
    private lateinit var keyGenerator: KeyGenerator
    @MockK
    private lateinit var keyGenParameterSpec: KeyGenParameterSpec

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        mockkStatic(Cipher::class, KeyStore::class, KeyGenerator::class)
        every { Cipher.getInstance(any()) } returns cipher
        every { KeyStore.getInstance(any()) } returns keyStore
        every { keyStore.getKey(any(), null) } returns secretKey
        every { KeyGenerator.getInstance(any(String::class), any(String::class)) } returns keyGenerator
        mockkConstructor(KeyGenParameterSpec.Builder::class)
        every {
            constructedWith<KeyGenParameterSpec.Builder>(
                OfTypeMatcher<String>(String::class),
                EqMatcher(KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
            ).build()
        } returns keyGenParameterSpec
    }

    @Test
    fun `test crypto API initialization when getting cipher object generates an exception`() {
        logTestName()

        // Getting cipher object will generates a NoSuchAlgorithmException
        every { Cipher.getInstance(any()) } throws NoSuchAlgorithmException()
        // Initialize API
        cryptoApi = CryptoApi()
        // Check that crypto API initialization failed
        assertEquals(CryptoAPIStatus.INIT_FAIL, cryptoApi.cryptoApiStatus, "Crypto API status")

        // Getting cipher object will generates a NoSuchPaddingException
        every { Cipher.getInstance(any()) } throws NoSuchPaddingException()
        // Initialize API
        cryptoApi = CryptoApi()
        // Check that crypto API initialization failed
        assertEquals(CryptoAPIStatus.INIT_FAIL, cryptoApi.cryptoApiStatus, "Crypto API status")
    }

    @Test
    fun `test crypto API initialization when unexpected exception occurred`() {
        logTestName()

        every { keyStore.load(any()) } throws CertificateException()

        // Initialize API
        cryptoApi = CryptoApi()

        // Check that crypto API initialization failed
        assertEquals(CryptoAPIStatus.NOT_READY, cryptoApi.cryptoApiStatus, "Crypto API status")
    }

    @Test
    fun `test crypto API initialization with success`() {
        logTestName()

        // Initialize API
        cryptoApi = CryptoApi()

        // Check that cipher was init with our secret key
        verify { cipher.init(Cipher.ENCRYPT_MODE, secretKey) }
        // Check that crypto API initialization succeed
        assertEquals(CryptoAPIStatus.READY, cryptoApi.cryptoApiStatus, "Crypto API status")
    }

    @Test
    fun `test crypto API initialization when previous key was invalidated`() {
        logTestName()

        every { keyStore.getKey(any(), null) } throws KeyPermanentlyInvalidatedException()

        // Initialize API
        cryptoApi = CryptoApi()

        // Check that cipher was not init with our secret key
        verify(inverse = true) { cipher.init(Cipher.ENCRYPT_MODE, secretKey) }
        // Check that our secret key is removed from keystore
        verify { keyStore.deleteEntry(any()) }
        // Check that crypto API initialization failed because previous key was invalidated
        assertEquals(CryptoAPIStatus.INVALIDATED, cryptoApi.cryptoApiStatus, "Crypto API status")
    }

    @Test
    fun `test get crypto object for encryption`() {
        logTestName()

        // Initialize API
        cryptoApi = CryptoApi()

        // Get crypto object when secret key already exists
        var cryptoObject = cryptoApi.getCryptoObjectForEncryption()

        // Check that cipher was init with our secret key
        verify { cipher.init(Cipher.ENCRYPT_MODE, secretKey) }
        // Check that our cipher will be used for encryption
        assertEquals(cipher, cryptoObject.cipher, "Cipher")

        // Get crypto object when secret key needs to be generated
        every { keyStore.getKey(any(), null) } returns null
        every { keyGenerator.generateKey() } returns secretKey
        cryptoObject = cryptoApi.getCryptoObjectForEncryption()

        // Check that cipher was init with our secret key
        verify { cipher.init(Cipher.ENCRYPT_MODE, secretKey) }
        // Check that the key generator was initialized
        verify { keyGenerator.init(keyGenParameterSpec) }
        // Check that our cipher will be used for encryption
        assertEquals(cipher, cryptoObject.cipher, "Cipher")
    }

    @Test
    fun `test get crypto object for decryption`() {
        logTestName()

        // Initialize API
        cryptoApi = CryptoApi()

        // Get crypto object
        val initializationVector = byteArrayOf(0x13, 0x37)
        val cryptoObject = cryptoApi.getCryptoObjectForDecryption(initializationVector)

        // Check that cipher was init with our secret key and our initialization vector
        verify {
            cipher.init(
                Cipher.DECRYPT_MODE,
                secretKey,
                withArg<GCMParameterSpec> {
                    assertArrayEquals(initializationVector, it.iv, "Initialization vector")
                }
            )
        }
        // Check that our cipher will be used for decryption
        assertEquals(cipher, cryptoObject.cipher, "Cipher")
    }

    @Test
    fun `test data encryption`() {
        logTestName()

        val initializationVector = byteArrayOf(0x42, 0x42, 0x42, 0x42)
        val data = "Data to encrypt"
        val encryptedData = byteArrayOf(0xDE.toByte(), 0xAD.toByte(), 0xBE.toByte(), 0xEF.toByte())
        every { cipher.iv } returns initializationVector
        every { cipher.doFinal(data.toByteArray(Charsets.UTF_8)) } returns encryptedData

        // Initialize API
        cryptoApi = CryptoApi()

        // Encrypt data
        val result = cryptoApi.encryptData(data, cipher)

        // Check that our data was encrypted with our cipher
        verify { cipher.doFinal(data.toByteArray()) }
        // Check that returned object contains encrypted data and our IV
        assertEquals(encryptedData, result.data, "Encrypted data")
        assertEquals(initializationVector, result.initializationVector, "Initialization vector")
    }

    @Test
    fun `test data decryption`() {
        logTestName()

        val encryptedData = byteArrayOf(0xDE.toByte(), 0xAD.toByte(), 0xBE.toByte(), 0xEF.toByte())
        val decryptedData = byteArrayOf(0x13, 0x37)
        every { cipher.doFinal(encryptedData) } returns decryptedData

        // Initialize API
        cryptoApi = CryptoApi()

        // Decrypt data
        val result = cryptoApi.decryptData(encryptedData, cipher)

        // Check that our data was decrypted with our cipher
        verify { cipher.doFinal(encryptedData) }
        // Check that returned object is our decrypted data
        assertEquals(decryptedData.toString(Charsets.UTF_8), result, "Decrypted data")
    }
}
