package com.kidor.vigik.ui.notification

import com.kidor.vigik.data.notification.NotificationIcon
import com.kidor.vigik.ui.base.ViewState

/**
 * State of the notification view.
 *
 * @param notificationIcon        The selected notification's icon.
 * @param addTextContentSelected  True if notification should have text content, otherwise false.
 * @param longTextContentSelected True if notification's content should be long, otherwise false.
 * @param addPictureSelected      True if notification's content should have a picture, otherwise false.
 * @param addLoaderSelected       True if notification's should have a loader, otherwise false.
 * @param infiniteLoaderSelected  True if notification's loader should be infinite, otherwise false.
 * @param addActionButtons        True if notification should have action buttons, otherwise false.
 */
data class NotificationViewState(
    val notificationIcon: NotificationIcon = NotificationIcon.DEFAULT,
    val addTextContentSelected: Boolean = false,
    val longTextContentSelected: Boolean = false,
    val addPictureSelected: Boolean = false,
    val addLoaderSelected: Boolean = false,
    val infiniteLoaderSelected: Boolean = false,
    val addActionButtons: Boolean = false
) : ViewState
