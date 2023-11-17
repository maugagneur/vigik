package com.kidor.vigik.data.notification

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.core.app.NotificationCompat
import androidx.core.app.TaskStackBuilder
import com.kidor.vigik.R
import com.kidor.vigik.utils.AssertUtils.assertEquals
import com.kidor.vigik.utils.TestUtils.logTestName
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.mockkStatic
import io.mockk.verify
import org.junit.Before
import org.junit.Test
import kotlin.random.Random
import kotlin.random.nextInt

private const val ACTION_1_TITLE = "REMOVE"
private const val ACTION_2_TITLE = "DO NOTHING"
private const val ACTION_3_TITLE = "DISABLE"

/**
 * Unit tests for [NotificationFactory].
 */
class NotificationFactoryTest {

    private lateinit var notificationFactory: NotificationFactory

    @MockK
    private lateinit var context: Context
    @MockK
    private lateinit var notificationBuilder: NotificationCompat.Builder
    @MockK
    private lateinit var taskStackBuilder: TaskStackBuilder
    @MockK
    private lateinit var pendingIntent: PendingIntent
    @MockK
    private lateinit var notification: Notification

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        every { context.getString(R.string.notification_default_channel_id) } returns "Notification channel ID"
        every { context.getString(R.string.notification_generated_notification_title) } returns "Notification title"
        every { context.getString(R.string.notification_action_button_1_label) } returns ACTION_1_TITLE
        every { context.getString(R.string.notification_action_button_2_label) } returns ACTION_2_TITLE
        every { context.getString(R.string.notification_action_button_3_label) } returns ACTION_3_TITLE
        every { context.resources } returns mockk()
        mockkConstructor(NotificationCompat.Builder::class)
        every { anyConstructed<NotificationCompat.Builder>().setSmallIcon(any<Int>()) } returns notificationBuilder
        every { notificationBuilder.setContentTitle(any()) } returns notificationBuilder
        every { notificationBuilder.setContentText(any()) } returns notificationBuilder
        every { notificationBuilder.setPriority(any()) } returns notificationBuilder
        every { notificationBuilder.setAutoCancel(any()) } returns notificationBuilder
        every { notificationBuilder.setContentIntent(any()) } returns notificationBuilder
        every { notificationBuilder.setLargeIcon(any<Bitmap>()) } returns notificationBuilder
        every { notificationBuilder.setStyle(any()) } returns notificationBuilder
        every { notificationBuilder.setProgress(any(), any(), any()) } returns notificationBuilder
        every { notificationBuilder.addAction(any()) } returns notificationBuilder
        every { notificationBuilder.build() } returns notification
        mockkStatic(TaskStackBuilder::class, BitmapFactory::class, PendingIntent::class)
        every { TaskStackBuilder.create(context) } returns taskStackBuilder
        every { taskStackBuilder.addNextIntentWithParentStack(any()) } returns taskStackBuilder
        every { taskStackBuilder.getPendingIntent(any(), any()) } returns pendingIntent
        every { PendingIntent.getBroadcast(context, any(), any(), any()) } returns mockk()
        notificationFactory = NotificationFactory(context)
    }

    @Test
    fun `test build minimal notification`() {
        logTestName()

        val notificationId = Random.nextInt()
        val icon = R.drawable.ic_notifications

        // Build notification
        notificationFactory.buildNotification(
            notificationId = notificationId,
            icon = icon,
            content = null,
            addPicture = false,
            progress = null,
            addActions = false
        )

        // Notification has a small icon
        verify { anyConstructed<NotificationCompat.Builder>().setSmallIcon(icon) }
        // Notification does not have text content
        verify { notificationBuilder.setContentText(null) }
        // Notification has default priority
        verify { notificationBuilder.priority = NotificationCompat.PRIORITY_DEFAULT }
        // Notification is cancelled on click
        verify { notificationBuilder.setAutoCancel(true) }
        // Notification does not have large icon
        verify(inverse = true) { notificationBuilder.setLargeIcon(any<Bitmap>()) }
        // Notification does not have any progress
        verify(inverse = true) { notificationBuilder.setProgress(any(), any(), any()) }
        // Notification does not have any action
        verify(inverse = true) { notificationBuilder.addAction(any()) }
    }

    @Test
    fun `test build notification with text content`() {
        logTestName()

        val notificationId = Random.nextInt()
        val icon = R.drawable.ic_notifications
        val textContent = "Notification test content"

        // Build notification
        notificationFactory.buildNotification(
            notificationId = notificationId,
            icon = icon,
            content = textContent,
            addPicture = false,
            progress = null,
            addActions = false
        )

        // Notification has a small icon
        verify { anyConstructed<NotificationCompat.Builder>().setSmallIcon(icon) }
        // Notification has text content
        verify { notificationBuilder.setContentText(textContent) }
        // Notification has default priority
        verify { notificationBuilder.priority = NotificationCompat.PRIORITY_DEFAULT }
        // Notification is cancelled on click
        verify { notificationBuilder.setAutoCancel(true) }
        // Notification does not have large icon
        verify(inverse = true) { notificationBuilder.setLargeIcon(any<Bitmap>()) }
        // Notification does not have any progress
        verify(inverse = true) { notificationBuilder.setProgress(any(), any(), any()) }
        // Notification does not have any action
        verify(inverse = true) { notificationBuilder.addAction(any()) }
    }

    @Test
    fun `test build notification with picture`() {
        logTestName()

        val notificationId = Random.nextInt()
        val icon = R.drawable.ic_notifications
        val bitmap: Bitmap = mockk()
        every { BitmapFactory.decodeResource(any(), any()) } returns bitmap

        // Build notification
        notificationFactory.buildNotification(
            notificationId = notificationId,
            icon = icon,
            content = null,
            addPicture = true,
            progress = null,
            addActions = false
        )

        // Notification has a small icon
        verify { anyConstructed<NotificationCompat.Builder>().setSmallIcon(icon) }
        // Notification does not have text content
        verify { notificationBuilder.setContentText(null) }
        // Notification has default priority
        verify { notificationBuilder.priority = NotificationCompat.PRIORITY_DEFAULT }
        // Notification is cancelled on click
        verify { notificationBuilder.setAutoCancel(true) }
        // Notification has large icon
        verify { notificationBuilder.setLargeIcon(bitmap) }
        // Notification does not have any progress
        verify(inverse = true) { notificationBuilder.setProgress(any(), any(), any()) }
        // Notification does not have any action
        verify(inverse = true) { notificationBuilder.addAction(any()) }
    }

    @Test
    fun `test build notification with progress`() {
        logTestName()

        val notificationId = Random.nextInt()
        val icon = R.drawable.ic_notifications
        val progress = Random.nextInt(0..100)

        // Build notification
        notificationFactory.buildNotification(
            notificationId = notificationId,
            icon = icon,
            content = null,
            addPicture = false,
            progress = progress,
            addActions = false
        )

        // Notification has a small icon
        verify { anyConstructed<NotificationCompat.Builder>().setSmallIcon(icon) }
        // Notification does not have text content
        verify { notificationBuilder.setContentText(null) }
        // Notification has default priority
        verify { notificationBuilder.priority = NotificationCompat.PRIORITY_DEFAULT }
        // Notification is cancelled on click
        verify { notificationBuilder.setAutoCancel(true) }
        // Notification does not have large icon
        verify(inverse = true) { notificationBuilder.setLargeIcon(any<Bitmap>()) }
        // Notification has progress
        verify { notificationBuilder.setProgress(any(), progress, false) }
        // Notification does not have any action
        verify(inverse = true) { notificationBuilder.addAction(any()) }
    }

    @Test
    fun `test build minimal with indeterminate progress`() {
        logTestName()

        val notificationId = Random.nextInt()
        val icon = R.drawable.ic_notifications

        // Build notification
        notificationFactory.buildNotification(
            notificationId = notificationId,
            icon = icon,
            content = null,
            addPicture = false,
            progress = -1,
            addActions = false
        )

        // Notification has a small icon
        verify { anyConstructed<NotificationCompat.Builder>().setSmallIcon(icon) }
        // Notification does not have text content
        verify { notificationBuilder.setContentText(null) }
        // Notification has default priority
        verify { notificationBuilder.priority = NotificationCompat.PRIORITY_DEFAULT }
        // Notification is cancelled on click
        verify { notificationBuilder.setAutoCancel(true) }
        // Notification does not have large icon
        verify(inverse = true) { notificationBuilder.setLargeIcon(any<Bitmap>()) }
        // Notification has indeterminate progress
        verify { notificationBuilder.setProgress(0, 0, true) }
        // Notification does not have any action
        verify(inverse = true) { notificationBuilder.addAction(any()) }
    }

    @Test
    fun `test build notification with actions`() {
        logTestName()

        val notificationId = Random.nextInt()
        val icon = R.drawable.ic_notifications

        // Build notification
        notificationFactory.buildNotification(
            notificationId = notificationId,
            icon = icon,
            content = null,
            addPicture = false,
            progress = null,
            addActions = true
        )

        // Notification has a small icon
        verify { anyConstructed<NotificationCompat.Builder>().setSmallIcon(icon) }
        // Notification does not have text content
        verify { notificationBuilder.setContentText(null) }
        // Notification has default priority
        verify { notificationBuilder.priority = NotificationCompat.PRIORITY_DEFAULT }
        // Notification is cancelled on click
        verify { notificationBuilder.setAutoCancel(true) }
        // Notification does not have large icon
        verify(inverse = true) { notificationBuilder.setLargeIcon(any<Bitmap>()) }
        // Notification does not have any progress
        verify(inverse = true) { notificationBuilder.setProgress(any(), any(), any()) }
        // Notification should have 3 distinct actions
        verify(exactly = 3) { notificationBuilder.addAction(any()) }
        verify(exactly = 1) { notificationBuilder.addAction(withArg { assertEquals(ACTION_1_TITLE, it.title, "Action 1 title") }) }
        verify(exactly = 1) { notificationBuilder.addAction(withArg { assertEquals(ACTION_2_TITLE, it.title, "Action 2 title") }) }
        verify(exactly = 1) { notificationBuilder.addAction(withArg { assertEquals(ACTION_3_TITLE, it.title, "Action 3 title") }) }
    }
}
