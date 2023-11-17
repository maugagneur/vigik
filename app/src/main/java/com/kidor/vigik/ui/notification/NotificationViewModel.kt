package com.kidor.vigik.ui.notification

import android.app.NotificationManager
import androidx.lifecycle.viewModelScope
import com.kidor.vigik.R
import com.kidor.vigik.data.Localization
import com.kidor.vigik.data.notification.NotificationFactory
import com.kidor.vigik.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import kotlin.random.Random

private const val PROGRESS_MAX_VALUE = 100
private const val SECOND_TO_MILLI = 1_000L

/**
 * Business logic of notification screen.
 */
@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val localization: Localization,
    private val notificationFactory: NotificationFactory,
    private val notificationManager: NotificationManager
) : BaseViewModel<NotificationViewAction, NotificationViewState, Nothing>() {

    private val generatedNotificationIds: MutableSet<Int> = mutableSetOf()

    init {
        // Emit view state with default values at start
        _viewState.value = NotificationViewState()
    }

    override fun handleAction(viewAction: NotificationViewAction) {
        when (viewAction) {
            is NotificationViewAction.ChangeNotificationIcon -> {
                updateViewState { it.copy(notificationIcon = viewAction.icon) }
            }

            is NotificationViewAction.ChangeTextContentSelection -> {
                updateViewState { it.copy(addTextContentSelected = viewAction.addTextContent) }
            }

            is NotificationViewAction.ChangeContentLength -> {
                updateViewState { it.copy(longTextContentSelected = viewAction.longContentSelected) }
            }

            is NotificationViewAction.ChangePictureSelection -> {
                updateViewState { it.copy(addPictureSelected = viewAction.addPicture) }
            }

            is NotificationViewAction.ChangeLoaderSelection -> {
                updateViewState { it.copy(addLoaderSelected = viewAction.addLoader) }
            }

            is NotificationViewAction.ChangeInfiniteLoaderSelection -> {
                updateViewState { it.copy(infiniteLoaderSelected = viewAction.infiniteLoaderSelected) }
            }

            is NotificationViewAction.ChangeActionButtonsSelection -> {
                updateViewState { it.copy(addActionButtons = viewAction.actionButtonsSelected) }
            }

            is NotificationViewAction.GenerateNotification -> {
                generateNotification()
            }

            is NotificationViewAction.RemovePreviousNotification -> {
                generatedNotificationIds.lastOrNull()?.let {
                    notificationManager.cancel(it)
                    generatedNotificationIds.remove(it)
                } ?: Timber.d("No more notification to remove")
            }
        }
    }

    /**
     * Generates a notification based on current view state.
     */
    private fun generateNotification() {
        // Generate new notification ID
        val notificationId = Random.nextInt()

        // Build notification based on current view state
        viewState.value?.let { currentState ->
            if (currentState.addLoaderSelected && !currentState.infiniteLoaderSelected) {
                emitPeriodicNotification(currentState, notificationId)
            } else {
                emitSingleNotification(currentState, notificationId)
            }
        }
    }

    /**
     * Generates and emits a notification based on current view state then updates it every seconds with a new progress
     * value. After that, do a last update without the progress bar but the selected content.
     *
     * @param viewState      The current state of the view.
     * @param notificationId The ID of the notification.
     */
    private fun emitPeriodicNotification(viewState: NotificationViewState, notificationId: Int) {
        // Add the notification ID in the list of emitted notifications
        generatedNotificationIds.add(notificationId)

        viewModelScope.launch {
            for (progress in 0 until PROGRESS_MAX_VALUE) {
                // If the notification ID has been removed it means that the notification has been canceled -> abort
                // periodic updates.
                if (!generatedNotificationIds.contains(notificationId)) break

                val notification = generateNotification(notificationId, viewState, progress)

                notificationManager.notify(notificationId, notification)

                delay(SECOND_TO_MILLI)
            }

            // If the notification was not cancelled, make a last update without the progress bar so that text content
            // can be shown.
            if (generatedNotificationIds.contains(notificationId)) {
                notificationManager.notify(notificationId, generateNotification(notificationId, viewState))
            }
        }
    }

    /**
     * Generates and emits a single notification based on current view state.
     *
     * @param viewState      The current state of the view.
     * @param notificationId The ID of the notification.
     */
    private fun emitSingleNotification(viewState: NotificationViewState, notificationId: Int) {
        // Add the notification ID in the list of emitted notifications
        generatedNotificationIds.add(notificationId)

        if (viewState.addLoaderSelected && viewState.infiniteLoaderSelected) {
            notificationManager.notify(notificationId, generateNotification(notificationId, viewState, -1))
        } else {
            notificationManager.notify(notificationId, generateNotification(notificationId, viewState))
        }
    }

    /**
     * Generates a notification based on given view state and a progress value.
     *
     * @param notificationId The unique ID of the notification.
     * @param viewState      The current state of the view.
     * @param progress       The notification loader's progress value.
     */
    private fun generateNotification(
        notificationId: Int,
        viewState: NotificationViewState,
        progress: Int? = null
    ) = notificationFactory
        .buildNotification(
            notificationId = notificationId,
            icon = viewState.notificationIcon.drawableId,
            content = when {
                !viewState.addTextContentSelected -> null
                viewState.longTextContentSelected ->
                    localization.getString(R.string.notification_generated_notification_long_content)

                else -> localization.getString(R.string.notification_generated_notification_short_content)
            },
            addPicture = viewState.addPictureSelected,
            progress = progress,
            addActions = viewState.addActionButtons
        )
}
