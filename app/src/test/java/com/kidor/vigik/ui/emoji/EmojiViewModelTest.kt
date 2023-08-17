package com.kidor.vigik.ui.emoji

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.kidor.vigik.MainCoroutineRule
import com.kidor.vigik.data.PreferencesKeys
import com.kidor.vigik.utils.AssertUtils.assertEquals
import com.kidor.vigik.utils.AssertUtils.assertTrue
import com.kidor.vigik.utils.TestUtils.logTestName
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Unit tests for [EmojiViewModel].
 */
class EmojiViewModelTest {

    @ExperimentalCoroutinesApi
    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: EmojiViewModel

    @MockK
    private lateinit var dataStore: DataStore<Preferences>
    @MockK
    private lateinit var preferences: Preferences

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        every { dataStore.data } returns flow { emit(preferences) }
        coEvery { dataStore.updateData(any()) } returns preferences
        every { preferences[PreferencesKeys.EMOJI_PICKED] } returns null
    }

    @Test
    fun `test view state shows default emoji at start when no emoji previously saved`() {
        logTestName()

        // Start view model
        viewModel = EmojiViewModel(preferences = dataStore)

        // Check view state
        viewModel.viewState.value.let { viewState ->
            if (viewState is EmojiViewState.SelectedEmoji) {
                assertEquals("❔", viewState.emoji, "Selected Emoji")
            } else {
                fail("Unexpected view state")
            }
        }
    }

    @Test
    fun `test view state shows saved emoji at start when previous emoji exists`() {
        logTestName()

        // Set a previously saved Emoji
        every { preferences[PreferencesKeys.EMOJI_PICKED] } returns "🛠️"

        // Start view model
        viewModel = EmojiViewModel(preferences = dataStore)

        // Check view state
        viewModel.viewState.value.let { viewState ->
            if (viewState is EmojiViewState.SelectedEmoji) {
                assertEquals("🛠️", viewState.emoji, "Selected Emoji")
            } else {
                fail("Unexpected view state")
            }
        }
    }

    @Test
    fun `test emoji picker displayed when clicking on previous Emoji`() {
        logTestName()

        // Start view model
        viewModel = EmojiViewModel(preferences = dataStore)

        // Check that view state should display the last Emoji selected
        var viewState = viewModel.viewState.value
        assertTrue(viewState is EmojiViewState.SelectedEmoji, "View state")

        // Click on Emoji
        viewModel.handleAction(EmojiViewAction.ClickOnEmoji)

        // Check that view should display Emoji picker
        viewState = viewModel.viewState.value
        assertTrue(viewState is EmojiViewState.EmojiPicker, "View state")
    }

    @Test
    fun `test emoji displayed and saved in persistent store when selected through emoji picker`() {
        logTestName()

        val testEmoji = "FakeEmojiValue"

        // Start view model
        viewModel = EmojiViewModel(preferences = dataStore)

        // Select an Emoji
        viewModel.handleAction(EmojiViewAction.ChangeSelectedEmoji(testEmoji))

        // Check that view should display the selected Emoji
        viewModel.viewState.value.let { viewState ->
            if (viewState is EmojiViewState.SelectedEmoji) {
                assertEquals(testEmoji, viewState.emoji, "Selected Emoji")
            } else {
                fail("Unexpected view state")
            }
        }
        // Check that persistent storage is updated
        coVerify { dataStore.updateData(any()) }
    }
}
