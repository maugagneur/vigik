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
     * Action to change the notification's content length.
     *
     * @param longContentSelected True if content length should be long, otherwise false.
     */
    data class ChangeContentLength(val longContentSelected: Boolean) : NotificationViewAction()

    /**
     * Action to generate a new notification.
     */
    data object GenerateNotification : NotificationViewAction()

    /**
     * Action to remove the previous notification generated.
     */
    data object RemovePreviousNotification : NotificationViewAction()
}
