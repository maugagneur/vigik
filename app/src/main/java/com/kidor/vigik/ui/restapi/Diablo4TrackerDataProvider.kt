package com.kidor.vigik.ui.restapi

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.kidor.vigik.data.diablo.model.Diablo4WorldBoss
import com.kidor.vigik.utils.ExcludedFromKoverReport

/**
 * Provides a set of data for the preview of Diablo4Tracker Composable.
 */
@ExcludedFromKoverReport
class Diablo4TrackerDataProvider : PreviewParameterProvider<Diablo4TrackerData> {
    override val values: Sequence<Diablo4TrackerData> = sequenceOf(
        Diablo4TrackerData(),
        Diablo4TrackerData(
            nextBoss = Diablo4WorldBoss.ASHAVA,
            timeUntilNextBoss = "2 h 18 min",
            timeUntilNextHellTide = "35 min"
        ),
        Diablo4TrackerData(
            nextBoss = Diablo4WorldBoss.AVARICE,
            timeUntilNextBoss = "23 min"
        ),
        Diablo4TrackerData(
            nextBoss = Diablo4WorldBoss.WANDERING_DEATH,
            timeUntilNextBoss = null,
            timeUntilNextHellTide = "1 h 02"
        ),
        Diablo4TrackerData(
            nextBoss = Diablo4WorldBoss.UNKNOWN,
            timeUntilNextBoss = "5 h 37",
            timeUntilNextHellTide = null
        )
    )
}
