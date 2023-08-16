package com.kidor.vigik.di

import android.app.NotificationManager
import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

/**
 * Dependency injection module related to notification features.
 */
@Module
@InstallIn(SingletonComponent::class)
object NotificationModule {

    /**
     * Provides instance of [NotificationManager].
     *
     * @param context The application context.
     */
    @Provides
    fun providesNotificationManager(@ApplicationContext context: Context): NotificationManager =
        (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
}
