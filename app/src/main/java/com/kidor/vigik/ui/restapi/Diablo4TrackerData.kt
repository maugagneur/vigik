package com.kidor.vigik.ui.restapi

import com.kidor.vigik.data.diablo.model.Diablo4WorldBoss
import com.kidor.vigik.ui.base.ViewState

/**
 * Model of data gets from Diablo IV tracker.
 *
 * @param nextBoss              The next world boss to spawn.
 * @param timeUntilNextBoss     Readable time until next boss.
 * @param timeUntilNextHellTide Readable time until next hell tide.
 */
data class Diablo4TrackerData(
    val nextBoss: Diablo4WorldBoss? = null,
    val timeUntilNextBoss: String? = null,
    val timeUntilNextHellTide: String? = null
) : ViewState()
