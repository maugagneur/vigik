package com.kidor.vigik.ui.restapi

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.asFlow
import app.cash.turbine.test
import com.kidor.vigik.MainCoroutineRule
import com.kidor.vigik.R
import com.kidor.vigik.data.Localization
import com.kidor.vigik.data.apes.ApesRepository
import com.kidor.vigik.data.apes.AvailabilityPreset
import com.kidor.vigik.data.apes.DEFAULT_PRESET_END_TIME
import com.kidor.vigik.data.apes.DEFAULT_PRESET_START_TIME
import com.kidor.vigik.data.apes.model.AvailabilityOut
import com.kidor.vigik.data.apes.model.DayOut
import com.kidor.vigik.data.apes.model.MeOut
import com.kidor.vigik.data.apes.model.VoiceOut
import com.kidor.vigik.utils.AssertUtils.assertEquals
import com.kidor.vigik.utils.AssertUtils.assertFalse
import com.kidor.vigik.utils.AssertUtils.assertNotNull
import com.kidor.vigik.utils.AssertUtils.assertNull
import com.kidor.vigik.utils.AssertUtils.assertTrue
import com.kidor.vigik.utils.NetworkResult
import com.kidor.vigik.utils.TestUtils.logTestName
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Unit tests for [RestApiViewModel].
 */
class RestApiViewModelTest {

    @ExperimentalCoroutinesApi
    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: RestApiViewModel

    @MockK
    private lateinit var apesRepository: ApesRepository
    @MockK
    private lateinit var localization: Localization

    private val userTokenFlow = MutableStateFlow<String?>(null)
    private val presetStartTimeFlow = MutableStateFlow(DEFAULT_PRESET_START_TIME)
    private val presetEndTimeFlow = MutableStateFlow(DEFAULT_PRESET_END_TIME)

    private val dummyVoice = VoiceOut(now = "2026-10-13T20:00:00Z", members = emptyList())
    private val dummyAvail = AvailabilityOut(
        fromDate = "2026-10-13",
        toDate = "2026-10-15",
        timezone = "Europe/Paris",
        slots = emptyList(),
        members = emptyList()
    )
    private val dummyMe = MeOut(id = "user123", displayName = "Arthur")

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        every { apesRepository.getUserToken() } returns userTokenFlow
        every { apesRepository.getPresetStartTime() } returns presetStartTimeFlow
        every { apesRepository.getPresetEndTime() } returns presetEndTimeFlow
        coEvery { apesRepository.getVoice() } returns NetworkResult.Success(200, dummyVoice)
        coEvery { apesRepository.getAvailability(any(), any()) } returns NetworkResult.Success(200, dummyAvail)
        coEvery { apesRepository.getMe(any()) } returns NetworkResult.Success(200, dummyMe)
        coEvery { apesRepository.saveUserToken(any()) } answers {
            userTokenFlow.value = firstArg()
        }
        coEvery { apesRepository.clearUserToken() } answers {
            userTokenFlow.value = null
        }
        coEvery { apesRepository.savePresetTimes(any(), any()) } answers {
            presetStartTimeFlow.value = firstArg()
            presetEndTimeFlow.value = secondArg()
        }
        every { localization.getString(R.string.apes_dispo_updated_success) } returns "Dispos mises à jour !"
        every { localization.getString(R.string.apes_dispo_update_error, any()) } returns "Erreur dispo"
        every { localization.getString(R.string.apes_preset_times_updated_success) } returns "Horaires du preset mis à jour"
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun buildViewModel() {
        viewModel = RestApiViewModel(
            apesRepository = apesRepository,
            localization = localization,
            ioDispatcher = mainCoroutineRule.testDispatcher
        )
    }

    @Test
    fun `test initial state and fetch data`() = runTest {
        logTestName()

        buildViewModel()

        val state = viewModel.viewState.value
        assertNotNull(state, "ViewState")
        assertEquals(dummyVoice, state?.voiceData, "Voice data")
        assertEquals(dummyAvail, state?.availabilityData, "Availability data")
        assertFalse(state?.isRefreshing == true, "Is refreshing should be false after init")

        coVerify { apesRepository.getVoice() }
        coVerify { apesRepository.getAvailability(any(), any()) }
    }

    @Test
    fun `test user token observation fetches profile`() = runTest {
        logTestName()

        userTokenFlow.value = "sj_test_token"
        buildViewModel()

        val state = viewModel.viewState.value
        assertEquals("sj_test_token", state?.userToken, "User token")
        assertEquals(dummyMe, state?.meData, "Me data")

        coVerify { apesRepository.getMe("sj_test_token") }
    }

