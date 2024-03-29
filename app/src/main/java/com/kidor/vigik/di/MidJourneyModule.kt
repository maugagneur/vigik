package com.kidor.vigik.di

import com.kidor.vigik.data.midjourney.GeneratedImagesRepository
import com.kidor.vigik.data.midjourney.GeneratedImagesRepositoryImp
import com.kidor.vigik.data.midjourney.MID_JOURNEY_API_BASE_URL
import com.kidor.vigik.data.midjourney.MidJourneyApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

/**
 * Dependency injection module related to MidJourney generation features.
 */
@Module
@InstallIn(SingletonComponent::class)
object MidJourneyModule {

    /**
     * Provides instance of [MidJourneyApi].
     *
     * @param httpClient The HTTP client used for requests.
     */
    @Singleton
    @Provides
    fun providesMidJourneyApi(httpClient: OkHttpClient): MidJourneyApi = Retrofit.Builder()
        .baseUrl(MID_JOURNEY_API_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(httpClient)
        .build()
        .create(MidJourneyApi::class.java)

    /**
     * Provides instance of [GeneratedImagesRepository].
     *
     * @param midJourneyApi The HTTP API used for requests.
     */
    @Provides
    fun providesGeneratedImagesRepository(midJourneyApi: MidJourneyApi): GeneratedImagesRepository =
        GeneratedImagesRepositoryImp(midJourneyApi)
}
