package com.kidor.vigik.ui.compass

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.android.gms.location.DeviceOrientation
import com.kidor.vigik.MainCoroutineRule
import com.kidor.vigik.data.orientation.OrientationRepository
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Unit tests for [CompassViewModel].
 */
@ExperimentalCoroutinesApi
class CompassViewModelTest {

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: CompassViewModel

    @MockK
    private lateinit var orientationRepository: OrientationRepository
    @MockK
    private lateinit var deviceOrientation: DeviceOrientation

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        viewModel = CompassViewModel(
            ioDispatcher = mainCoroutineRule.testDispatcher,
            orientationRepository = orientationRepository
        )
    }

    @Test
    fun `check start listening orientation at init`() {
        verify { orientationRepository.addListener(viewModel, any()) }
    }

    @Test
    fun `check stop listening orientation when cleared`() {
        viewModel.onCleared()

        verify { orientationRepository.removeListener(viewModel) }
    }

    @Test
    fun `check view uses heading gets when device orientation changed`() {
        val heading = 42f
        every { deviceOrientation.headingDegrees } returns heading

        viewModel.onDeviceOrientationChanged(orientation = deviceOrientation)

        val viewState = viewModel.viewState.value
        assertEquals(heading, viewState?.cameraPositionState?.position?.bearing)
    }
}
