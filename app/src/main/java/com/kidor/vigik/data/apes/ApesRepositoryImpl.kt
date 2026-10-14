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
import java.net.HttpURLConnection
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of [ApesRepository].
 */
@Singleton
class ApesRepositoryImpl @Inject constructor(
    private val apesApi: ApesApi,
    private val preferences: DataStore<Preferences>
) : ApesRepository {

    override fun getUserToken(): Flow<String?> = preferences.data.map { preferences ->
        preferences[PreferencesKeys.APES_USER_TOKEN]
    }

    override suspend fun saveUserToken(token: String) {
        preferences.edit { preferences ->
            preferences[PreferencesKeys.APES_USER_TOKEN] = token.trim()
        }
    }

    override suspend fun clearUserToken() {
        preferences.edit { preferences ->
            preferences.remove(PreferencesKeys.APES_USER_TOKEN)
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

    override suspend fun applyPreset(date: String, preset: AvailabilityPreset): NetworkResult<DayOut> {
        val token = getUserToken().firstOrNull()
            ?: return NetworkResult.Error(code = HttpURLConnection.HTTP_UNAUTHORIZED, errorMessage = "Token non configuré")

        val startTime = preferences.data.map { it[PreferencesKeys.APES_PRESET_START_TIME] ?: DEFAULT_PRESET_START_TIME }
            .firstOrNull() ?: DEFAULT_PRESET_START_TIME
        val endTime = preferences.data.map { it[PreferencesKeys.APES_PRESET_END_TIME] ?: DEFAULT_PRESET_END_TIME }
            .firstOrNull() ?: DEFAULT_PRESET_END_TIME

        val ranges = when (preset) {
            AvailabilityPreset.PRESENT_TONIGHT -> listOf(
                RangeIn(start = startTime, end = endTime, status = 100)
            )
            AvailabilityPreset.ABSENT_TONIGHT -> listOf(
                RangeIn(start = "20:00", end = "03:00", status = 0)
            )
            AvailabilityPreset.CLEAR -> emptyList()
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

    override fun getPresetStartTime(): Flow<String> {
        return preferences.data.map { preferences ->
            preferences[PreferencesKeys.APES_PRESET_START_TIME] ?: DEFAULT_PRESET_START_TIME
        }
    }

    override fun getPresetEndTime(): Flow<String> {
        return preferences.data.map { preferences ->
            preferences[PreferencesKeys.APES_PRESET_END_TIME] ?: DEFAULT_PRESET_END_TIME
        }
    }

    override suspend fun savePresetTimes(startTime: String, endTime: String) {
        preferences.edit { preferences ->
            preferences[PreferencesKeys.APES_PRESET_START_TIME] = startTime
            preferences[PreferencesKeys.APES_PRESET_END_TIME] = endTime
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
