package com.kidor.vigik.db

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.kidor.vigik.MainCoroutineRule
import com.kidor.vigik.db.model.RoomTag
import com.kidor.vigik.extensions.toTagList
import com.kidor.vigik.nfc.model.Tag
import com.kidor.vigik.utils.AssertUtils.assertEquals
import com.kidor.vigik.utils.TestUtils.logTestName
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.RelaxedMockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class TagRepositoryTest {

    @ExperimentalCoroutinesApi
    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var repository: TagRepository

    @RelaxedMockK
    lateinit var tagDao: TagDao

    @Before
    fun setup() {
        MockKAnnotations.init(this)
    }

    @ExperimentalCoroutinesApi
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

    @ExperimentalCoroutinesApi
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

    @ExperimentalCoroutinesApi
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
