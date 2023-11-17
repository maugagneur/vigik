package com.kidor.vigik.receivers

import android.content.Intent
import android.location.LocationManager
import com.kidor.vigik.data.bluetooth.BluetoothApi
import com.kidor.vigik.utils.TestUtils.logTestName
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for [LocationStateChangeReceiver].
 */
class LocationStateChangeReceiverTest {

    private lateinit var receiver: LocationStateChangeReceiver

    @MockK
    private lateinit var bluetoothApi: BluetoothApi
    @MockK
    private lateinit var receivedIntent: Intent

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        receiver = LocationStateChangeReceiver(bluetoothApi)
    }

    @Test
    fun `test when receiving invalid intent`() {
        logTestName()

        // Receive an intent without action
        every { receivedIntent.action } returns null
        receiver.onReceive(null, receivedIntent)

        // No notification should occurred
        verify(inverse = true) { bluetoothApi.onLocationStateChanged() }
    }

    @Test
    fun `test when location state changes`() {
        logTestName()

        // Receive a valid intent
        every { receivedIntent.action } returns LocationManager.MODE_CHANGED_ACTION
        receiver.onReceive(null, receivedIntent)

        // No notification should occurred
        verify { bluetoothApi.onLocationStateChanged() }
    }
}
