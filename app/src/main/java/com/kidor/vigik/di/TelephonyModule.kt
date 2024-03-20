package com.kidor.vigik.di

import android.content.ContentResolver
import android.content.Context
import com.kidor.vigik.data.telephony.ContactRepository
import com.kidor.vigik.data.telephony.ContactRepositoryImp
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Dependency injection module related to telephony features.
 */
@Module
@InstallIn(SingletonComponent::class)
object TelephonyModule {

    /**
     * Provides instance of [ContentResolver].
     *
     * @param context The application's context.
     */
    @Provides
    fun providesContentResolver(@ApplicationContext context: Context): ContentResolver = context.contentResolver

    /**
     * Provides instance of [ContactRepository].
     */
    @Singleton
    @Provides
    fun providesContactRepository(contentResolver: ContentResolver): ContactRepository =
        ContactRepositoryImp(contentResolver)
}
