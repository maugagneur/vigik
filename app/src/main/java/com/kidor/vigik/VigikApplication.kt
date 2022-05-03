package com.kidor.vigik

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

/**
 * Implementation of [Application].
 */
@HiltAndroidApp
class VigikApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        Timber.plant(object : Timber.DebugTree() {
            override fun createStackElementTag(element: StackTraceElement): String {
                // Add line number after TAG
                return super.createStackElementTag(element) + ":" + element.lineNumber
            }
        })
    }
}
