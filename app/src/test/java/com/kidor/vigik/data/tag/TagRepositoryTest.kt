package com.kidor.vigik.data.tag

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.kidor.vigik.MainCoroutineRule
import com.kidor.vigik.data.tag.model.RoomTag
import com.kidor.vigik.extensions.toTagList
import com.kidor.vigik.data.nfc.model.Tag
import com.kidor.vigik.utils.AssertUtils.assertEquals
import com.kidor.vigik.utils.TestUtils.logTestName
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Unit tests for [TagRepositoryImp].
 */
class TagRepositoryTest {

    @ExperimentalCoroutinesApi
    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var repository: TagRepository

    @MockK
    private lateinit var tagDao: TagDao

    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        every { tagDao.getAll() } returns flowOf(emptyList())
    }

    @Test
    fun addTagInDatabase() {
        logTestName()

        // Given
        val expectedResult = 42L
        coEvery { tagDao.insert(any()) } returns expectedResult

        runBlocking {
            // When
            repository = TagRepositoryImp(tagDao)
            val result = repository.insert(Tag())

            // Then
            assertEquals(expectedResult, result, "Insert result")
        }
    }

    @Test
    fun getAllTagsWhenNothingInBase() {
        logTestName()

        // Given
        coEvery { tagDao.getAll() } returns flowOf(emptyList())

        runBlocking {
            // When
            repository = TagRepositoryImp(tagDao)
            repository.allTags.test {
                // Then
                assertEquals(awaitItem(), emptyList<Tag>(), "Tag list from database")
                cancelAndIgnoreRemainingEvents()
            }
        }
    }

    @Test
    fun getAllTagsAfterAddingSomeInDatabase() {
        logTestName()

        // Given
        val roomTags = listOf(
            RoomTag(System.currentTimeMillis(), byteArrayOf(0x13, 0x37), "Tech list", "Data", byteArrayOf(0x42)),
            RoomTag(System.currentTimeMillis(), byteArrayOf(0xBE.toByte(), 0xEF.toByte()), "Tech list", "Data", byteArrayOf(0xFF.toByte()))
        )
        coEvery { tagDao.getAll() } returns flowOf(roomTags)

        runBlocking {
            // When
            repository = TagRepositoryImp(tagDao)
            repository.allTags.test {
                // Then
                assertEquals(roomTags.toTagList(), awaitItem(), "Tag list from database")
                cancelAndIgnoreRemainingEvents()
            }
        }
    }
}
