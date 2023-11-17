package com.kidor.vigik.extensions

import android.content.Context
import android.graphics.drawable.Icon
import androidx.car.app.model.CarColor
import androidx.car.app.model.CarIcon
import androidx.car.app.model.GridItem
import androidx.core.graphics.drawable.IconCompat
import com.kidor.vigik.R
import com.kidor.vigik.utils.TestUtils.logTestName
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockkConstructor
import io.mockk.mockkStatic
import io.mockk.verify
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for GridItemBuilder's extensions.
 */
class GridItemBuilderExtTest {

    private lateinit var builder: GridItem.Builder

    @MockK
    private lateinit var context: Context
    @MockK
    private lateinit var iconCompat: IconCompat
    @MockK
    private lateinit var carIcon: CarIcon

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        mockkConstructor(CarIcon.Builder::class)
        every { anyConstructed<CarIcon.Builder>().build() } returns carIcon
        mockkStatic(IconCompat::class)
        every { iconCompat.type } returns Icon.TYPE_RESOURCE
        every { carIcon.type } returns Icon.TYPE_RESOURCE
        builder = GridItem.Builder()
    }

    @Test
    fun `test set image from drawable`() {
        logTestName()

        val resourceId = R.drawable.ic_car
        val color = CarColor.BLUE
        every { IconCompat.createWithResource(context, resourceId) } returns iconCompat

        builder.setImageFromDrawable(context = context, resId = resourceId, tint = color)

        // Check that the tint was applied to the icon
        verify { anyConstructed<CarIcon.Builder>().setTint(color) }
        // Check that the built icon is set as image with the good type
        verify { builder.setImage(carIcon, GridItem.IMAGE_TYPE_ICON) }
    }

    @Test
    fun `test set image from drawable without tint`() {
        logTestName()

        val resourceId = R.drawable.ic_cloud
        every { IconCompat.createWithResource(context, resourceId) } returns iconCompat

        builder.setImageFromDrawable(context = context, resId = resourceId)

        // Check that the no tint was applied to the icon
        verify(inverse = true) { anyConstructed<CarIcon.Builder>().setTint(any()) }
        // Check that the built icon is set as image with the good type
        verify { builder.setImage(carIcon, GridItem.IMAGE_TYPE_ICON) }
    }
}
