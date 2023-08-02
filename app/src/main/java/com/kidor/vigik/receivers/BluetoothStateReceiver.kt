package com.kidor.vigik.receivers

import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.kidor.vigik.data.bluetooth.BluetoothApi
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * Broadcast receiver listening to Bluetooth state changes.
 */
@AndroidEntryPoint
class BluetoothStateReceiver : BroadcastReceiver() {

    @Inject lateinit var bluetoothApi: BluetoothApi

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == BluetoothAdapter.ACTION_STATE_CHANGED) {
            when (intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR)) {
                BluetoothAdapter.STATE_OFF -> bluetoothApi.onBluetoothStateChanged(false)
                BluetoothAdapter.STATE_ON -> bluetoothApi.onBluetoothStateChanged(true)
            }
        }
    }
}
