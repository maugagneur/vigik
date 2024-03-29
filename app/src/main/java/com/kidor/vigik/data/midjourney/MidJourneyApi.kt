package com.kidor.vigik.data.midjourney

import com.kidor.vigik.data.midjourney.model.GetGeneratedImagesResponse
import retrofit2.http.GET
import retrofit2.http.Query

const val MID_JOURNEY_API_BASE_URL = "https://mj.akgns.com/"

/**
 * MidJourney HTTP API used by Retrofit.
 */
fun interface MidJourneyApi {

    @GET("images")
    suspend fun getRandomGeneratedImages(@Query("page") page: Int): GetGeneratedImagesResponse
}
