package com.kidor.vigik.nfc.hostapdu

import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.os.Message
import android.os.Messenger
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Helper used to interact with the custom host APDU (Application Protocol Data Unit) service.
 */
@Singleton
class HostApduManager @Inject constructor(@ApplicationContext context: Context) {

    private var messenger: Messenger? = null
    private val localBroadcastManager: LocalBroadcastManager = LocalBroadcastManager.getInstance(context)
    private val listeners = mutableListOf<HostApduListener>()

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            messenger = Messenger(service)
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            messenger = null
        }
    }

    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action.equals(HostApduServiceImpl.APDU_SERVICE_ACTION_NEW_MESSAGE)) {
                when (val type = intent?.getStringExtra(HostApduServiceImpl.KEY_MESSAGE_TYPE)) {
                    HostApduServiceImpl.TYPE_APDU_COMMAND -> {
                        val apduCommand = intent.getByteArrayExtra(HostApduServiceImpl.KEY_APDU_COMMAND)
                        // Notify listeners
                        listeners.forEach { it.onApduCommandReceived(apduCommand) }
                    }
                    HostApduServiceImpl.TYPE_DEACTIVATED -> {
                        val reason = intent.getIntExtra(HostApduServiceImpl.KEY_DEACTIVATED_REASON, -1)
                        // Notify listeners
                        listeners.forEach { it.onConnectionLost(reason) }
                    }
                    else -> Timber.w("Unexpected message type received from host APDU service: $type")
                }
            }
        }
    }

    init {
        // Register to the local broadcast forwarding messages from the host APDU service
        val intentFilter = IntentFilter(HostApduServiceImpl.APDU_SERVICE_ACTION_NEW_MESSAGE)
        localBroadcastManager.registerReceiver(broadcastReceiver, intentFilter)

        // Connect to the custom host APDU service
        context.bindService(
            Intent(context, HostApduServiceImpl::class.java),
            serviceConnection,
            Context.BIND_AUTO_CREATE
        )
    }

    /**
     * Register to get host APDU notifications.
     *
     * @see unregister
     */
    fun register(listener: HostApduListener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener)
        }
    }

    /**
     * Unregister to host APDU notifications.
     */
    fun unregister(listener: HostApduListener) = listeners.remove(listener)

    /**
     * Sends an APDU frame.
     *
     * @param apduResponse The frame to send.
     * @throws android.os.RemoteException Throws DeadObjectException if the target Handler no longer exists.
     */
    fun sendApduResponse(apduResponse: ByteArray?) {
        val message = Message.obtain(null, HostApduServiceImpl.MSG_RESPONSE_APDU).apply {
            data = Bundle().apply {
                putByteArray(HostApduServiceImpl.KEY_DATA, apduResponse)
            }
        }
        messenger?.send(message)
    }
}
