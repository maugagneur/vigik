package com.kidor.vigik.data.orientation

import com.google.android.gms.location.DeviceOrientationListener
import com.google.android.gms.location.DeviceOrientationRequest
import com.google.android.gms.location.FusedOrientationProviderClient
import com.google.android.gms.tasks.Task
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockkConstructor
import io.mockk.verify
import org.junit.Before
import org.junit.Test
import java.util.concurrent.ExecutorService

/**
 * Unit tests for [OrientationRepository].
 */
class OrientationRepositoryTest {

    private lateinit var repository: OrientationRepository

    @MockK
    private lateinit var fusedOrientationProviderClient: FusedOrientationProviderClient
    @MockK
    private lateinit var listener: DeviceOrientationListener
    @MockK
    private lateinit var executorService: ExecutorService
    @MockK
    private lateinit var deviceOrientationRequest: DeviceOrientationRequest
    @MockK
    private lateinit var voidTask: Task<Void>

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        mockkConstructor(DeviceOrientationRequest.Builder::class)
        every { anyConstructed<DeviceOrientationRequest.Builder>().build() } returns deviceOrientationRequest
        every { voidTask.addOnSuccessListener(any()) } returns voidTask
        every { voidTask.addOnFailureListener(any()) } returns voidTask
        repository = OrientationRepository(fusedOrientationProviderClient)
    }

    @Test
    fun `test add listener`() {
        every { fusedOrientationProviderClient.requestOrientationUpdates(any(), any(), any()) } returns voidTask

        repository.addListener(listener, executorService)

        verify { fusedOrientationProviderClient.requestOrientationUpdates(deviceOrientationRequest, executorService, listener) }
        verify { voidTask.addOnSuccessListener(any()) }
        verify { voidTask.addOnFailureListener(any()) }
    }

    @Test
    fun `test remove listener`() {
        every { fusedOrientationProviderClient.removeOrientationUpdates(listener) } returns voidTask

        repository.removeListener(listener)

        verify { fusedOrientationProviderClient.removeOrientationUpdates(listener) }
    }
}
