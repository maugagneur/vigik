package com.kidor.vigik

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.bluetooth.BluetoothAdapter
import android.content.IntentFilter
import android.location.LocationManager
import android.os.Build
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.kidor.vigik.data.bluetooth.BluetoothApi
import com.kidor.vigik.receivers.BluetoothStateReceiver
import com.kidor.vigik.receivers.LocationStateChangeReceiver
import com.kidor.vigik.receivers.NotificationReceiver
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import javax.inject.Inject

/**
 * Implementation of [Application].
 */
@HiltAndroidApp
class VigikApplication : Application(), Configuration.Provider {

    /**
     * Worker Factory for the Hilt Extension.
     */
    @Inject lateinit var workerFactory: HiltWorkerFactory

    @Inject lateinit var notificationManager: NotificationManager

    @Inject lateinit var bluetoothApi: BluetoothApi

    override fun onCreate() {
        super.onCreate()

        Timber.plant(object : Timber.DebugTree() {
            override fun createStackElementTag(element: StackTraceElement): String {
                // Add line number after TAG
                return super.createStackElementTag(element) + ":" + element.lineNumber
            }
        })

        registerReceivers()

        createNotificationChannel()
    }

    override fun getWorkManagerConfiguration(): Configuration =
        Configuration.Builder().apply {
            setWorkerFactory(workerFactory)
            setMinimumLoggingLevel(Log.DEBUG)
        }.build()

    /**
     * Create a notification channel for the app if needed.
     */
    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because the NotificationChannel class is new and not in
        // the support library.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val defaultChannel = NotificationChannel(
                applicationContext.getString(R.string.notification_default_channel_id),
                applicationContext.getString(R.string.notification_default_channel_name),
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = applicationContext.getString(R.string.notification_default_channel_description)
            }
            // Register the channel with the system
            notificationManager.createNotificationChannel(defaultChannel)
        }
    }

    /**
     * Register all receivers used by the application.
     */
    private fun registerReceivers() {
        // This BroadcastReceiver needs to be registered at runtime
        ContextCompat.registerReceiver(
            applicationContext,
            LocationStateChangeReceiver(bluetoothApi),
            IntentFilter(LocationManager.MODE_CHANGED_ACTION),
            ContextCompat.RECEIVER_NOT_EXPORTED
        )
        ContextCompat.registerReceiver(
            applicationContext,
            NotificationReceiver(notificationManager),
            IntentFilter(NotificationReceiver.ACTION_REMOVE),
            ContextCompat.RECEIVER_NOT_EXPORTED
        )
        ContextCompat.registerReceiver(
            applicationContext,
            BluetoothStateReceiver(bluetoothApi),
            IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED),
            ContextCompat.RECEIVER_NOT_EXPORTED
        )
    }
}
