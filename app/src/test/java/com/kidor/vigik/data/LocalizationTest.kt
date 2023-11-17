package com.kidor.vigik.data

import android.content.Context
import android.content.res.Resources
import android.content.res.Resources.NotFoundException
import com.kidor.vigik.utils.AssertUtils.assertEquals
import com.kidor.vigik.utils.TestUtils.logTestName
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import org.junit.Before
import org.junit.Test
import kotlin.random.Random

/**
 * Unit tests for [Localization].
 */
class LocalizationTest {

    private lateinit var localization: Localization

    @MockK
    private lateinit var context: Context
    @MockK
    private lateinit var resources: Resources

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        every { context.resources } returns resources
        localization = Localization(context)
    }

    @Test
    fun `test getting string resource`() {
        logTestName()

        val testResId = Random.nextInt()
        val testTranslation = "Translation for test"
        every { resources.getString(testResId) } returns testTranslation

        val result = localization.getString(testResId)

        assertEquals(testTranslation, result, "Translation")
    }

    @Test
    fun `test string resource not found`() {
        logTestName()

        val testResId = Random.nextInt()
        every { resources.getString(testResId) } throws NotFoundException()

        val result = localization.getString(testResId)

        // If translation is not found it should return the resource ID instead
        assertEquals("$testResId", result, "Translation")
    }

    @Test
    fun `test string resource with args`() {
        logTestName()

        val testResId = Random.nextInt()
        val testArgs = arrayOf(1, "a")
        val testTranslation = "Translation for test"
        every { resources.getString(testResId, testArgs) } returns testTranslation

        val result = localization.getString(testResId, testArgs)

        assertEquals(testTranslation, result, "Translation")
    }

    @Test
    fun `test string resource with args not found`() {
        logTestName()

        val testResId = Random.nextInt()
        val testArgs = arrayOf(1, "a")
        every { resources.getString(testResId, testArgs) } throws NotFoundException()

        val result = localization.getString(testResId, testArgs)

        // If translation is not found it should return the resource ID instead
        assertEquals("$testResId", result, "Translation")
    }
}
