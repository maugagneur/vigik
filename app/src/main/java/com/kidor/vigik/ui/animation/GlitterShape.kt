package com.kidor.vigik.ui.animation

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
    Rectangle,
    Circle,
    Triangle
}
