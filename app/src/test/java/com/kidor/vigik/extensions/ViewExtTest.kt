package com.kidor.vigik.extensions

import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.view.View
import androidx.compose.ui.geometry.Rect
import com.kidor.vigik.utils.TestUtils.logTestName
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockkStatic
import io.mockk.verify
import org.junit.Before
import org.junit.Test
import java.lang.reflect.Field
import java.lang.reflect.Modifier

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

    private fun setSdkIntViaReflection(value: Int) {
        Build.VERSION::class.java.getDeclaredField("SDK_INT").let { field ->
            field.isAccessible = true
            getModifiersField().apply {
                isAccessible = true
                set(field, field.modifiers and Modifier.FINAL.inv())
            }
            field.set(null, value)
        }
    }

    @Suppress("NestedBlockDepth")
    private fun getModifiersField(): Field {
        return try {
            Field::class.java.getDeclaredField("modifiers")
        } catch (noSuchFieldException: NoSuchFieldException) {
            try {
                val fields = Class::class.java.getDeclaredMethod(
                    "getDeclaredFields0",
                    Boolean::class.javaPrimitiveType
                ).let { method ->
                    method.isAccessible = true
                    method.invoke(Field::class.java, false) as Array<Field>
                }
                for (field in fields) {
                    if ("modifiers" == field.name) {
                        return field
                    }
                }
            } catch (reflectiveOperationException: ReflectiveOperationException) {
                noSuchFieldException.addSuppressed(reflectiveOperationException)
            }
            throw noSuchFieldException
        }
    }

    @Test
    fun `test capturing screen on unsupported SDK version`() {
        logTestName()

        setSdkIntViaReflection(Build.VERSION_CODES.N_MR1)
        val bounds = Rect(0f, 0f, 10f, 10f)

        // Try to capture screen
        view.screenshot(bounds, bitmapCallback)

        // Check that no bitmap is retrieved
        verify { bitmapCallback.invoke(null) }
    }

    @Test
    fun `test capturing screen`() {
        logTestName()

        setSdkIntViaReflection(Build.VERSION_CODES.P)
        val bounds = Rect(0f, 0f, -10f, -10f)
        every { Bitmap.createBitmap(-10, -10, Bitmap.Config.ARGB_8888) } throws IllegalStateException()

        // Try to capture screen
        view.screenshot(bounds, bitmapCallback)

        // Check that no bitmap is retrieved
        verify { bitmapCallback.invoke(null) }
    }
}
