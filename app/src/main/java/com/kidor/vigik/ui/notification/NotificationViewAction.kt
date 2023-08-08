package com.kidor.vigik.ui.notification

import com.kidor.vigik.ui.base.ViewAction

/**
 * Available actions from notification view.
 */
sealed class NotificationViewAction : ViewAction {

    /**
     * Action to change the notification's icon.
     *
     * @param icon The new icon.
     */
    data class ChangeNotificationIcon(val icon: NotificationIcon) : NotificationViewAction()

    /**
     * Action to generate a new notification.
     */
    data object GenerateNotification : NotificationViewAction()
}
