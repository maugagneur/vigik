package com.kidor.vigik.ui.notification

import com.kidor.vigik.ui.base.ViewState

/**
 * State of the notification view.
 *
 * @param notificationIcon The selected notification's icon.
 */
data class NotificationViewState(val notificationIcon: NotificationIcon = NotificationIcon.DEFAULT) : ViewState
