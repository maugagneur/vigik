package com.kidor.vigik.di

import android.content.Context
import androidx.biometric.BiometricManager
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.kidor.vigik.data.user.UserRepository
import com.kidor.vigik.data.user.UserRepositoryImp
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

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
