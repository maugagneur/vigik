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
     * Builds a notification with an icon, a title, a text content (optional) and a picture (optional).
     *
     * @param icon       The notification's icon.
     * @param title      The notification's title.
     * @param content    The notification's text content. Can be null.
     * @param addPicture True to add a picture to the notification.
     * @param progress   The notification's progress value. Will display an indeterminate progress bar if given value
     *                   is negative. Can be null to hide it.
     */
    fun buildNotification(
        @DrawableRes icon: Int,
        title: String,
        content: String?,
        addPicture: Boolean,
        progress: Int?
    ): Notification {
        val builder = NotificationCompat.Builder(context, context.getString(R.string.notification_default_channel_id))
            .setSmallIcon(icon)
            .setContentTitle(title)
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
}
