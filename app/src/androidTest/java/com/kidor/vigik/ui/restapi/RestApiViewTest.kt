package com.kidor.vigik.ui.restapi

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.runComposeUiTest
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.kidor.vigik.data.diablo.model.Diablo4WorldBoss
import com.kidor.vigik.utils.TestUtils.logTestName
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Integration tests for REST API screen.
 */
@RunWith(AndroidJUnit4::class)
class RestApiViewTest {

    @ExperimentalTestApi
    @Test
    fun checkTrackerViewWhenNoData() {
        logTestName()

        runComposeUiTest {
            setContent {
                Diablo4Tracker(
                    diablo4TrackerData = Diablo4TrackerData(
                        nextBoss = null,
                        timeUntilNextBoss = null,
                        timeUntilNextHellTide = null
                    )
                )
            }

            // Check visibility
            onNodeWithTag(WORLD_BOSS_ICON_TEST_TAG).assertIsDisplayed()
            onNodeWithTag(WORLD_BOSS_NAME_TEST_TAG).assertDoesNotExist()
            onNodeWithTag(WORLD_BOSS_TIME_TEST_TAG).assertDoesNotExist()
            onNodeWithTag(HELL_TIDE_ICON_TEST_TAG).assertDoesNotExist()
            onNodeWithTag(HELL_TIDE_TIME_TEST_TAG).assertDoesNotExist()
        }
    }

    @ExperimentalTestApi
    @Test
    fun checkTrackerViewWhenWorldBossIsUnknown() {
        logTestName()

        runComposeUiTest {
            setContent {
                Diablo4Tracker(
                    diablo4TrackerData = Diablo4TrackerData(
                        nextBoss = Diablo4WorldBoss.UNKNOWN,
                        timeUntilNextBoss = "5 min",
                        timeUntilNextHellTide = null
                    )
                )
            }

            // Check visibility
            onNodeWithTag(WORLD_BOSS_ICON_TEST_TAG).assertIsDisplayed()
            onNodeWithTag(WORLD_BOSS_NAME_TEST_TAG).assertIsDisplayed()
            onNodeWithTag(WORLD_BOSS_TIME_TEST_TAG).assertIsDisplayed()
            onNodeWithTag(HELL_TIDE_ICON_TEST_TAG).assertDoesNotExist()
            onNodeWithTag(HELL_TIDE_TIME_TEST_TAG).assertDoesNotExist()
        }
    }

    @ExperimentalTestApi
    @Test
    fun checkTrackerViewWhenOnlyHaveHellTide() {
        logTestName()

        runComposeUiTest {
            setContent {
                Diablo4Tracker(
                    diablo4TrackerData = Diablo4TrackerData(
                        nextBoss = null,
                        timeUntilNextBoss = null,
                        timeUntilNextHellTide = "42 min"
                    )
                )
            }

            // Check visibility
            onNodeWithTag(WORLD_BOSS_ICON_TEST_TAG).assertIsDisplayed()
            onNodeWithTag(WORLD_BOSS_NAME_TEST_TAG).assertDoesNotExist()
            onNodeWithTag(WORLD_BOSS_TIME_TEST_TAG).assertDoesNotExist()
            onNodeWithTag(HELL_TIDE_ICON_TEST_TAG).assertIsDisplayed()
            onNodeWithTag(HELL_TIDE_TIME_TEST_TAG).assertIsDisplayed()
        }
    }

    @ExperimentalTestApi
    @Test
    fun checkTrackerViewWithAllData() {
        logTestName()

        runComposeUiTest {
            setContent {
                Diablo4Tracker(
                    diablo4TrackerData = Diablo4TrackerData(
                        nextBoss = Diablo4WorldBoss.WANDERING_DEATH,
                        timeUntilNextBoss = "7 h 06 min",
                        timeUntilNextHellTide = "13 min"
                    )
                )
            }

            // Check visibility
            onNodeWithTag(WORLD_BOSS_ICON_TEST_TAG).assertIsDisplayed()
            onNodeWithTag(WORLD_BOSS_NAME_TEST_TAG).assertIsDisplayed()
            onNodeWithTag(WORLD_BOSS_TIME_TEST_TAG).assertIsDisplayed()
            onNodeWithTag(HELL_TIDE_ICON_TEST_TAG).assertIsDisplayed()
            onNodeWithTag(HELL_TIDE_TIME_TEST_TAG).assertIsDisplayed()
        }
    }
}
