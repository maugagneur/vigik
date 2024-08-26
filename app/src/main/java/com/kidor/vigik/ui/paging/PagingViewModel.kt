package com.kidor.vigik.ui.paging

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.kidor.vigik.data.midjourney.GeneratedImagesRepository
import com.kidor.vigik.data.midjourney.model.GeneratedImage
import com.kidor.vigik.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Business logic of Paging screen.
 */
@HiltViewModel
class PagingViewModel @Inject constructor(
    private val generatedImagesRepository: GeneratedImagesRepository
) : BaseViewModel<Nothing, Nothing, Nothing>() {

    /**
     * The paging flow of images to display.
     */
    fun images(): Flow<PagingData<GeneratedImage>> = generatedImagesRepository
        .getGeneratedImages()
        .cachedIn(viewModelScope)
}
