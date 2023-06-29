package com.kidor.vigik.ui.nfc.history

import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertRangeInfoEquals
import androidx.compose.ui.test.filter
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.onChildren
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.kidor.vigik.R
import com.kidor.vigik.extensions.onNodeWithText
import com.kidor.vigik.data.nfc.model.Tag
import com.kidor.vigik.utils.TestUtils.logTestName
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations
import java.util.Calendar

/**
 * Integration tests for History screen.
 */
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class HistoryViewTest {

    private lateinit var closeable: AutoCloseable

    @Mock
    private lateinit var viewActionCallback: (Tag) -> Unit

    @Before
    fun setUp() {
        closeable = MockitoAnnotations.openMocks(this)
    }

    @After
    fun tearDown() {
        closeable.close()
    }

    @ExperimentalTestApi
    @Test
    fun checkLoadingState() {
        logTestName()

        runComposeUiTest {
            setContent {
                LoadingState()
            }

            // Check that loader is visible
            onNodeWithTag(PROGRESS_BAR_TEST_TAG)
                .assertIsDisplayed()
                .assertRangeInfoEquals(ProgressBarRangeInfo.Indeterminate)

            // Check that the list of tags is hidden
            onNodeWithTag(TAGS_LIST_TEST_TAG)
                .assertDoesNotExist()

            // Check that the text for `no-data` is hidden
            onNodeWithText(stringResourceId = R.string.no_data_label)
                .assertDoesNotExist()
        }
    }

    @ExperimentalTestApi
    @Test
    fun checkUiElementsWhenThereIsNoTag() {
        logTestName()

        runComposeUiTest {
            setContent {
                DisplayTags(DisplayTagsStateData(emptyList()))
            }

            // Check that loader is not visible
            onNodeWithTag(PROGRESS_BAR_TEST_TAG)
                .assertDoesNotExist()

            // Check that the list of tags is hidden
            onNodeWithTag(TAGS_LIST_TEST_TAG)
                .assertDoesNotExist()

            // Check that the textview for `no-data` is visible
            onNodeWithText(stringResourceId = R.string.no_data_label)
                .assertIsDisplayed()
        }
    }

    @ExperimentalTestApi
    @Test
    fun checkUiElementsWhenThereIsTags() {
        logTestName()

        val firstTag = Tag()
        val secondTag = Tag(timestamp = 42)
        val now = Calendar.getInstance().timeInMillis
        val thirdTag = Tag(timestamp = now)
        val tagList = listOf(firstTag, secondTag, thirdTag)

        runComposeUiTest {
            setContent {
                DisplayTags(
                    DisplayTagsStateData(
                        tagList,
                        viewActionCallback
                    )
                )
            }

            // Check that loader is not visible
            onNodeWithTag(PROGRESS_BAR_TEST_TAG)
                .assertDoesNotExist()

            // Check that the list of tags is visible with all elements
            onNodeWithTag(TAGS_LIST_TEST_TAG)
                .assertIsDisplayed()
                .onChildren()
                .filter(hasTestTag(TAGS_LIST_ROW_TEST_TAG))
                .assertCountEquals(tagList.size)

            // Check that the textview for `no-data` is visible
            onNodeWithText(stringResourceId = R.string.no_data_label)
                .assertDoesNotExist()

            // Check that a click on delete button generates a DeleteTag action with right value
            onNodeWithTag(DELETE_ICON_TEST_TAG_PREFIX + 0)
                .performClick()
            verify(viewActionCallback).invoke(firstTag)
            onNodeWithTag(DELETE_ICON_TEST_TAG_PREFIX + 42)
                .performClick()
            verify(viewActionCallback).invoke(secondTag)
            onNodeWithTag(DELETE_ICON_TEST_TAG_PREFIX + now)
                .performClick()
            verify(viewActionCallback).invoke(thirdTag)
        }
    }
}
