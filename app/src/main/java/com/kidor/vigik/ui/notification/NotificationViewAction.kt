package com.kidor.vigik.ui.notification

import com.kidor.vigik.ui.base.ViewAction

/**
 * Available actions from notification view.
 */
sealed class NotificationViewAction : ViewAction {

    /**
     * Action to generate a new notification.
     */
    data object GenerateNotification : NotificationViewAction()
}
