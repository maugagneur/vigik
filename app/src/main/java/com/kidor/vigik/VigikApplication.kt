package com.kidor.vigik

import android.app.Application
import android.content.IntentFilter
import android.location.LocationManager
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.kidor.vigik.receivers.LocationStateChangeReceiver
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

    override fun onCreate() {
        super.onCreate()

        Timber.plant(object : Timber.DebugTree() {
            override fun createStackElementTag(element: StackTraceElement): String {
                // Add line number after TAG
                return super.createStackElementTag(element) + ":" + element.lineNumber
            }
        })

        // This BroadcastReceiver needs to be registered at runtime
        ContextCompat.registerReceiver(
            applicationContext,
            LocationStateChangeReceiver(),
            IntentFilter(LocationManager.MODE_CHANGED_ACTION),
            ContextCompat.RECEIVER_NOT_EXPORTED
        )
    }

    override fun getWorkManagerConfiguration(): Configuration =
        Configuration.Builder().apply {
            setWorkerFactory(workerFactory)
            setMinimumLoggingLevel(Log.DEBUG)
        }.build()
}
