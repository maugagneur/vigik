package com.kidor.vigik.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import com.kidor.vigik.data.bluetooth.BluetoothApi

/**
 * Broadcast receiver listening to Location state changes.
 */
class LocationStateChangeReceiver(private val bluetoothApi: BluetoothApi) : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == LocationManager.MODE_CHANGED_ACTION) {
            bluetoothApi.onLocationStateChanged()
        }
    }
}
