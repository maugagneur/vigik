package com.kidor.vigik.di

import com.kidor.vigik.data.diablo.Diablo4API
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

private const val DIABLO4_API_BASE_URL = "https://api.worldstone.io/"
private const val CONNECTION_TIMEOUT_IN_SECOND = 30L
private const val READ_TIMEOUT_IN_SECOND = 10L

/**
 * Dependency injection module related to Diablo IV features.
 */
@Module
@InstallIn(SingletonComponent::class)
object DiabloModule {

    @Provides
    fun providesOkHttpClient(): OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(CONNECTION_TIMEOUT_IN_SECOND, TimeUnit.SECONDS)
        .readTimeout(READ_TIMEOUT_IN_SECOND, TimeUnit.SECONDS)
        .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
        .build()

    @Singleton
    @Provides
    fun providesDiablo4API(httpClient: OkHttpClient): Diablo4API = Retrofit.Builder()
        .baseUrl(DIABLO4_API_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(httpClient)
        .build()
        .create(Diablo4API::class.java)
}
