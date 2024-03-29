package com.kidor.vigik.data.midjourney

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.kidor.vigik.data.midjourney.model.GeneratedImage
import kotlinx.coroutines.flow.Flow

private const val PAGE_SIZE = 20

/**
 * Implementation of [GeneratedImagesRepository].
 */
class GeneratedImagesRepositoryImp(
    private val midJourneyApi: MidJourneyApi
): GeneratedImagesRepository {

    override fun getGeneratedImages(): Flow<PagingData<GeneratedImage>> {
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                GeneratedImagesPagingSource(service = midJourneyApi)
            }
        ).flow
    }
}
