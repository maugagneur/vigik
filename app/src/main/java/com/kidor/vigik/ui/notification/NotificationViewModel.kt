package com.kidor.vigik.ui.notification

import android.app.NotificationManager
import com.kidor.vigik.R
import com.kidor.vigik.data.Localization
import com.kidor.vigik.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlin.random.Random

/**
 * Business logic of notification screen.
 */
@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val localization: Localization,
    private val buildNotification: BuildNotificationUseCase,
    private val notificationManager: NotificationManager
) : BaseViewModel<NotificationViewAction, Nothing, Nothing>() {

    private var lastNotificationIdGenerated: Int? = null

    override fun handleAction(viewAction: NotificationViewAction) {
        when (viewAction) {
            is NotificationViewAction.GenerateNotification -> {
                // Build notification
                val notification = buildNotification(
                    icon = R.drawable.ic_notifications,
                    title = localization.getString(R.string.notification_generated_notification_title),
                    content = localization.getString(R.string.notification_generated_notification_short_content)
                )
                // Show notification
                Random.nextInt().let { notificationId ->
                    lastNotificationIdGenerated = notificationId
                    notificationManager.notify(notificationId, notification)
                }
            }
        }
    }
}
