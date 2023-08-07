package com.kidor.vigik.ui.notification

import android.app.Notification
import android.content.Context
import androidx.annotation.DrawableRes
import androidx.core.app.NotificationCompat
import com.kidor.vigik.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

/**
 * Builds a [Notification] based on given parameters.
 */
class BuildNotificationUseCase @Inject constructor(
    @ApplicationContext private val context: Context
) {

    /**
     * Executes the use case.
     *
     * @param icon    The notification's icon.
     * @param title   The notification's title.
     * @param content The notification's content.
     */
    operator fun invoke(@DrawableRes icon: Int, title: String, content: String): Notification =
        NotificationCompat.Builder(context, context.getString(R.string.notification_default_channel_id))
            .setSmallIcon(icon)
            .setContentTitle(title)
            .setContentText(content)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()
}
