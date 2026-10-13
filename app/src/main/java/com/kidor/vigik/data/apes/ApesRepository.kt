package com.kidor.vigik.data.apes

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.kidor.vigik.data.PreferencesKeys
import com.kidor.vigik.data.apes.model.AvailabilityOut
import com.kidor.vigik.data.apes.model.DayOut
import com.kidor.vigik.data.apes.model.MeOut
import com.kidor.vigik.data.apes.model.NowOut
import com.kidor.vigik.data.apes.model.RangeIn
import com.kidor.vigik.data.apes.model.RangesIn
import com.kidor.vigik.data.apes.model.VoiceOut
import com.kidor.vigik.utils.NetworkHelper
import com.kidor.vigik.utils.NetworkResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Presets for fast availability submission.
 */
enum class DispoPreset {
    /** 21:30 -> 01:00 at 100%. */
    PRESENT_TONIGHT,
    /** 20:00 -> 03:00 at 0%. */
    ABSENT_TONIGHT,
    /** Clear availability. */
    CLEAR
}

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
    suspend fun applyPreset(date: String, preset: DispoPreset): NetworkResult<DayOut>
}

/**
 * Implementation of [ApesRepository].
 */
@Singleton
class ApesRepositoryImpl @Inject constructor(
    private val apesApi: ApesApi,
    private val preferences: DataStore<Preferences>
) : ApesRepository {

    override fun getUserToken(): Flow<String?> = preferences.data.map { pref ->
        pref[PreferencesKeys.APES_USER_TOKEN]
    }

    override suspend fun saveUserToken(token: String) {
        preferences.edit { pref ->
            pref[PreferencesKeys.APES_USER_TOKEN] = token.trim()
        }
    }

    override suspend fun clearUserToken() {
        preferences.edit { pref ->
            pref.remove(PreferencesKeys.APES_USER_TOKEN)
        }
    }

    override suspend fun getVoice(): NetworkResult<VoiceOut> {
        return NetworkHelper.handleApi { apesApi.getVoice() }
    }

    override suspend fun getStatusNow(): NetworkResult<NowOut> {
        return NetworkHelper.handleApi { apesApi.getStatusNow() }
    }

    override suspend fun getAvailability(fromDate: String, toDate: String): NetworkResult<AvailabilityOut> {
        return NetworkHelper.handleApi { apesApi.getAvailability(fromDate, toDate) }
    }

    override suspend fun getMe(token: String): NetworkResult<MeOut> {
        val authHeader = formatBearer(token)
        return NetworkHelper.handleApi { apesApi.getMe(authHeader) }
    }

    override suspend fun applyPreset(date: String, preset: DispoPreset): NetworkResult<DayOut> {
        val token = preferences.data.map { it[PreferencesKeys.APES_USER_TOKEN] }.firstOrNull()
            ?: return NetworkResult.Error(code = 401, errorMessage = "Token non configuré")

        val ranges = when (preset) {
            DispoPreset.PRESENT_TONIGHT -> listOf(
                RangeIn(start = "21:30", end = "01:00", status = 100)
            )
            DispoPreset.ABSENT_TONIGHT -> listOf(
                RangeIn(start = "20:00", end = "03:00", status = 0)
            )
            DispoPreset.CLEAR -> emptyList()
        }

        val authHeader = formatBearer(token)
        return NetworkHelper.handleApi {
            apesApi.putDayAvailability(
                authorization = authHeader,
                date = date,
                body = RangesIn(ranges = ranges)
            )
        }
    }

    private fun formatBearer(token: String): String {
        return if (token.startsWith("Bearer ", ignoreCase = true)) {
            token
        } else {
            "Bearer $token"
        }
    }
}
