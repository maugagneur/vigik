package com.kidor.vigik.data.diablo

import com.kidor.vigik.data.diablo.model.GetNextHellTideResponse
import com.kidor.vigik.data.diablo.model.GetNextWorldBossResponse
import retrofit2.Response
import retrofit2.http.GET

/**
 * Base URL for the Diablo IV API remote server.
 */
const val DIABLO4_API_BASE_URL = "https://api.worldstone.io/"

/**
 * Diablo IV HTTP API used by Retrofit.
 */
interface Diablo4API {
    /**
     * Returns next world boss data.
     */
    @GET("world-bosses/")
    suspend fun getNextWorldBoss(): Response<GetNextWorldBossResponse>

    /**
     * Returns next hell tide data.
     */
    @GET("helltides/")
    suspend fun getNextHellTide(): Response<GetNextHellTideResponse>
}
