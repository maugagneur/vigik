package com.kidor.vigik.utils

import retrofit2.HttpException
import retrofit2.Response

/**
 * Utility class to help when performing some network call.
 */
object NetworkHelper {

    /**
     * Executes an network API call and handle every types of result.
     *
     * @param execute The network API call to execute.
     */
    @Suppress("TooGenericExceptionCaught")
    suspend fun <T : Any> handleApi(execute: suspend () -> Response<T>): NetworkResult<T> {
        return try {
            val response = execute()

            if (response.isSuccessful) {
                NetworkResult.Success(response.code(), response.body())
            } else {
                NetworkResult.Error(response.code(), response.errorBody()?.string())
            }
        } catch (exception: HttpException) {
            NetworkResult.Error(exception.code(), exception.message())
        } catch (throwable: Throwable) {
            NetworkResult.Exception(throwable)
        }
    }
}
