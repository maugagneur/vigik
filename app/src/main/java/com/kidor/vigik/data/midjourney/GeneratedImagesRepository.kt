package com.kidor.vigik.data.midjourney

import androidx.paging.PagingData
import com.kidor.vigik.data.midjourney.model.GeneratedImage
import kotlinx.coroutines.flow.Flow

/**
 * Interface of a repository related to images generation features.
 */
fun interface GeneratedImagesRepository {

    /**
     * Returns a [Flow] of generated images.
     */
    fun getGeneratedImages(): Flow<PagingData<GeneratedImage>>
}
