package com.kidor.vigik.ui.widget

import androidx.datastore.preferences.core.MutablePreferences
import com.kidor.vigik.R
import com.kidor.vigik.data.Localization
import com.kidor.vigik.data.PreferencesKeys
import com.kidor.vigik.data.diablo.model.Diablo4WorldBoss
import com.kidor.vigik.utils.AssertUtils.assertEquals
import com.kidor.vigik.utils.AssertUtils.assertFalse
import com.kidor.vigik.utils.AssertUtils.assertTrue
import com.kidor.vigik.utils.SystemWrapper
import com.kidor.vigik.utils.TestUtils.logTestName
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.junit.Before
import org.junit.Test
import java.util.Calendar

/**
 * Unit tests for [DiabloWidgetStateHelper].
 */
class DiabloWidgetStateHelperTest {

    @MockK
    private lateinit var preferences: MutablePreferences
    @MockK
    private lateinit var localization: Localization
    @MockK
    private lateinit var systemWrapper: SystemWrapper

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        every { localization.getString(R.string.widget_diablo_boss_spawn_date_pattern) } returns "EEE d MMM \'at\' h:mm a"
    }

    @Test
    fun `test getState() behavior`() {
        logTestName()

        // Test when state was never set before
        every { preferences[PreferencesKeys.WIDGET_DIABLO_IS_LOADING] } returns null
        every { preferences[PreferencesKeys.WIDGET_DIABLO_BOSS_NAME] } returns null
        every { preferences[PreferencesKeys.WIDGET_DIABLO_BOSS_SPAWN_DATE] } returns null

        // Get state
        DiabloWidgetStateHelper.getState(preferences).let { state ->
            assertFalse(state.isLoading, "Is loading")
            assertEquals(Diablo4WorldBoss.UNKNOWN, state.data.worldBoss, "World boss")
            assertEquals("", state.data.spawnDate, "Spawn date")
        }

        // Test when an unknown boss is planned
        every { preferences[PreferencesKeys.WIDGET_DIABLO_IS_LOADING] } returns false
        every { preferences[PreferencesKeys.WIDGET_DIABLO_BOSS_NAME] } returns "new boss"
        every { preferences[PreferencesKeys.WIDGET_DIABLO_BOSS_SPAWN_DATE] } returns "Mon. 10 july at 1:16 PM"

        // Get state
        DiabloWidgetStateHelper.getState(preferences).let { state ->
            assertFalse(state.isLoading, "Is loading")
            assertEquals(Diablo4WorldBoss.UNKNOWN, state.data.worldBoss, "World boss")
            assertEquals("Mon. 10 july at 1:16 PM", state.data.spawnDate, "Spawn date")
        }

        // Test when fetching new data
        every { preferences[PreferencesKeys.WIDGET_DIABLO_IS_LOADING] } returns true
        every { preferences[PreferencesKeys.WIDGET_DIABLO_BOSS_NAME] } returns "avarice"
        every { preferences[PreferencesKeys.WIDGET_DIABLO_BOSS_SPAWN_DATE] } returns "Mon. 10 july at 1:16 PM"

        // Get state
        DiabloWidgetStateHelper.getState(preferences).let { state ->
            assertTrue(state.isLoading, "Is loading")
            assertEquals(Diablo4WorldBoss.AVARICE, state.data.worldBoss, "World boss")
            assertEquals("Mon. 10 july at 1:16 PM", state.data.spawnDate, "Spawn date")
        }
    }

    @Test
    fun `test setLoading() behavior`() {
        logTestName()

        // Set loading state to false
        DiabloWidgetStateHelper.setLoading(preferences, false)
        // Check that the appropriated preference is set to false
        verify { preferences[PreferencesKeys.WIDGET_DIABLO_IS_LOADING] = false }

        // Set loading state to true
        DiabloWidgetStateHelper.setLoading(preferences, true)
        // Check that the appropriated preference is set to true
        verify { preferences[PreferencesKeys.WIDGET_DIABLO_IS_LOADING] = true }
    }

    @Test
    fun `test setData() behavior`() {
        logTestName()

        val now = Calendar.getInstance().apply {
            this[Calendar.YEAR] = 2023
            this[Calendar.MONTH] = Calendar.JULY
            this[Calendar.DAY_OF_MONTH] = 10
            this[Calendar.HOUR] = 9
            this[Calendar.MINUTE] = 28
        }
        every { systemWrapper.currentTimeMillis() } returns now.timeInMillis

        // Test with missing boss name
        DiabloWidgetStateHelper.setData(
            preferences = preferences,
            bossName = null,
            spawnDelay = 42,
            localization = localization,
            system = systemWrapper
        )
        // Check that the appropriated preferences are set
        verify { preferences[PreferencesKeys.WIDGET_DIABLO_BOSS_NAME] = "" }
        verify { preferences[PreferencesKeys.WIDGET_DIABLO_BOSS_SPAWN_DATE] = "Lun. 10 juil. at 10:10 PM" }
        // Check that loading state is set to false
        verify { preferences[PreferencesKeys.WIDGET_DIABLO_IS_LOADING] = false }

        // Test with valid data
        DiabloWidgetStateHelper.setData(
            preferences = preferences,
            bossName = "ashava",
            spawnDelay = 42,
            localization = localization,
            system = systemWrapper
        )
        // Check that the appropriated preferences are set
        verify { preferences[PreferencesKeys.WIDGET_DIABLO_BOSS_NAME] = "ashava" }
        verify { preferences[PreferencesKeys.WIDGET_DIABLO_BOSS_SPAWN_DATE] = "Lun. 10 juil. at 10:10 PM" }
        // Check that loading state is set to false
        verify { preferences[PreferencesKeys.WIDGET_DIABLO_IS_LOADING] = false }
    }
}
