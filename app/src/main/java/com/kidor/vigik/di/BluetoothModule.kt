package com.kidor.vigik.di

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
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
}
