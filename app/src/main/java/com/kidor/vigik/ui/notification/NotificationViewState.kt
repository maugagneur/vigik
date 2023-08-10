package com.kidor.vigik.ui.notification

import com.kidor.vigik.data.notification.NotificationIcon
import com.kidor.vigik.ui.base.ViewState

/**
 * State of the notification view.
 *
 * @param notificationIcon    The selected notification's icon.
 * @param longContentSelected True if notification's content should be long, otherwise false.
 * @param pictureSelected     True if notification's content should have a picture, otherwise false.
 */
data class NotificationViewState(
    val notificationIcon: NotificationIcon = NotificationIcon.DEFAULT,
    val longContentSelected: Boolean = false,
    val pictureSelected: Boolean = false
) : ViewState
