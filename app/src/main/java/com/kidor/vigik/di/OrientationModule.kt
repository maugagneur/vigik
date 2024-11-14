package com.kidor.vigik.di

import android.content.Context
import com.google.android.gms.location.FusedOrientationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

/**
 * Dependency injection module related to device's orientation features.
 */
@Module
@InstallIn(SingletonComponent::class)
object OrientationModule {

    /**
     * Provides instance of [FusedOrientationProviderClient].
     *
     * @param context The application context.
     */
    @Provides
    fun providesFusedOrientationProviderClient(@ApplicationContext context: Context): FusedOrientationProviderClient =
        LocationServices.getFusedOrientationProviderClient(context)
}
