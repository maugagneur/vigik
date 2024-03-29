package com.kidor.vigik.data.midjourney

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.kidor.vigik.data.midjourney.model.GeneratedImage
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import kotlin.math.min

private const val STARTING_PAGE_INDEX = 1

/**
 * Base class for loading snapshots of generated images.
 */
class GeneratedImagesPagingSource @Inject constructor(
    private val service: MidJourneyApi
) : PagingSource<Int, GeneratedImage>() {

    override fun getRefreshKey(state: PagingState<Int, GeneratedImage>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, GeneratedImage> {
        val pageIndex = params.key ?: STARTING_PAGE_INDEX
        return try {
            val response = service.getRandomGeneratedImages(pageIndex)
            val images = response.images

            if (images == null || response.pageSize == null) {
                return LoadResult.Invalid()
            }

            val nextKey = if (images.isEmpty()) {
                null
            } else {
                min(pageIndex + 1, response.totalPages ?: 1)
            }

            LoadResult.Page(
                data = images,
                prevKey = if (pageIndex == STARTING_PAGE_INDEX) null else pageIndex,
                nextKey = nextKey
            )
        } catch (exception: IOException) {
            return LoadResult.Error(exception)
        } catch (exception: HttpException) {
            return LoadResult.Error(exception)
        }
    }
}
