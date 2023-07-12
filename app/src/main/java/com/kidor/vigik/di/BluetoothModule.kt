package com.kidor.vigik.di

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.location.LocationManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

/**
 * Dependency injection module related to Bluetooth features.
 */
@Module
@InstallIn(SingletonComponent::class)
object BluetoothModule {

    /**
     * Provides instance of [BluetoothAdapter].
     *
     * @param context The application context.
     */
    @Provides
    fun providesBluetoothAdapter(@ApplicationContext context: Context): BluetoothAdapter =
        (context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager).adapter

    /**
     * Provides instance of [LocationManager].
     *
     * @param context The application context.
     */
    @Provides
    fun providesLocationManager(@ApplicationContext context: Context): LocationManager =
        (context.getSystemService(Context.LOCATION_SERVICE) as LocationManager)
}
