package com.kidor.vigik.ui.widget

import com.kidor.vigik.data.diablo.model.Diablo4WorldBoss
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private const val DATE_PATTERN = "EEE d MMM \'at\' h:mm a"

/**
 * Data to display on Diablo IV widget.
 *
 * @param worldBoss The world boss data.
 * @param spawnDate The world boss spawn date.
 */
data class DiabloWidgetData(val worldBoss: Diablo4WorldBoss, val spawnDate: Date) {

    /**
     * Formats and returns the spawn date as a String in format "".
     */
    fun getFormattedDate(): String {
        val dateFormat = SimpleDateFormat(DATE_PATTERN, Locale.getDefault())
        return dateFormat
            .format(spawnDate)
            .replaceFirstChar { firstChar ->
                firstChar.uppercaseChar()
            }
    }
}
