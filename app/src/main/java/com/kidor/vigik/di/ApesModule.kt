package com.kidor.vigik.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.glance.appwidget.GlanceAppWidgetManager
import com.kidor.vigik.data.apes.APES_API_BASE_URL
import com.kidor.vigik.data.apes.ApesApi
import com.kidor.vigik.data.apes.ApesRepository
import com.kidor.vigik.data.apes.ApesRepositoryImpl
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
object ApesModule {

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
     * Provides instance of [ApesRepository].
     */
    @Singleton
    @Provides
    fun providesApesRepository(
        apesApi: ApesApi,
        preferences: DataStore<Preferences>
    ): ApesRepository = ApesRepositoryImpl(apesApi, preferences)

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
