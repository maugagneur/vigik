package com.kidor.vigik.data.notification

import android.app.NotificationManager
import javax.inject.Inject

/**
 * Removes a notification from UI.
 */
class RemoveNotificationFromUiUseCase @Inject constructor(
    private val notificationManager: NotificationManager
) {

    /**
     * Executes this use case.
     *
     * @param notificationId The ID of the notification to remove.
     */
    operator fun invoke(notificationId: Int) {
        notificationManager.cancel(notificationId)
    }
}
