package com.kidor.vigik.ui.notification

import android.app.NotificationManager
import androidx.lifecycle.viewModelScope
import com.kidor.vigik.R
import com.kidor.vigik.data.Localization
import com.kidor.vigik.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
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
) : BaseViewModel<NotificationViewAction, NotificationViewState, Nothing>() {

    private var generatedNotificationIds: MutableList<Int> = mutableListOf()

    init {
        // Emit view state with default values at start
        _viewState.value = NotificationViewState()
    }

    override fun handleAction(viewAction: NotificationViewAction) {
        when (viewAction) {
            is NotificationViewAction.ChangeNotificationIcon -> {
                updateViewState { it.copy(notificationIcon = viewAction.icon) }
            }

            is NotificationViewAction.ChangeContentLength -> {
                updateViewState { it.copy(longContentSelected = viewAction.longContentSelected) }
            }

            is NotificationViewAction.ChangePictureSelection -> {
                updateViewState { it.copy(pictureSelected = viewAction.addPicture) }
            }

            is NotificationViewAction.GenerateNotification -> {
                // Build notification based on current view state
                viewState.value?.let { currentState ->
                    val notification = buildNotification(
                        icon = currentState.notificationIcon.drawableId,
                        title = localization.getString(R.string.notification_generated_notification_title),
                        content = if (currentState.longContentSelected) {
                            localization.getString(R.string.notification_generated_notification_long_content)
                        } else {
                            localization.getString(R.string.notification_generated_notification_short_content)
                        },
                        addPicture = currentState.pictureSelected
                    )

                    // Show notification
                    Random.nextInt().let { notificationId ->
                        generatedNotificationIds.add(notificationId)
                        notificationManager.notify(notificationId, notification)
                    }
                }
            }

            is NotificationViewAction.RemovePreviousNotification -> {
                generatedNotificationIds.lastOrNull()?.let {
                    notificationManager.cancel(it)
                    generatedNotificationIds.removeLast()
                } ?: Timber.d("No more notification to remove")
            }
        }
    }

    /**
     * Update the current view state.
     *
     * @param update The operation to perform on view state.
     */
    private fun updateViewState(update: (NotificationViewState) -> NotificationViewState) {
        viewModelScope.launch {
            _viewState.value = update(viewState.value ?: NotificationViewState())
        }
    }
}
