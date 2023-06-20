package com.kidor.vigik.extensions

import android.content.Context
import android.content.ContextWrapper
import androidx.fragment.app.FragmentActivity

/**
 * Looks for the top [FragmentActivity] of the application.
 */
fun Context.findActivity(): FragmentActivity? {
    var currentContext = this
    var previousContext: Context? = null
    while (currentContext is ContextWrapper && previousContext != currentContext) {
        if (currentContext is FragmentActivity) {
            return currentContext
        }
        previousContext = currentContext
        currentContext = currentContext.baseContext
    }
    return null
}
