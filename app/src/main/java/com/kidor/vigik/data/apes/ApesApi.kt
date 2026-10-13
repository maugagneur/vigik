package com.kidor.vigik.data.apes

import com.kidor.vigik.data.apes.model.AvailabilityOut
import com.kidor.vigik.data.apes.model.DayOut
import com.kidor.vigik.data.apes.model.MeOut
import com.kidor.vigik.data.apes.model.NowOut
import com.kidor.vigik.data.apes.model.RangesIn
import com.kidor.vigik.data.apes.model.VoiceOut
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Base URL of the APES service.
 */
const val APES_API_BASE_URL = "https://apes.lirlya.fr/"

/**
 * Retrofit interface to access APES API.
 */
interface ApesApi {

    /**
     * Retrieves members currently in voice channel.
     */
    @GET("api/v1/voice")
    suspend fun getVoice(): Response<VoiceOut>

    /**
     * Retrieves group snapshot right now.
     */
    @GET("api/v1/status/now")
    suspend fun getStatusNow(): Response<NowOut>

    /**
     * Retrieves calculated group availability between two dates.
     *
     * @param fromDate Start date included (YYYY-MM-DD).
     * @param toDate   End date excluded (YYYY-MM-DD).
     */
    @GET("api/v1/availability")
    suspend fun getAvailability(
        @Query("from") fromDate: String,
        @Query("to") toDate: String
    ): Response<AvailabilityOut>

    /**
     * Retrieves authenticated user info.
     *
     * @param authorization Bearer token (Bearer sj_...).
     */
    @GET("api/me/v1/")
    suspend fun getMe(
        @Header("Authorization") authorization: String
    ): Response<MeOut>

    /**
     * Declares availability ranges for a given evening date.
     *
     * @param authorization Bearer token.
     * @param date          Date string (YYYY-MM-DD).
     * @param body          Ranges to set.
     */
    @PUT("api/me/v1/day/{date}")
    suspend fun putDayAvailability(
        @Header("Authorization") authorization: String,
        @Path("date") date: String,
        @Body body: RangesIn
    ): Response<DayOut>
}
