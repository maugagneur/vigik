package com.kidor.vigik.extensions

import android.app.Activity
import android.graphics.Bitmap
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.view.PixelCopy
import android.view.View
import androidx.compose.ui.geometry.Rect
import timber.log.Timber

/**
 * Captures a copy of the screen.
 *
 * @param bounds         The boundaries of the layout to capture.
 * @param bitmapCallback The callback to retrieve the [Bitmap].
 */
fun View.screenshot(bounds: Rect, bitmapCallback: (Bitmap?) -> Unit) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        try {
            val bitmap = Bitmap.createBitmap(
                bounds.width.toInt(),
                bounds.height.toInt(),
                Bitmap.Config.ARGB_8888
            )
            PixelCopy.request(
                (context as Activity).window,
                android.graphics.Rect(
                    bounds.left.toInt(),
                    bounds.top.toInt(),
                    bounds.right.toInt(),
                    bounds.bottom.toInt()
                ),
                bitmap,
                { result ->
                    when (result) {
                        PixelCopy.SUCCESS -> bitmapCallback.invoke(bitmap)
                        else -> bitmapCallback.invoke(null)
                    }
                },
                Handler(Looper.getMainLooper())
            )
        } catch (exception: IllegalStateException) {
            Timber.e(exception, "Fail to take a screenshot of the view")
        }
    } else {
        Timber.i("Screenshot of current view unavailable on this device")
        bitmapCallback.invoke(null)
    }
}
