package com.kidor.vigik.di

import android.content.ContentResolver
import android.content.Context
import com.kidor.vigik.data.telephony.TelephonyRepository
import com.kidor.vigik.data.telephony.TelephonyRepositoryImp
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
     * Provides instance of [TelephonyRepository].
     */
    @Singleton
    @Provides
    fun providesTelephonyRepository(contentResolver: ContentResolver): TelephonyRepository =
        TelephonyRepositoryImp(contentResolver)
}
