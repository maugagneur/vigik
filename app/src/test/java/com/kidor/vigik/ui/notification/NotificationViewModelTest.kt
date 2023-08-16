package com.kidor.vigik.ui.notification

import android.app.Notification
import android.app.NotificationManager
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.kidor.vigik.MainCoroutineRule
import com.kidor.vigik.R
import com.kidor.vigik.data.Localization
import com.kidor.vigik.data.notification.NotificationFactory
import com.kidor.vigik.data.notification.NotificationIcon
import com.kidor.vigik.data.notification.RemoveNotificationFromUiUseCase
import com.kidor.vigik.utils.AssertUtils.assertEquals
import com.kidor.vigik.utils.AssertUtils.assertFalse
import com.kidor.vigik.utils.AssertUtils.assertTrue
import com.kidor.vigik.utils.TestUtils.logTestName
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Unit test for [NotificationViewModel].
 */
class NotificationViewModelTest {

    @ExperimentalCoroutinesApi
    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: NotificationViewModel

    @MockK
    private lateinit var localization: Localization
    @MockK
    private lateinit var notificationFactory: NotificationFactory
    @MockK
    private lateinit var notification: Notification
    @MockK
    private lateinit var notificationManager: NotificationManager
    @MockK
    private lateinit var removeNotificationFromUiUseCase: RemoveNotificationFromUiUseCase

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        viewModel = NotificationViewModel(
            localization = localization,
            notificationFactory = notificationFactory,
            notificationManager = notificationManager,
            removeNotificationFromUi = removeNotificationFromUiUseCase
        )
    }

    @Test
    fun `test view initial state`() {
        logTestName()

        val viewState = viewModel.viewState.value

        assertEquals(NotificationIcon.DEFAULT, viewState?.notificationIcon, "Notification icon")
        assertFalse(viewState?.addTextContentSelected, "Add text content")
        assertFalse(viewState?.longTextContentSelected, "Long text content")
        assertFalse(viewState?.addPictureSelected, "Add picture content")
        assertFalse(viewState?.addLoaderSelected, "Add loader content")
        assertFalse(viewState?.infiniteLoaderSelected, "Infinite loader")
        assertFalse(viewState?.addActionButtons, "Add action buttons")
    }

    @Test
    fun `test updating notification icon`() {
        logTestName()

        // Check that initial notification icon is 'DEFAULT'
        var viewState = viewModel.viewState.value
        assertEquals(NotificationIcon.DEFAULT, viewState?.notificationIcon, "Notification icon")

        // Change notification icon to 'TIPS_AND_UPDATES'
        viewModel.handleAction(NotificationViewAction.ChangeNotificationIcon(icon = NotificationIcon.TIPS_AND_UPDATES))

        // Check that notification icon is 'TIPS_AND_UPDATES'
        viewState = viewModel.viewState.value
        assertEquals(NotificationIcon.TIPS_AND_UPDATES, viewState?.notificationIcon, "Notification icon")

        // Change notification icon to 'ROCKET_LAUNCH'
        viewModel.handleAction(NotificationViewAction.ChangeNotificationIcon(icon = NotificationIcon.ROCKET_LAUNCH))

        // Check that notification icon is 'ROCKET_LAUNCH'
        viewState = viewModel.viewState.value
        assertEquals(NotificationIcon.ROCKET_LAUNCH, viewState?.notificationIcon, "Notification icon")
    }

    @Test
    fun `test changing text content selection`() {
        logTestName()

        // Check that text content is unselected
        var viewState = viewModel.viewState.value
        assertFalse(viewState?.addTextContentSelected, "Add text content")

        // Select text content
        viewModel.handleAction(NotificationViewAction.ChangeTextContentSelection(true))

        // Check that text content is selected
        viewState = viewModel.viewState.value
        assertTrue(viewState?.addTextContentSelected, "Add text content")

        // Unselect text content
        viewModel.handleAction(NotificationViewAction.ChangeTextContentSelection(false))

        // Check that text content is unselected
        viewState = viewModel.viewState.value
        assertFalse(viewState?.addTextContentSelected, "Add text content")
    }

    @Test
    fun `test changing long text content selection`() {
        logTestName()

        // Check that long text content is unselected
        var viewState = viewModel.viewState.value
        assertFalse(viewState?.longTextContentSelected, "Long text content")

        // Select long text content
        viewModel.handleAction(NotificationViewAction.ChangeContentLength(true))

        // Check that long text content is selected
        viewState = viewModel.viewState.value
        assertTrue(viewState?.longTextContentSelected, "Long text content")

        // Unselect long text content
        viewModel.handleAction(NotificationViewAction.ChangeContentLength(false))

        // Check that long text content is unselected
        viewState = viewModel.viewState.value
        assertFalse(viewState?.longTextContentSelected, "Long text content")
    }

    @Test
    fun `test changing add picture content selection`() {
        logTestName()

        // Check that picture content is unselected
        var viewState = viewModel.viewState.value
        assertFalse(viewState?.addPictureSelected, "Add picture content")

        // Select picture content
        viewModel.handleAction(NotificationViewAction.ChangePictureSelection(true))

        // Check that picture content is selected
        viewState = viewModel.viewState.value
        assertTrue(viewState?.addPictureSelected, "Add picture content")

        // Unselect picture content
        viewModel.handleAction(NotificationViewAction.ChangePictureSelection(false))

        // Check that picture content is unselected
        viewState = viewModel.viewState.value
        assertFalse(viewState?.addPictureSelected, "Add picture content")
    }

    @Test
    fun `test changing add loader content selection`() {
        logTestName()

        // Check that loader content is unselected
        var viewState = viewModel.viewState.value
        assertFalse(viewState?.addLoaderSelected, "Add loader content")

        // Select loader content
        viewModel.handleAction(NotificationViewAction.ChangeLoaderSelection(true))

        // Check that loader content is selected
        viewState = viewModel.viewState.value
        assertTrue(viewState?.addLoaderSelected, "Add loader content")

        // Unselect loader content
        viewModel.handleAction(NotificationViewAction.ChangeLoaderSelection(false))

        // Check that loader content is unselected
        viewState = viewModel.viewState.value
        assertFalse(viewState?.addLoaderSelected, "Add loader content")
    }

    @Test
    fun `test changing add infinite loader selection`() {
        logTestName()

        // Check that infinite loader is unselected
        var viewState = viewModel.viewState.value
        assertFalse(viewState?.infiniteLoaderSelected, "Infinite loader")

        // Select infinite loader content
        viewModel.handleAction(NotificationViewAction.ChangeInfiniteLoaderSelection(true))

        // Check that infinite loader is selected
        viewState = viewModel.viewState.value
        assertTrue(viewState?.infiniteLoaderSelected, "Infinite loader")

        // Unselect infinite loader content
        viewModel.handleAction(NotificationViewAction.ChangeInfiniteLoaderSelection(false))

        // Check that infinite loader is unselected
        viewState = viewModel.viewState.value
        assertFalse(viewState?.infiniteLoaderSelected, "Infinite loader")
    }

    @Test
    fun `test changing add action buttons selection`() {
        logTestName()

        // Check that action buttons is unselected
        var viewState = viewModel.viewState.value
        assertFalse(viewState?.addActionButtons, "Add action buttons")

        // Select action buttons
        viewModel.handleAction(NotificationViewAction.ChangeActionButtonsSelection(true))

        // Check that action buttons is selected
        viewState = viewModel.viewState.value
        assertTrue(viewState?.addActionButtons, "Add action buttons")

        // Unselect action buttons
        viewModel.handleAction(NotificationViewAction.ChangeActionButtonsSelection(false))

        // Check that action buttons is unselected
        viewState = viewModel.viewState.value
        assertFalse(viewState?.addActionButtons, "Add action buttons")
    }

    @Test
    fun `test generate default notification`() {
        logTestName()

        val notificationId = slot<Int>()
        every { notificationFactory.buildNotification(
            notificationId = capture(notificationId),
            icon = any(),
            content = any(),
            addPicture = any(),
            progress = any(),
            addActions = any()
        ) } returns notification

        // Generate a notification
        viewModel.handleAction(NotificationViewAction.GenerateNotification)

        // Check that notification was built with the right parameters
        verify { notificationFactory.buildNotification(
            notificationId = any(),
            icon = NotificationIcon.DEFAULT.drawableId,
            content = null,
            addPicture = false,
            progress = null,
            addActions = false
        ) }
        // Check that notification is displayed
        verify { notificationManager.notify(notificationId.captured, notification) }
    }

    @Test
    fun `test generate custom notification with infinite loader`() {
        logTestName()

        val notificationId = slot<Int>()
        every { notificationFactory.buildNotification(
            notificationId = capture(notificationId),
            icon = any(),
            content = any(),
            addPicture = any(),
            progress = any(),
            addActions = any()
        ) } returns notification
        val longTextContent = "Long text content"
        every { localization.getString(R.string.notification_generated_notification_long_content) } returns longTextContent

        // Change notification icon
        viewModel.handleAction(NotificationViewAction.ChangeNotificationIcon(NotificationIcon.TIPS_AND_UPDATES))
        // Add text content
        viewModel.handleAction(NotificationViewAction.ChangeTextContentSelection(true))
        // Set text long content
        viewModel.handleAction(NotificationViewAction.ChangeContentLength(true))
        // Add a picture
        viewModel.handleAction(NotificationViewAction.ChangePictureSelection(true))
        // Add a loader
        viewModel.handleAction(NotificationViewAction.ChangeLoaderSelection(true))
        // Set loader to infinite mode
        viewModel.handleAction(NotificationViewAction.ChangeInfiniteLoaderSelection(true))
        // Add action buttons
        viewModel.handleAction(NotificationViewAction.ChangeActionButtonsSelection(true))

        // Generate a notification
        viewModel.handleAction(NotificationViewAction.GenerateNotification)

        // Check that notification was built with the right parameters
        verify { notificationFactory.buildNotification(
            notificationId = any(),
            icon = NotificationIcon.TIPS_AND_UPDATES.drawableId,
            content = longTextContent,
            addPicture = true,
            progress = -1,
            addActions = true
        ) }
        // Check that notification is displayed
        verify { notificationManager.notify(notificationId.captured, notification) }
    }

    @Test
    fun `test generate custom notification with loader`() {
        logTestName()

        val notificationId = slot<Int>()
        every { notificationFactory.buildNotification(
            notificationId = capture(notificationId),
            icon = any(),
            content = any(),
            addPicture = any(),
            progress = any(),
            addActions = any()
        ) } returns notification
        val shortTextContent = "Short text content"
        every { localization.getString(R.string.notification_generated_notification_short_content) } returns shortTextContent

        // Change notification icon
        viewModel.handleAction(NotificationViewAction.ChangeNotificationIcon(NotificationIcon.ROCKET_LAUNCH))
        // Add text content
        viewModel.handleAction(NotificationViewAction.ChangeTextContentSelection(true))
        // Add a picture
        viewModel.handleAction(NotificationViewAction.ChangePictureSelection(true))
        // Add a loader
        viewModel.handleAction(NotificationViewAction.ChangeLoaderSelection(true))
        // Add action buttons
        viewModel.handleAction(NotificationViewAction.ChangeActionButtonsSelection(true))

        runTest {
            // Generate a notification
            viewModel.handleAction(NotificationViewAction.GenerateNotification)

            // One notification should be emitted every second to update the loader (from 0 to 100%) and a last one to
            // dismiss the loader from the notification. So we need to wait at least 100 seconds to check if all
            // notifications are emitted correctly.
            delay(110_000)
            // Check that notification was built with the right parameters
            verify(exactly = 101) { notificationFactory.buildNotification(
                notificationId = any(),
                icon = NotificationIcon.ROCKET_LAUNCH.drawableId,
                content = shortTextContent,
                addPicture = true,
                progress = any(),
                addActions = true
            ) }
            // Check that the same ID is used to update the notification
            verify(exactly = 101) { notificationManager.notify(notificationId.captured, notification) }
        }
    }

    @Test
    fun `test generated notifications do not have the same ID`() {
        logTestName()

        // Generate 1000 notifications
        val notificationIds = mutableListOf<Int>()
        every { notificationFactory.buildNotification(
            notificationId = capture(notificationIds),
            icon = any(),
            content = any(),
            addPicture = any(),
            progress = any(),
            addActions = any()
        ) } returns notification
        repeat(1000) {
            viewModel.handleAction(NotificationViewAction.GenerateNotification)
        }

        assertEquals(1000, notificationIds.distinct().size, "Distinct IDs")
    }

    @Test
    fun `test generate custom notification with long text content selected but no text content selected`() {
        logTestName()

        val notificationId = slot<Int>()
        every { notificationFactory.buildNotification(
            notificationId = capture(notificationId),
            icon = any(),
            content = any(),
            addPicture = any(),
            progress = any(),
            addActions = any()
        ) } returns notification

        // Unset text content
        viewModel.handleAction(NotificationViewAction.ChangeTextContentSelection(false))
        // Set text long content
        viewModel.handleAction(NotificationViewAction.ChangeContentLength(true))

        // Generate a notification
        viewModel.handleAction(NotificationViewAction.GenerateNotification)

        // Check that notification was built with the right parameters
        verify { notificationFactory.buildNotification(
            notificationId = any(),
            icon = NotificationIcon.DEFAULT.drawableId,
            content = null,
            addPicture = false,
            progress = null,
            addActions = false
        ) }
        // Check that notification is displayed
        verify { notificationManager.notify(notificationId.captured, notification) }
    }

    @Test
    fun `test generate custom notification with infinite loader selected but no loader content selected`() {
        logTestName()

        val notificationId = slot<Int>()
        every { notificationFactory.buildNotification(
            notificationId = capture(notificationId),
            icon = any(),
            content = any(),
            addPicture = any(),
            progress = any(),
            addActions = any()
        ) } returns notification

        // Unset loader content
        viewModel.handleAction(NotificationViewAction.ChangeLoaderSelection(false))
        // Set infinite loader
        viewModel.handleAction(NotificationViewAction.ChangeInfiniteLoaderSelection(true))

        // Generate a notification
        viewModel.handleAction(NotificationViewAction.GenerateNotification)

        // Check that notification was built with the right parameters
        verify { notificationFactory.buildNotification(
            notificationId = any(),
            icon = NotificationIcon.DEFAULT.drawableId,
            content = null,
            addPicture = false,
            progress = null,
            addActions = false
        ) }
        // Check that notification is displayed
        verify { notificationManager.notify(notificationId.captured, notification) }
    }

    @Test
    fun `test remove previous notification`() {
        logTestName()

        // Try to remove previous notification when no notification has been generated before
        viewModel.handleAction(NotificationViewAction.RemovePreviousNotification)

        // Check that remove notification use case is never called
        verify(inverse = true) { removeNotificationFromUiUseCase(any()) }

        // Generate 3 notifications
        val notificationIds = mutableListOf<Int>()
        every { notificationFactory.buildNotification(
            notificationId = capture(notificationIds),
            icon = any(),
            content = any(),
            addPicture = any(),
            progress = any(),
            addActions = any()
        ) } returns notification
        viewModel.handleAction(NotificationViewAction.GenerateNotification)
        viewModel.handleAction(NotificationViewAction.GenerateNotification)
        viewModel.handleAction(NotificationViewAction.GenerateNotification)

        // Remove each notifications one by one
        viewModel.handleAction(NotificationViewAction.RemovePreviousNotification)
        verify { removeNotificationFromUiUseCase(notificationIds[2]) }
        viewModel.handleAction(NotificationViewAction.RemovePreviousNotification)
        verify { removeNotificationFromUiUseCase(notificationIds[1]) }
        viewModel.handleAction(NotificationViewAction.RemovePreviousNotification)
        verify { removeNotificationFromUiUseCase(notificationIds[0]) }

        // Try to remove notification one more time when no more notification should be prompted
        viewModel.handleAction(NotificationViewAction.RemovePreviousNotification)

        // Check that remove notification use case is not called more than 3 times
        verify(exactly = 3) { removeNotificationFromUiUseCase(any()) }
    }
}
