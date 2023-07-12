package com.kidor.vigik.data.bluetooth

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * Broadcast receiver listening to Location state changes.
 */
@AndroidEntryPoint
class LocationStateChangeReceiver : BroadcastReceiver() {

    @Inject lateinit var bluetoothApi: BluetoothApi

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == LocationManager.MODE_CHANGED_ACTION) {
            bluetoothApi.onLocationStateChanged()
        }
    }
}
