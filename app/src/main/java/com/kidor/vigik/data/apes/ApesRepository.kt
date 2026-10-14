package com.kidor.vigik.data.apes

import com.kidor.vigik.data.apes.model.AvailabilityOut
import com.kidor.vigik.data.apes.model.DayOut
import com.kidor.vigik.data.apes.model.MeOut
import com.kidor.vigik.data.apes.model.NowOut
import com.kidor.vigik.data.apes.model.VoiceOut
import com.kidor.vigik.utils.NetworkResult
import kotlinx.coroutines.flow.Flow

/**
 * Repository to manage APES API operations and cached tokens.
 */
interface ApesRepository {
    /** Observes saved user token. */
    fun getUserToken(): Flow<String?>
    /** Saves user token into datastore preferences. */
    suspend fun saveUserToken(token: String)
    /** Clears saved user token. */
    suspend fun clearUserToken()
    /** Fetches active voice channel members. */
    suspend fun getVoice(): NetworkResult<VoiceOut>
    /** Fetches snapshot of players right now. */
    suspend fun getStatusNow(): NetworkResult<NowOut>
    /** Fetches group availability between two dates. */
    suspend fun getAvailability(fromDate: String, toDate: String): NetworkResult<AvailabilityOut>
    /** Fetches current user profile and slots. */
    suspend fun getMe(token: String): NetworkResult<MeOut>
    /** Applies a quick preset for given date. */
    suspend fun applyPreset(date: String, preset: AvailabilityPreset): NetworkResult<DayOut>
    /** Observes saved preset start time. */
    fun getPresetStartTime(): Flow<String>
    /** Observes saved preset end time. */
    fun getPresetEndTime(): Flow<String>
    /** Saves preset start and end times into datastore preferences. */
    suspend fun savePresetTimes(startTime: String, endTime: String)
}

/** Default start time for PRESENT_TONIGHT preset. */
const val DEFAULT_PRESET_START_TIME = "21:30"

/** Default end time for PRESENT_TONIGHT preset. */
const val DEFAULT_PRESET_END_TIME = "01:00"

