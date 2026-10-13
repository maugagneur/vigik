package com.kidor.vigik.ui.restapi

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.runComposeUiTest
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.kidor.vigik.data.apes.model.AvailabilityOut
import com.kidor.vigik.data.apes.model.MeOut
import com.kidor.vigik.data.apes.model.MemberAvailability
import com.kidor.vigik.data.apes.model.SlotOut
import com.kidor.vigik.data.apes.model.VoiceMember
import com.kidor.vigik.data.apes.model.VoiceOut
import com.kidor.vigik.utils.TestUtils.logTestName
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Integration tests for REST API (APES) screen components.
 */
@RunWith(AndroidJUnit4::class)
class RestApiViewTest {

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun checkLiveVoiceCardWhenEmpty() {
        logTestName()

        runComposeUiTest {
            setContent {
                LiveVoiceCard(voiceData = VoiceOut(now = "2026-10-13T21:00:00Z", members = emptyList()))
            }

            onNodeWithTag(VOICE_CARD_TEST_TAG).assertIsDisplayed()
            onNodeWithTag(VOICE_EMPTY_TEST_TAG).assertIsDisplayed()
            onNodeWithTag(VOICE_MEMBERS_TEST_TAG).assertDoesNotExist()
        }
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun checkLiveVoiceCardWithMembers() {
        logTestName()

        val members = listOf(
            VoiceMember(id = "1", displayName = "Arthur", since = "2026-10-13T20:30:00Z"),
            VoiceMember(id = "2", displayName = "Merlin", since = "2026-10-13T20:45:00Z")
        )

        runComposeUiTest {
            setContent {
                LiveVoiceCard(voiceData = VoiceOut(now = "2026-10-13T21:00:00Z", members = members))
            }

            onNodeWithTag(VOICE_CARD_TEST_TAG).assertIsDisplayed()
            onNodeWithTag(VOICE_MEMBERS_TEST_TAG).assertIsDisplayed()
            onNodeWithText("Arthur").assertIsDisplayed()
            onNodeWithText("Merlin").assertIsDisplayed()
        }
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun checkEveningTimelineCardWithSlots() {
        logTestName()

        val slots = listOf(
            SlotOut(local = "2026-10-13T21:00", startUtc = "2026-10-13T19:00:00Z", endUtc = "2026-10-13T19:30:00Z"),
            SlotOut(local = "2026-10-13T21:30", startUtc = "2026-10-13T19:30:00Z", endUtc = "2026-10-13T20:00:00Z")
        )
        val members = listOf(
            MemberAvailability(id = "1", displayName = "Arthur", statuses = listOf(100, 100)),
            MemberAvailability(id = "2", displayName = "Merlin", statuses = listOf(66, 100))
        )

        runComposeUiTest {
            setContent {
                EveningTimelineCard(
                    availabilityData = AvailabilityOut(
                        fromDate = "2026-10-13",
                        toDate = "2026-10-15",
                        timezone = "Europe/Paris",
                        slots = slots,
                        members = members
                    )
                )
            }

            onNodeWithTag(EVENING_CARD_TEST_TAG).assertIsDisplayed()
            onNodeWithTag(EVENING_PEAK_TEST_TAG).assertIsDisplayed()
        }
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun checkUserAvailabilityCardWhenNotConnected() {
        logTestName()

        runComposeUiTest {
            setContent {
                UserAvailabilityCard(
                    userToken = null,
                    meData = null,
                    isSubmitting = false,
                    onSaveToken = {},
                    onClearToken = {},
                    onApplyPreset = {}
                )
            }

            onNodeWithTag(TOKEN_CARD_TEST_TAG).assertIsDisplayed()
            onNodeWithTag(TOKEN_INPUT_TEST_TAG).assertIsDisplayed()
            onNodeWithTag(TOKEN_SAVE_BUTTON_TEST_TAG).assertIsDisplayed()
            onNodeWithTag(PRESET_PRESENT_BUTTON_TEST_TAG).assertDoesNotExist()
        }
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun checkUserAvailabilityCardWhenConnected() {
        logTestName()

        runComposeUiTest {
            setContent {
                UserAvailabilityCard(
                    userToken = "sj_valid_token",
                    meData = MeOut(id = "123", displayName = "Arthur"),
                    isSubmitting = false,
                    onSaveToken = {},
                    onClearToken = {},
                    onApplyPreset = {}
                )
            }

            onNodeWithTag(TOKEN_CARD_TEST_TAG).assertIsDisplayed()
            onNodeWithTag(TOKEN_INPUT_TEST_TAG).assertDoesNotExist()
            onNodeWithTag(PRESET_PRESENT_BUTTON_TEST_TAG).assertIsDisplayed()
            onNodeWithTag(PRESET_ABSENT_BUTTON_TEST_TAG).assertIsDisplayed()
            onNodeWithTag(PRESET_CLEAR_BUTTON_TEST_TAG).assertIsDisplayed()
        }
    }
}
