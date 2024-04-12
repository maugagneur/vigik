package com.kidor.vigik.data.midjourney

import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.testing.TestPager
import com.kidor.vigik.data.midjourney.model.GeneratedImage
import com.kidor.vigik.data.midjourney.model.GetGeneratedImagesResponse
import com.kidor.vigik.utils.AssertUtils.assertTrue
import com.kidor.vigik.utils.TestUtils.logTestName
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for [GeneratedImagesPagingSource]
 */
class GeneratedImagesPagingSourceTest {

    private lateinit var pagingSource: GeneratedImagesPagingSource

    @MockK
    private lateinit var midJourneyApi: MidJourneyApi

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        coEvery { midJourneyApi.getRandomGeneratedImages(1) } returns
            GetGeneratedImagesResponse(
                totalPages = 1,
                currentPage = 1,
                pageSize = 1,
                totalImages = 3,
                images = listOf(FAKE_IMAGE_1, FAKE_IMAGE_2, FAKE_IMAGE_3)
            )
        pagingSource = GeneratedImagesPagingSource(service = midJourneyApi)
    }

    @Test
    fun `test paging source use data from API`() {
        logTestName()

        runTest {
            val pager = TestPager(
                config = CONFIG,
                pagingSource = pagingSource
            )
            val result = pager.refresh() as PagingSource.LoadResult.Page
            assertTrue(result.data.contains(FAKE_IMAGE_1), "Data contains image 1")
            assertTrue(result.data.contains(FAKE_IMAGE_2), "Data contains image 2")
            assertTrue(result.data.contains(FAKE_IMAGE_3), "Data contains image 3")
        }
    }

    companion object {
        private val CONFIG = PagingConfig(
            pageSize = 20,
            enablePlaceholders = false
        )
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
