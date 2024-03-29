package com.kidor.vigik.di

import android.content.ContentResolver
import android.content.Context
import android.os.Build
import android.telephony.SmsManager
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
    fun providesTelephonyRepository(contentResolver: ContentResolver, smsManager: SmsManager): TelephonyRepository =
        TelephonyRepositoryImp(contentResolver, smsManager)

    /**
     * Provides instance of [SmsManager].
     *
     * @param context The application's context.
     */
    @Suppress("DEPRECATION")
    @Provides
    fun providesSmsManager(@ApplicationContext context: Context): SmsManager {
        // SmsManager.getDefault() method is only deprecated starting from API 31
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            context.getSystemService(SmsManager::class.java)
        } else {
            // We still use SmsManager.getDefault() below API 31 because getSystemService(SmsManager::class.java)
            // returns null for API levels below 31...
            SmsManager.getDefault()
        }
    }
}
