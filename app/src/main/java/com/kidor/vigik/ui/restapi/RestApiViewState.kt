package com.kidor.vigik.ui.restapi

import androidx.compose.runtime.Stable
import com.kidor.vigik.data.apes.DEFAULT_PRESET_END_TIME
import com.kidor.vigik.data.apes.DEFAULT_PRESET_START_TIME
import com.kidor.vigik.data.apes.model.AvailabilityOut
import com.kidor.vigik.data.apes.model.MeOut
import com.kidor.vigik.data.apes.model.NowOut
import com.kidor.vigik.data.apes.model.VoiceOut
import com.kidor.vigik.ui.base.ViewState

/**
 * View state for REST API (APES) screen.
 *
 * @param isRefreshing       True if pull-to-refresh is in progress.
 * @param isLoading          True if initial load is in progress.
 * @param voiceData          Voice channel members data.
 * @param availabilityData   Group availability slots and statuses.
 * @param nowData            Members status right now.
 * @param userToken          Stored personal API token.
 * @param meData             Authenticated user profile info.
 * @param isSubmittingPreset True if preset update is in progress.
 * @param presetStartTime    Start time of PRESENT_TONIGHT preset ("HH:mm").
 * @param presetEndTime      End time of PRESENT_TONIGHT preset ("HH:mm").
 * @param userMessage        Informational message (e.g. success).
 * @param errorMessage       Error message to display.
 */
@Stable
data class RestApiViewState(
    val isRefreshing: Boolean = false,
    val isLoading: Boolean = false,
    val voiceData: VoiceOut? = null,
    val availabilityData: AvailabilityOut? = null,
    val nowData: NowOut? = null,
    val userToken: String? = null,
    val meData: MeOut? = null,
    val isSubmittingPreset: Boolean = false,
    val presetStartTime: String = DEFAULT_PRESET_START_TIME,
    val presetEndTime: String = DEFAULT_PRESET_END_TIME,
    val userMessage: String? = null,
    val errorMessage: String? = null
) : ViewState
