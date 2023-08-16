package com.kidor.vigik.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.kidor.vigik.data.notification.RemoveNotificationFromUiUseCase
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

/**
 * Broadcast receiver listening to notification's action buttons.
 */
@AndroidEntryPoint
class NotificationReceiver : BroadcastReceiver() {

    @Inject lateinit var removeNotificationFromUi: RemoveNotificationFromUiUseCase

    override fun onReceive(context: Context?, intent: Intent?) {
        when (intent?.action) {
            ACTION_REMOVE -> {
                removeNotificationFromUi(intent.getIntExtra(EXTRA_NOTIFICATION_ID, 0))
            }
            ACTION_DO_NOTHING -> {
                // Nothing to do
                Timber.i("Do nothing")
            }
        }
    }

    companion object {
        /**
         * Action to remove a notification.
         *
         * @see EXTRA_NOTIFICATION_ID
         */
        const val ACTION_REMOVE: String = "vigik.notification.action.REMOVE"

        /**
         * Dummy action that do nothing.
         */
        const val ACTION_DO_NOTHING: String = "vigik.notification.action.DO_NOTHING"

        /**
         * Extra data representing the ID of the notification.
         * Used with [ACTION_REMOVE].
         */
        const val EXTRA_NOTIFICATION_ID: String = "vigik.notification.extra.NOTIFICATION_ID"
    }
}
