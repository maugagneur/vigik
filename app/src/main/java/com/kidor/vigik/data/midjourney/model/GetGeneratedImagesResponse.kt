package com.kidor.vigik.data.midjourney.model

/**
 * Model object for network response when requesting generated images.
 *
 * @param totalPages  The total number of pages.
 * @param currentPage The current page index.
 * @param pageSize    The size of each page.
 * @param totalImages The total number of images available.
 * @param images      The list of generated images.
 */
data class GetGeneratedImagesResponse(
    val totalPages: Int?,
    val currentPage: Int?,
    val pageSize: Int?,
    val totalImages: Int?,
    val images: List<GeneratedImage>?
)
