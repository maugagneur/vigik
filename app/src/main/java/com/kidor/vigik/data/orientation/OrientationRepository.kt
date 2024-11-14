package com.kidor.vigik.data.orientation

import com.google.android.gms.location.DeviceOrientationListener
import com.google.android.gms.location.DeviceOrientationRequest
import com.google.android.gms.location.FusedOrientationProviderClient
import timber.log.Timber
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository providing methods to get device's orientation updates.
 */
@Singleton
class OrientationRepository @Inject constructor(
    private val fusedOrientationProviderClient: FusedOrientationProviderClient
) {

    /**
     * Adds a listener for orientation updates.
     *
     * @param listener The [DeviceOrientationListener] to add.
     * @param executor The thread executor to request orientation updates.
     */
    fun addListener(
        listener: DeviceOrientationListener,
        executor: ExecutorService = Executors.newSingleThreadExecutor()
    ) {
        val request = DeviceOrientationRequest
            .Builder(DeviceOrientationRequest.OUTPUT_PERIOD_DEFAULT)
            .build()
        fusedOrientationProviderClient.requestOrientationUpdates(request, executor, listener)
            .addOnSuccessListener { Timber.i("Successfully added new orientation listener") }
            .addOnFailureListener { exception -> Timber.e(exception, "Failed to add new orientation listener") }
    }

    /**
     * Removes a listener for orientation updates.
     *
     * @param listener The [DeviceOrientationListener] to remove.
     */
    fun removeListener(listener: DeviceOrientationListener) {
        fusedOrientationProviderClient.removeOrientationUpdates(listener)
    }
}
