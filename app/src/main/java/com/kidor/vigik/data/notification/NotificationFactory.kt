package com.kidor.vigik.data.notification

import android.app.Notification
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.annotation.DrawableRes
import androidx.core.app.NotificationCompat
import com.kidor.vigik.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

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
     */
    fun buildNotification(@DrawableRes icon: Int, title: String, content: String?, addPicture: Boolean): Notification {
        val builder = NotificationCompat.Builder(context, context.getString(R.string.notification_default_channel_id))
            .setSmallIcon(icon)
            .setContentTitle(title)
            .setContentText(content)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

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

        return builder.build()
    }
}
