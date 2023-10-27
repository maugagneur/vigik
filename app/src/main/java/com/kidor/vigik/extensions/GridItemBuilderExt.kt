package com.kidor.vigik.extensions

import android.content.Context
import androidx.annotation.DrawableRes
import androidx.car.app.model.CarColor
import androidx.car.app.model.CarIcon
import androidx.car.app.model.GridItem
import androidx.core.graphics.drawable.IconCompat

/**
 * Sets an image to show in the grid item with the given parameters.
 *
 * @param context The context for the application whose resources should be used to resolve the given resource ID.
 * @param resId   ID of the drawable resource.
 * @param tint    Tint of the icon. Can be null to apply the default tint.
 */
fun GridItem.Builder.setImageFromDrawable(
    context: Context,
    @DrawableRes resId: Int,
    tint: CarColor? = null
): GridItem.Builder {
    this.setImage(
        CarIcon.Builder(IconCompat.createWithResource(context, resId))
            .apply {
                if (tint != null) {
                    setTint(tint)
                }
            }
            .build(),
        GridItem.IMAGE_TYPE_ICON
    )
    return this
}
