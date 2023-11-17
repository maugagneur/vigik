package com.kidor.vigik.receivers

import android.app.NotificationManager
import android.content.Intent
import com.kidor.vigik.utils.TestUtils.logTestName
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for [NotificationReceiver].
 */
class NotificationReceiverTest {

    private lateinit var receiver: NotificationReceiver

    @MockK
    private lateinit var notificationManager: NotificationManager
    @MockK
    private lateinit var receivedIntent: Intent

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        receiver = NotificationReceiver(notificationManager)
    }

    @Test
    fun `test when receiving invalid intent`() {
        logTestName()

        // Receive an intent without action
        every { receivedIntent.action } returns null
        receiver.onReceive(null, receivedIntent)

        // Nothing should occurred
        verify(inverse = true) { notificationManager.cancel(any()) }
    }

    @Test
    fun `test when receiving DO_NOTHING intent action`() {
        logTestName()

        // Receive an intent with action = NotificationReceiver.ACTION_DO_NOTHING
        every { receivedIntent.action } returns NotificationReceiver.ACTION_DO_NOTHING
        receiver.onReceive(null, receivedIntent)

        // Nothing should occurred
        verify(inverse = true) { notificationManager.cancel(any()) }
    }

    @Test
    fun `test when receiving ACTION_REMOVE intent action without extra parameter`() {
        logTestName()

        // Receive an intent with action = NotificationReceiver.ACTION_REMOVE
        every { receivedIntent.action } returns NotificationReceiver.ACTION_REMOVE
        every { receivedIntent.hasExtra(NotificationReceiver.EXTRA_NOTIFICATION_ID) } returns false
        receiver.onReceive(null, receivedIntent)

        // Nothing should occurred
        verify(inverse = true) { notificationManager.cancel(any()) }
    }

    @Test
    fun `test when receiving ACTION_REMOVE intent action with extra parameter`() {
        logTestName()

        val notificationId = 42

        // Receive an intent with action = NotificationReceiver.ACTION_REMOVE and a notification ID
        every { receivedIntent.action } returns NotificationReceiver.ACTION_REMOVE
        every { receivedIntent.hasExtra(NotificationReceiver.EXTRA_NOTIFICATION_ID) } returns true
        every { receivedIntent.getIntExtra(NotificationReceiver.EXTRA_NOTIFICATION_ID, any()) } returns notificationId
        receiver.onReceive(null, receivedIntent)

        // Notification should be cancelled
        verify { notificationManager.cancel(notificationId) }
    }
}
