package com.kidor.vigik.data.notification

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.annotation.DrawableRes
import androidx.core.app.NotificationCompat
import androidx.core.app.TaskStackBuilder
import androidx.core.net.toUri
import com.kidor.vigik.R
import com.kidor.vigik.receivers.NotificationReceiver
import com.kidor.vigik.ui.MainActivity
import com.kidor.vigik.ui.compose.AppScreen
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

private const val PROGRESS_MAX_VALUE = 100

/**
 * Builds a [Notification] based on given parameters.
 */
class NotificationFactory @Inject constructor(@ApplicationContext private val context: Context) {

    /**
     * Builds a notification with given parameters.
     *
     * @param notificationId The unique ID of the notification.
     * @param icon           The notification's icon.
     * @param content        The notification's text content. Can be null.
     * @param addPicture     True to add a picture to the notification.
     * @param progress       The notification's progress value. Will display an indeterminate progress bar if given
     *                       value is negative. Can be null to hide it.
     * @param addActions     True to add action buttons to the notification.
     */
    @Suppress("LongParameterList")
    fun buildNotification(
        notificationId: Int,
        @DrawableRes icon: Int,
        content: String?,
        addPicture: Boolean,
        progress: Int?,
        addActions: Boolean
    ): Notification {
        val builder = NotificationCompat.Builder(context, context.getString(R.string.notification_default_channel_id))
            .setSmallIcon(icon)
            .setContentTitle(context.getString(R.string.notification_generated_notification_title))
            .setContentText(content)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        createContentIntent()?.let {
            builder.setContentIntent(it)
        }

        if (addPicture) {
            val image = BitmapFactory.decodeResource(context.resources, R.drawable.notification_big_picture)
            builder
                .setLargeIcon(image)
                .setStyle(
                    NotificationCompat
                        .BigPictureStyle()
                        .bigPicture(image)
                        // Hide small picture when notification is expanded
                        .bigLargeIcon(null as Bitmap?)
                )
        }

        if (progress != null) {
            if (progress >= 0) {
                builder.setProgress(PROGRESS_MAX_VALUE, progress, false)
            } else {
                builder.setProgress(0, 0, true)
            }
        }

        if (addActions) {
            // A notification can offer up to three action buttons.
            // Notification actions do not present with icons since Nougat (API 24).
            builder.addAction(
                NotificationCompat.Action.Builder(
                    null,
                    context.getString(R.string.notification_action_button_1_label),
                    createRemoveNotificationIntent(notificationId)
                ).build()
            )
            builder.addAction(
                NotificationCompat.Action.Builder(
                    null,
                    context.getString(R.string.notification_action_button_2_label),
                    createDoNothingNotificationIntent(notificationId)
                ).build()
            )
            builder.addAction(
                NotificationCompat.Action.Builder(
                    null,
                    context.getString(R.string.notification_action_button_3_label),
                    null
                ).build()
            )
        }

        return builder.build()
    }

    /**
     * Create the [PendingIntent] that will be send when clicking on a notification.
     */
    private fun createContentIntent(): PendingIntent? {
        val intent = Intent(
            Intent.ACTION_VIEW,
            AppScreen.NotificationScreen.deeplinkPath.toUri(),
            context,
            MainActivity::class.java
        )
        return TaskStackBuilder.create(context).run {
            addNextIntentWithParentStack(intent)
            getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        }
    }

    /**
     * Create the [PendingIntent] that will remove the notification from the UI by clicking on related action button.
     *
     * @param notificationId The unique ID of the notification.
     */
    private fun createRemoveNotificationIntent(notificationId: Int): PendingIntent {
        val intent = Intent().apply {
            action = NotificationReceiver.ACTION_REMOVE
            putExtra(NotificationReceiver.EXTRA_NOTIFICATION_ID, notificationId)
        }
        return buildPendingIntent(intent, notificationId, PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE)
    }

    /**
     * Create the [PendingIntent] that will do nothing. ¯\_(ツ)_/¯
     *
     * @param notificationId The unique ID of the notification.
     */
    private fun createDoNothingNotificationIntent(notificationId: Int): PendingIntent {
        val intent = Intent().apply {
            action = NotificationReceiver.ACTION_DO_NOTHING
        }
        return buildPendingIntent(intent, notificationId, PendingIntent.FLAG_IMMUTABLE)
    }

    // When using PendingIntent we must provide a request code that is different for each conversation or provide an
    // intent that doesn't return true when you call equals(). Here the notification ID is passed as part of the
    // intent's extras bundle, but is ignored when you call equals()... (╯°□°）╯︵ ┻━┻
    private fun buildPendingIntent(intent: Intent, notificationId: Int, flags: Int): PendingIntent =
        PendingIntent.getBroadcast(
            context,
            notificationId,
            intent,
            flags
        )
}
