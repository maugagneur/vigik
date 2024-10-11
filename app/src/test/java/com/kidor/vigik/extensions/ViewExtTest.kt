package com.kidor.vigik.extensions

import android.content.Context
import android.graphics.Bitmap
import android.view.View
import androidx.compose.ui.geometry.Rect
import com.kidor.vigik.utils.BuildVersionWrapper
import com.kidor.vigik.utils.TestUtils.logTestName
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockkObject
import io.mockk.mockkStatic
import io.mockk.verify
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for View's extensions.
 */
class ViewExtTest {

    private lateinit var view: View

    @MockK
    private lateinit var context: Context
    @MockK
    private lateinit var bitmapCallback: (Bitmap?) -> Unit

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        every { bitmapCallback.invoke(null) } returns Unit
        mockkStatic(Bitmap::class)
        view = View(context)
    }

    @Test
    fun `test capturing screen on unsupported SDK version`() {
        logTestName()

        mockkObject(BuildVersionWrapper)
        every { BuildVersionWrapper.isOOrAbove() } returns false
        val bounds = Rect(0f, 0f, 10f, 10f)

        // Try to capture screen
        view.screenshot(bounds, bitmapCallback)

        // Check that no bitmap is retrieved
        verify { bitmapCallback.invoke(null) }
    }

    @Test
    fun `test capturing screen`() {
        logTestName()

        mockkObject(BuildVersionWrapper)
        every { BuildVersionWrapper.isOOrAbove() } returns true
        val bounds = Rect(0f, 0f, -10f, -10f)
        every { Bitmap.createBitmap(-10, -10, Bitmap.Config.ARGB_8888) } throws IllegalStateException()

        // Try to capture screen
        view.screenshot(bounds, bitmapCallback)

        // Check that no bitmap is retrieved
        verify { bitmapCallback.invoke(null) }
    }
}
