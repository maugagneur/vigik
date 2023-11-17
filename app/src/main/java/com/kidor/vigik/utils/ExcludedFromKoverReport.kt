package com.kidor.vigik.utils

/**
 * Denotes that the class or function should be excluded by Kover report generation task.
 */
@Retention(AnnotationRetention.BINARY)
@Target(
    AnnotationTarget.CLASS,
    AnnotationTarget.FUNCTION
)
annotation class ExcludedFromKoverReport
