package com.kidor.vigik.di

import android.content.Context
import androidx.glance.appwidget.GlanceAppWidgetManager
import com.kidor.vigik.data.diablo.DIABLO4_API_BASE_URL
import com.kidor.vigik.data.diablo.Diablo4API
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

private const val CONNECTION_TIMEOUT_IN_SECOND = 30L
private const val READ_TIMEOUT_IN_SECOND = 10L

/**
 * Dependency injection module related to Diablo IV features.
 */
@Module
@InstallIn(SingletonComponent::class)
object DiabloModule {

    /**
     * Provides instance of [OkHttpClient].
     */
    @Provides
    fun providesOkHttpClient(): OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(CONNECTION_TIMEOUT_IN_SECOND, TimeUnit.SECONDS)
        .readTimeout(READ_TIMEOUT_IN_SECOND, TimeUnit.SECONDS)
        .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
        .build()

    /**
     * Provides instance of [Diablo4API].
     *
     * @param httpClient The HTTP client used for requests.
     */
    @Singleton
    @Provides
    fun providesDiablo4API(httpClient: OkHttpClient): Diablo4API = Retrofit.Builder()
        .baseUrl(DIABLO4_API_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(httpClient)
        .build()
        .create(Diablo4API::class.java)

    /**
     * Provides instance of [GlanceAppWidgetManager].
     *
     * @param context The application context.
     */
    @Singleton
    @Provides
    fun providesGlanceAppWidgetManager(@ApplicationContext context: Context): GlanceAppWidgetManager =
        GlanceAppWidgetManager(context)
}
