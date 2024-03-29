package com.kidor.vigik.data.midjourney.model

/**
 * Model object of a generated image.
 *
 * @param ratio    The ratio of the image.
 * @param date     The creation date of the image.
 * @param imageUrl The URL of the image.
 */
data class GeneratedImage(val ratio: Float?, val date: String?, val imageUrl: String?)
