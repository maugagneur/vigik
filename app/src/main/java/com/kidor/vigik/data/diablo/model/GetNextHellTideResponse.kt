package com.kidor.vigik.data.diablo.model

/**
 * Model object for network response when requesting next hell tide data.
 *
 * @param time The time until hell tide's spawn.
 */
data class GetNextHellTideResponse(val time: Int?)
