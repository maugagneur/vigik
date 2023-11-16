package com.kidor.vigik.ui.camera

import android.net.Uri
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.kidor.vigik.MainCoroutineRule
import com.kidor.vigik.utils.AssertUtils.assertEquals
import com.kidor.vigik.utils.AssertUtils.assertTrue
import com.kidor.vigik.utils.TestUtils.logTestName
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Unit tests for [CameraViewModel].
 */
class CameraViewModelTest {

    @ExperimentalCoroutinesApi
    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: CameraViewModel

    @MockK
    private lateinit var pictureUri: Uri

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        viewModel = CameraViewModel()
    }

    @Test
    fun `test view initial state`() {
        logTestName()

        // Check initial view state
        val initialViewState = viewModel.viewState.value
        assertTrue(initialViewState is CameraViewState.CheckPermission, "Initial view state")
    }

    @Test
    fun `test view shows camera when camera permission is granted`() {
        logTestName()

        // Grant camera permission
        viewModel.handleAction(CameraViewAction.PermissionGranted)

        // Check view state
        val viewState = viewModel.viewState.value
        assertTrue(viewState is CameraViewState.ShowCamera, "View state")
    }

    @Test
    fun `test view shows picture when a photo is taken`() {
        logTestName()

        // Grant camera permission
        viewModel.handleAction(CameraViewAction.PhotoCaptured(uri = pictureUri))

        // Check view state
        val viewState = viewModel.viewState.value
        if (viewState is CameraViewState.ShowCapturedPhoto) {
            assertEquals(pictureUri, viewState.uri, "URI")
        } else {
            fail()
        }
    }

    @Test
    fun `test view shows camera when user wants to retry`() {
        logTestName()

        // Grant camera permission
        viewModel.handleAction(CameraViewAction.RetryCapture)

        // Check view state
        val viewState = viewModel.viewState.value
        assertTrue(viewState is CameraViewState.ShowCamera, "View state")
    }
}
