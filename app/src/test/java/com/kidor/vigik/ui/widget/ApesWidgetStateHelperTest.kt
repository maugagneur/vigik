package com.kidor.vigik.ui.widget

import androidx.datastore.preferences.core.MutablePreferences
import com.kidor.vigik.data.PreferencesKeys
import com.kidor.vigik.utils.AssertUtils.assertEquals
import com.kidor.vigik.utils.AssertUtils.assertFalse
import com.kidor.vigik.utils.AssertUtils.assertTrue
import com.kidor.vigik.utils.TestUtils.logTestName
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for [ApesWidgetStateHelper].
 */
class ApesWidgetStateHelperTest {

    @MockK
    private lateinit var preferences: MutablePreferences

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)
    }

    @Test
    fun `test getState() behavior when preferences are empty`() {
        logTestName()

        every { preferences[PreferencesKeys.WIDGET_APES_IS_LOADING] } returns null
        every { preferences[PreferencesKeys.WIDGET_APES_VOICE_COUNT] } returns null
        every { preferences[PreferencesKeys.WIDGET_APES_VOICE_MEMBERS] } returns null
        every { preferences[PreferencesKeys.WIDGET_APES_EVENING_SUMMARY] } returns null

        val state = ApesWidgetStateHelper.getState(preferences)
        assertFalse(state.isLoading, "Is loading")
        assertEquals(0, state.data.voiceCount, "Voice count")
        assertEquals("", state.data.voiceMembers, "Voice members")
        assertEquals("", state.data.eveningSummary, "Evening summary")
    }

    @Test
    fun `test getState() behavior with values`() {
        logTestName()

        every { preferences[PreferencesKeys.WIDGET_APES_IS_LOADING] } returns true
        every { preferences[PreferencesKeys.WIDGET_APES_VOICE_COUNT] } returns 3
        every { preferences[PreferencesKeys.WIDGET_APES_VOICE_MEMBERS] } returns "Arthur, Merlin, Perceval"
        every { preferences[PreferencesKeys.WIDGET_APES_EVENING_SUMMARY] } returns "Pic à 4 joueurs à 21h30"

        val state = ApesWidgetStateHelper.getState(preferences)
        assertTrue(state.isLoading, "Is loading")
        assertEquals(3, state.data.voiceCount, "Voice count")
        assertEquals("Arthur, Merlin, Perceval", state.data.voiceMembers, "Voice members")
        assertEquals("Pic à 4 joueurs à 21h30", state.data.eveningSummary, "Evening summary")
    }

    @Test
    fun `test setLoading() behavior`() {
        logTestName()

        ApesWidgetStateHelper.setLoading(preferences, false)
        verify { preferences[PreferencesKeys.WIDGET_APES_IS_LOADING] = false }

        ApesWidgetStateHelper.setLoading(preferences, true)
        verify { preferences[PreferencesKeys.WIDGET_APES_IS_LOADING] = true }
    }

    @Test
    fun `test setData() behavior`() {
        logTestName()

        ApesWidgetStateHelper.setData(
            preferences = preferences,
            voiceCount = 2,
            voiceMembers = "Arthur, Merlin",
            eveningSummary = "Pic à 3 joueurs à 22h00"
        )

        verify { preferences[PreferencesKeys.WIDGET_APES_VOICE_COUNT] = 2 }
        verify { preferences[PreferencesKeys.WIDGET_APES_VOICE_MEMBERS] = "Arthur, Merlin" }
        verify { preferences[PreferencesKeys.WIDGET_APES_EVENING_SUMMARY] = "Pic à 3 joueurs à 22h00" }
        verify { preferences[PreferencesKeys.WIDGET_APES_IS_LOADING] = false }
    }
}
