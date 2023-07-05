package com.kidor.vigik

import android.app.Application
import android.util.Log
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
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
    }

    override fun getWorkManagerConfiguration(): Configuration =
        Configuration.Builder().apply {
            setWorkerFactory(workerFactory)
            setMinimumLoggingLevel(Log.DEBUG)
        }.build()
}
