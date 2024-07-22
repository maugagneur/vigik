package com.kidor.vigik.utils

/**
 * Types of result for a network request.
 */
sealed interface NetworkResult<out T : Any?> {
    /**
     * Successful network result.
     *
     * @param code HTTP status code.
     * @param data Data retrieved.
     */
    data class Success<out T : Any>(val code: Int, val data: T?) : NetworkResult<T>

    /**
     * Failure network result.
     *
     * @param code         HTTP status code.
     * @param errorMessage Error message.
     */
    data class Error<out T : Any>(val code: Int, val errorMessage: String?) : NetworkResult<T>

    /**
     * Network-related or technical issue result.
     *
     * @param throwable The exception.
     */
    data class Exception(val throwable: Throwable) : NetworkResult<Nothing>
}
