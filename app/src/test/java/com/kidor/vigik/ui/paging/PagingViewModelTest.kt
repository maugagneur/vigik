package com.kidor.vigik.ui.paging

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.testing.asPagingSourceFactory
import androidx.paging.testing.asSnapshot
import com.kidor.vigik.MainCoroutineRule
import com.kidor.vigik.data.midjourney.GeneratedImagesRepository
import com.kidor.vigik.data.midjourney.model.GeneratedImage
import com.kidor.vigik.utils.AssertUtils.assertNotNull
import com.kidor.vigik.utils.AssertUtils.assertTrue
import com.kidor.vigik.utils.TestUtils.logTestName
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Unit tests for [PagingViewModel].
 */
class PagingViewModelTest {

    @ExperimentalCoroutinesApi
    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: PagingViewModel

    @MockK
    private lateinit var generatedImagesRepository: GeneratedImagesRepository

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        val images = listOf(FAKE_IMAGE_1, FAKE_IMAGE_2, FAKE_IMAGE_3)
        every { generatedImagesRepository.getGeneratedImages() } returns Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            ),
            pagingSourceFactory = images.asPagingSourceFactory()
        ).flow
        viewModel = PagingViewModel(generatedImagesRepository)
    }

    @Test
    fun `test view model forwards paging flow from image repository`() {
        logTestName()

        runTest {
            viewModel.images().asSnapshot().let { data ->
                assertNotNull(data, "Paging data")
                assertTrue(data.contains(FAKE_IMAGE_1), "Data contains image 1")
                assertTrue(data.contains(FAKE_IMAGE_2), "Data contains image 2")
                assertTrue(data.contains(FAKE_IMAGE_3), "Data contains image 3")
            }
        }
    }

    companion object {
        private val FAKE_IMAGE_1 = GeneratedImage(
            ratio = 1.0f,
            date = "2024-04-02T16:50:08.699+02:00",
            imageUrl = "https://cdn.midjourney.com/72fb778a-d9ee-4542-b495-0a9b71387fac/0_0.webp"
        )
        private val FAKE_IMAGE_2 = GeneratedImage(
            ratio = 0.5604396f,
            date = "2024-04-02T16:50:08.701+02:00",
            imageUrl = "https://cdn.midjourney.com/c469ee32-2f63-4121-906e-21204402affa/0_0.webp"
        )
        private val FAKE_IMAGE_3 = GeneratedImage(
            ratio = 1.75f,
            date = "2024-04-02T16:50:08.706+02:00",
            imageUrl = "https://cdn.midjourney.com/fcf8346d-e2a4-481a-9fd8-f9b922984466/0_1.webp"
        )
    }
}
