package com.kidor.vigik.di

import android.content.Context
import androidx.glance.appwidget.GlanceAppWidgetManager
import com.kidor.vigik.data.apes.APES_API_BASE_URL
import com.kidor.vigik.data.apes.ApesApi
import com.kidor.vigik.data.apes.ApesRepository
import com.kidor.vigik.data.apes.ApesRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

/**
 * Dependency injection module related to APES (Sync Jeux) features.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class ApesModule {

    /**
     * Binds instance of [ApesRepository].
     */
    @Singleton
    @Binds
    abstract fun bindApesRepository(impl: ApesRepositoryImpl): ApesRepository

    companion object {
        /**
         * Provides instance of [ApesApi].
         *
         * @param httpClient The HTTP client used for requests.
         */
        @Singleton
        @Provides
        fun providesApesApi(httpClient: OkHttpClient): ApesApi = Retrofit.Builder()
            .baseUrl(APES_API_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient)
            .build()
            .create(ApesApi::class.java)

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
}

