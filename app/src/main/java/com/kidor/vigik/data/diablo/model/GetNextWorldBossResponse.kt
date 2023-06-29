package com.kidor.vigik.data.diablo.model

/**
 * Model object for network response when requesting next world boss data.
 *
 * @param name The world boss's name.
 * @param time The time until world boss's spawn.
 */
data class GetNextWorldBossResponse(val name: String?, val time: Int?)
