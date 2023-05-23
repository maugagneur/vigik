package com.kidor.vigik.ui.usecases

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Transforms a timestamp into readable date.
 */
class FormatDateUseCase {

    private val formatter = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.US)

    /**
     * Executes this use case.
     *
     * @param timestamp The timestamp to transform.
     */
    operator fun invoke(timestamp: Long?): String {
        return if (timestamp != null) {
            formatter.format(Date(timestamp))
        } else {
            "Invalid date"
        }
    }
}
