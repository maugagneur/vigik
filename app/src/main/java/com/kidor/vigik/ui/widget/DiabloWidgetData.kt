package com.kidor.vigik.ui.widget

import com.kidor.vigik.data.diablo.model.Diablo4WorldBoss

/**
 * Data to display on Diablo IV widget.
 *
 * @param worldBoss The world boss data.
 * @param spawnDate The world boss spawn date.
 */
data class DiabloWidgetData(val worldBoss: Diablo4WorldBoss, val spawnDate: String)
