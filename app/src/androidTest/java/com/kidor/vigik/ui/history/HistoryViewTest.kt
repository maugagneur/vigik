package com.kidor.vigik.ui.history

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.kidor.vigik.R
import com.kidor.vigik.db.TagRepository
import com.kidor.vigik.extensions.launchFragmentInHiltContainer
import com.kidor.vigik.nfc.model.Tag
import com.kidor.vigik.utils.EspressoUtils.checkViewIsNotVisible
import com.kidor.vigik.utils.EspressoUtils.checkViewIsVisible
import com.kidor.vigik.utils.TestUtils.logTestName
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

/**
 * Integration tests for [HistoryFragment].
 */
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class HistoryViewTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var repository: TagRepository

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @Test
    fun checkUiElementsWhenInitializing() {
        logTestName()

        // Load fragment in empty fragment activity and force state `Initializing`
        launchFragmentInHiltContainer<HistoryFragment> { fragment ->
            fragment.forceState(HistoryViewState.Initializing)
        }

        // Check that the recyclerview of tags is hidden
        checkViewIsNotVisible(R.id.tag_history_recyclerview, "History RecyclerView")

        // Check that the textview for `no-data` is hidden
        checkViewIsNotVisible(R.id.no_data_textview, "No-data TextView")
    }

    @Test
    fun checkUiElementsAtStartWhenThereIsNoTagFromDatabase() {
        logTestName()

        // Load fragment in empty fragment activity
        launchFragmentInHiltContainer<HistoryFragment>()

        // Check that the recyclerview of tags is hidden
        checkViewIsNotVisible(R.id.tag_history_recyclerview, "History RecyclerView")

        // Check that the textview for `no-data` is hidden
        checkViewIsVisible(R.id.no_data_textview, "No-data TextView")
    }

    @Test
    fun checkUiElementsAtStartWhenThereIsTagsFromDatabase() {
        logTestName()

        // Insert empty tag in database before launching screen
        runBlocking {
            repository.insert(Tag())
        }

        // Load fragment in empty fragment activity
        launchFragmentInHiltContainer<HistoryFragment>()

        // Check that the recyclerview of tags is hidden
        checkViewIsVisible(R.id.tag_history_recyclerview, "History RecyclerView")

        // Check that the textview for `no-data` is hidden
        checkViewIsNotVisible(R.id.no_data_textview, "No-data TextView")
    }
}