    @Test
    fun `test refresh action`() = runTest {
        logTestName()

        buildViewModel()

        viewModel.viewState.asFlow().test {
            awaitItem() // initial state

            viewModel.handleAction(RestApiViewAction.RefreshData)

            val refreshingState = awaitItem()
            assertTrue(refreshingState.isRefreshing, "Should be refreshing")

            val completedState = awaitItem()
            assertFalse(completedState.isRefreshing, "Should not be refreshing")
            assertEquals(dummyVoice, completedState.voiceData, "Voice data")
            assertEquals(dummyAvail, completedState.availabilityData, "Availability data")

            coVerify(atLeast = 2) { apesRepository.getVoice() }
            coVerify(atLeast = 2) { apesRepository.getAvailability(any(), any()) }

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `test save user token`() = runTest {
        logTestName()

        buildViewModel()

        viewModel.handleAction(RestApiViewAction.SaveUserToken("sj_new_token"))

        coVerify { apesRepository.saveUserToken("sj_new_token") }
        coVerify { apesRepository.getMe("sj_new_token") }
    }

    @Test
    fun `test clear user token`() = runTest {
        logTestName()

        userTokenFlow.value = "sj_token"
        buildViewModel()

        viewModel.handleAction(RestApiViewAction.ClearUserToken)

        coVerify { apesRepository.clearUserToken() }
        assertNull(viewModel.viewState.value?.userToken, "User token should be cleared")
        assertNull(viewModel.viewState.value?.meData, "Me data should be cleared")
    }

    @Test
    fun `test apply preset success refreshes data and shows success message`() = runTest {
        logTestName()

        coEvery { apesRepository.applyPreset(any(), AvailabilityPreset.PRESENT_TONIGHT) } returns NetworkResult.Success(
            200,
            DayOut(date = "2026-10-13", ranges = emptyList())
        )

        buildViewModel()

        viewModel.handleAction(RestApiViewAction.ApplyPreset(AvailabilityPreset.PRESENT_TONIGHT))

        val state = viewModel.viewState.value
        assertEquals("Dispos mises à jour !", state?.userMessage, "User message")
        assertFalse(state?.isSubmittingPreset == true, "Is submitting preset")

        // Verify data was fetched again after preset
        coVerify(atLeast = 2) { apesRepository.getAvailability(any(), any()) }
    }

    @Test
    fun `test apply preset error sets error message`() = runTest {
        logTestName()

        coEvery { apesRepository.applyPreset(any(), AvailabilityPreset.ABSENT_TONIGHT) } returns NetworkResult.Error(
            code = 400,
            errorMessage = "Invalid range"
        )

        buildViewModel()

        viewModel.handleAction(RestApiViewAction.ApplyPreset(AvailabilityPreset.ABSENT_TONIGHT))

        val state = viewModel.viewState.value
        assertEquals("Erreur dispo", state?.userMessage, "User message")
        assertFalse(state?.isSubmittingPreset == true, "Is submitting preset")
    }

    @Test
    fun `test dismiss message clears messages`() = runTest {
        logTestName()

        coEvery { apesRepository.applyPreset(any(), AvailabilityPreset.PRESENT_TONIGHT) } returns NetworkResult.Success(
            200,
            DayOut(date = "2026-10-13", ranges = emptyList())
        )

        buildViewModel()
        viewModel.handleAction(RestApiViewAction.ApplyPreset(AvailabilityPreset.PRESENT_TONIGHT))
        assertNotNull(viewModel.viewState.value?.userMessage, "User message present")

        viewModel.handleAction(RestApiViewAction.DismissMessage)
        assertNull(viewModel.viewState.value?.userMessage, "User message cleared")
    }

    @Test
    fun `test save preset times updates state and repository`() = runTest {
        logTestName()

        buildViewModel()

        viewModel.handleAction(RestApiViewAction.SavePresetTimes("20:00", "23:30"))

        coVerify { apesRepository.savePresetTimes("20:00", "23:30") }
        assertEquals("Horaires du preset mis à jour", viewModel.viewState.value?.userMessage, "User message")
        assertEquals("20:00", viewModel.viewState.value?.presetStartTime, "Preset start time")
        assertEquals("23:30", viewModel.viewState.value?.presetEndTime, "Preset end time")
    }
}
