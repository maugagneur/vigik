package com.kidor.vigik.data.notification

import android.app.NotificationManager
import com.kidor.vigik.utils.TestUtils.logTestName
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for [RemoveNotificationFromUiUseCase].
 */
class RemoveNotificationFromUiUseCaseTest {

    private lateinit var useCase: RemoveNotificationFromUiUseCase

    @MockK
    private lateinit var notificationManager: NotificationManager

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        useCase = RemoveNotificationFromUiUseCase(notificationManager)
    }

    @Test
    fun `test use case execution cancels the notification`() {
        logTestName()

        var notificationId = 42
        useCase(notificationId)
        verify { notificationManager.cancel(notificationId) }

        notificationId = 1337
        useCase(notificationId)
        verify { notificationManager.cancel(notificationId) }
    }
}
