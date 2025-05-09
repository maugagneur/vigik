package com.kidor.vigik.ui.animations.glitterrainbow

/**
 * A shape configuration for glitters.
 */
interface GlitterShape

/**
 * Undefined shape for glitters.
 */
data object Mixed : GlitterShape

/**
 * Geometric shape for glitters.
 */
enum class GeometricShape : GlitterShape {
    /** Rectangle shape. **/
    RECTANGLE,
    /** Circle shape. **/
    CIRCLE,
    /** Triangle shape. **/
    TRIANGLE
}
