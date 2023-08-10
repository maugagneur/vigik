package com.kidor.vigik.ui.notification

import com.kidor.vigik.data.notification.NotificationIcon
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
     * Action to change text content presence in notification.
     *
     * @param addTextContent True if notification should contain a text content, otherwise false.
     */
    data class ChangeTextContentSelection(val addTextContent: Boolean) : NotificationViewAction()

    /**
     * Action to change the notification's content length.
     *
     * @param longContentSelected True if content length should be long, otherwise false.
     */
    data class ChangeContentLength(val longContentSelected: Boolean) : NotificationViewAction()

    /**
     * Action to change picture presence in notification.
     *
     * @param addPicture True if notification should contain a picture, otherwise false.
     */
    data class ChangePictureSelection(val addPicture: Boolean) : NotificationViewAction()

    /**
     * Action to change loader presence in notification.
     *
     * @param addLoader True if notification should contain a loader, otherwise false.
     */
    data class ChangeLoaderSelection(val addLoader: Boolean) : NotificationViewAction()

    /**
     * Action to generate a new notification.
     */
    data object GenerateNotification : NotificationViewAction()

    /**
     * Action to remove the previous notification generated.
     */
    data object RemovePreviousNotification : NotificationViewAction()
}
