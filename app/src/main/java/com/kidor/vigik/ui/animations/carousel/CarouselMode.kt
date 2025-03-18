package com.kidor.vigik.ui.animations.carousel

/**
 * Available display mode for carousel.
 */
enum class CarouselMode {
    /**
     * Highlight the center element by zooming on it.
     */
    ZOOM,

    /**
     * Create the illusion of a rotation cube.
     */
    CUBE,

    /**
     * Create a circular reveal animation that will clip the incoming page in a growing circle.
     */
    CIRCLE_REVEAL,

    /**
     * Add some parallax with a blur effect.
     */
    BLUR_DECAL
}
