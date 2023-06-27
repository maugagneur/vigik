package com.kidor.vigik.di

import android.content.Context
import androidx.biometric.BiometricManager
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.kidor.vigik.data.Localization
import com.kidor.vigik.data.biometric.BiometricRepository
import com.kidor.vigik.data.biometric.BiometricRepositoryImp
import com.kidor.vigik.data.crypto.CryptoApi
import com.kidor.vigik.data.user.UserRepository
import com.kidor.vigik.data.user.UserRepositoryImp
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

/**
 * Dependency injection module related to biometric features.
 */
@Module
@InstallIn(SingletonComponent::class)
object BiometricModule {

    /**
     * Provides instance of [BiometricManager].
     *
     * @param context The application context.
     */
    @Singleton
    @Provides
    fun provideBiometricManager(@ApplicationContext context: Context): BiometricManager = BiometricManager.from(context)

    /**
     * Provides instance of [BiometricRepository].
     *
     * @param biometricManager The biometric manager.
     * @param cryptoApi        The crypto API module.
     * @param localization     The localization module.
     * @param preferences      The shared preferences data store.
     */
    @Singleton
    @Provides
    fun provideBiometricRepository(
        biometricManager: BiometricManager,
        cryptoApi: CryptoApi,
        localization: Localization,
        preferences: DataStore<Preferences>
    ): BiometricRepository =
        BiometricRepositoryImp(
            biometricManager = biometricManager,
            cryptoApi = cryptoApi,
            localization = localization,
            preferences = preferences,
            dispatcher = Dispatchers.IO
        )

    /**
     * Provides instance of [UserRepository].
     *
     * @param preferences The shared preferences data store.
     */
    @Singleton
    @Provides
    fun provideUserRepository(
        preferences: DataStore<Preferences>
    ): UserRepository = UserRepositoryImp(
        preferences = preferences
    )
}
