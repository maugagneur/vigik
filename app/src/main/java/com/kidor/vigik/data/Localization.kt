package com.kidor.vigik.data

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources.NotFoundException
import androidx.annotation.StringRes
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Context wrapper than allow to get translations based on the current app's language.
 */
@Singleton
class Localization @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val configuration = Configuration(context.resources.configuration)

    /**
     * Returns the string value associated with a particular resource ID.
     *
     * @param resId The resource's ID to find.
     */
    fun getString(@StringRes resId: Int): String {
        return try {
            context.createConfigurationContext(configuration).resources.getString(resId)
        } catch (exception: NotFoundException) {
            Timber.e(exception, "String resource not found: $resId")
            resId.toString()
        }
    }

    /**
     * Returns the string value associated with a particular resource ID, substituting the format arguments.
     *
     * @param resId      The resource's ID to find.
     * @param formatArgs The format arguments that will be used for substitution.
     */
    fun getString(@StringRes resId: Int, vararg formatArgs: Any?): String {
        return try {
            context.createConfigurationContext(configuration).resources.getString(resId, *formatArgs)
        } catch (exception: NotFoundException) {
            Timber.e(exception, "String resource not found: $resId")
            resId.toString()
        }
    }
}
