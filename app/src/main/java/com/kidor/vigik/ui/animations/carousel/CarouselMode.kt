package com.kidor.vigik.ui.animations.carousel

import com.kidor.vigik.R

/**
 * Available display mode for carousel.
 *
 * @param displayNameId The resource ID to use as display name.
 */
enum class CarouselMode(val displayNameId: Int) {
    /**
     * Classic horizontal pager without any customization.
     */
    CLASSIC(R.string.carousel_mode_classic),

    /**
     * Highlight the center element by zooming on it.
     */
    ZOOM(R.string.carousel_mode_zoom),

    /**
     * Create the illusion of a rotation cube.
     */
    CUBE(R.string.carousel_mode_cube),

    /**
     * Create a circular reveal animation that will clip the incoming page in a growing circle.
     */
    CIRCLE_REVEAL(R.string.carousel_mode_circle_reveal),

    /**
     * Add some parallax with a blur effect.
     */
    DECAL(R.string.carousel_mode_decal)
}
