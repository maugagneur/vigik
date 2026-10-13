package com.kidor.vigik.ui.widget

/**
 * Data displayed by APES widget.
 *
 * @param voiceCount     Number of members currently in voice channel.
 * @param voiceMembers   Comma-separated names of members in voice channel.
 * @param eveningSummary Summary text for this evening's availability.
 */
data class ApesWidgetData(
    val voiceCount: Int = 0,
    val voiceMembers: String = "",
    val eveningSummary: String = ""
)
