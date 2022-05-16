package com.kidor.vigik.ui.history

import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertRangeInfoEquals
import androidx.compose.ui.test.filter
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onChildren
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.kidor.vigik.R
import com.kidor.vigik.extensions.onNodeWithText
import com.kidor.vigik.nfc.model.Tag
import com.kidor.vigik.utils.TestUtils.logTestName
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations
import java.util.Calendar

/**
 * Integration tests for [HistoryFragment].
 */
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class HistoryViewTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var closeable: AutoCloseable

    @Mock
    private lateinit var viewActionCallback: (HistoryViewAction) -> Unit

    @Before
    fun setUp() {
        closeable = MockitoAnnotations.openMocks(this)
    }

    @After
    fun tearDown() {
        closeable.close()
    }

    @Test
    fun checkLoadingState() {
        logTestName()

        composeTestRule.setContent {
            LoadingState()
        }

        // Check that loader is visible
        composeTestRule
            .onNodeWithTag(PROGRESS_BAR_TEST_TAG)
            .assertIsDisplayed()
            .assertRangeInfoEquals(ProgressBarRangeInfo.Indeterminate)

        // Check that the list of tags is hidden
        composeTestRule
            .onNodeWithTag(TAGS_LIST_TEST_TAG)
            .assertDoesNotExist()

        // Check that the text for `no-data` is hidden
        composeTestRule
            .onNodeWithText(stringResourceId = R.string.no_data_label)
            .assertDoesNotExist()
    }

    @Test
    fun checkUiElementsWhenThereIsNoTag() {
        logTestName()

        composeTestRule.setContent {
            DisplayTags(DisplayTagsStateData(emptyList()))
        }

        // Check that loader is not visible
        composeTestRule
            .onNodeWithTag(PROGRESS_BAR_TEST_TAG)
            .assertDoesNotExist()

        // Check that the list of tags is hidden
        composeTestRule
            .onNodeWithTag(TAGS_LIST_TEST_TAG)
            .assertDoesNotExist()

        // Check that the textview for `no-data` is visible
        composeTestRule
            .onNodeWithText(stringResourceId = R.string.no_data_label)
            .assertIsDisplayed()
    }

    @Test
    fun checkUiElementsWhenThereIsTags() {
        logTestName()

        val firstTag = Tag()
        val secondTag = Tag(timestamp = 42)
        val now = Calendar.getInstance().timeInMillis
        val thirdTag = Tag(timestamp = now)
        val tagList = listOf(firstTag, secondTag, thirdTag)

        composeTestRule.setContent {
            DisplayTags(DisplayTagsStateData(
                tagList,
                viewActionCallback
            ))
        }

        // Check that loader is not visible
        composeTestRule
            .onNodeWithTag(PROGRESS_BAR_TEST_TAG)
            .assertDoesNotExist()

        // Check that the list of tags is visible with all elements
        composeTestRule
            .onNodeWithTag(TAGS_LIST_TEST_TAG)
            .assertIsDisplayed()
            .onChildren()
            .filter(hasTestTag(TAGS_LIST_ROW_TEST_TAG))
            .assertCountEquals(tagList.size)

        // Check that the textview for `no-data` is visible
        composeTestRule
            .onNodeWithText(stringResourceId = R.string.no_data_label)
            .assertDoesNotExist()

        // Check that a click on delete button generates a DeleteTag action with right value
        composeTestRule
            .onNodeWithTag(DELETE_ICON_TEST_TAG_PREFIX + 0)
            .performClick()
        verify(viewActionCallback).invoke(HistoryViewAction.DeleteTag(firstTag))
        composeTestRule
            .onNodeWithTag(DELETE_ICON_TEST_TAG_PREFIX + 42)
            .performClick()
        verify(viewActionCallback).invoke(HistoryViewAction.DeleteTag(secondTag))
        composeTestRule
            .onNodeWithTag(DELETE_ICON_TEST_TAG_PREFIX + now)
            .performClick()
        verify(viewActionCallback).invoke(HistoryViewAction.DeleteTag(thirdTag))
    }
}