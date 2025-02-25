package com.kidor.vigik.ui.restapi

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import app.cash.turbine.test
import com.kidor.vigik.MainCoroutineRule
import com.kidor.vigik.R
import com.kidor.vigik.data.Localization
import com.kidor.vigik.data.diablo.Diablo4API
import com.kidor.vigik.data.diablo.model.Diablo4WorldBoss
import com.kidor.vigik.data.diablo.model.GetNextHellTideResponse
import com.kidor.vigik.data.diablo.model.GetNextWorldBossResponse
import com.kidor.vigik.utils.AssertUtils.assertEquals
import com.kidor.vigik.utils.AssertUtils.assertFalse
import com.kidor.vigik.utils.AssertUtils.assertNull
import com.kidor.vigik.utils.AssertUtils.assertTrue
import com.kidor.vigik.utils.TestUtils.logTestName
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import retrofit2.Response

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
    private lateinit var diablo4API: Diablo4API
    @MockK
    private lateinit var localization: Localization

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        every { localization.getString(R.string.duration_unit_hour, 1L) } returns "1 h"
        every { localization.getString(R.string.duration_unit_hour, 22L) } returns "22 h"
        every { localization.getString(R.string.duration_unit_minute, 17) } returns "17 min"
        every { localization.getString(R.string.duration_unit_minute, 41) } returns "41 min"
        every { localization.getString(R.string.duration_unit_minute, 42) } returns "42 min"
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun buildViewModel() {
        viewModel = RestApiViewModel(diablo4API, localization, mainCoroutineRule.testDispatcher)
    }

    @Test
    fun `test emitted state at start`() {
        logTestName()

        buildViewModel()

        // At start view state should have no valid data
        val initialState = viewModel.viewState.value
        assertNull(initialState?.diablo4TrackerData?.nextBoss, "Next boss")
        assertNull(initialState?.diablo4TrackerData?.timeUntilNextBoss, "Time until next boss")
        assertNull(initialState?.diablo4TrackerData?.timeUntilNextHellTide, "Time until next hell tide")

        // Check that network calls are made at start
        coVerify { diablo4API.getNextWorldBoss() }
        coVerify { diablo4API.getNextHellTide() }
    }

    @Test
    fun `test tracker data refresh frequency`() {
        logTestName()

        // Mock network calls
        coEvery { diablo4API.getNextWorldBoss() } returns Response.success(GetNextWorldBossResponse(name = "ashava", time = 1337))
        coEvery { diablo4API.getNextHellTide() } returns Response.success(GetNextHellTideResponse(time = 42))

        runTest {
            buildViewModel()

            // Check UI state
            val state = viewModel.viewState.value
            assertEquals(Diablo4WorldBoss.ASHAVA, state?.diablo4TrackerData?.nextBoss, "Next boss")
            assertEquals("22 h 17 min", state?.diablo4TrackerData?.timeUntilNextBoss, "Time until next boss")
            assertEquals("42 min", state?.diablo4TrackerData?.timeUntilNextHellTide, "Time until next hell tide")

            // Wait 5 minutes and 10 ms
            delay(5 * 60 * 1000 + 10)

            // World boss data should be fetched twice (every 5 minutes)
            coVerify(exactly = 2) { diablo4API.getNextWorldBoss() }
            // Hell tide data should be fetched 6 times (every minutes)
            coVerify(exactly = 6) { diablo4API.getNextHellTide() }

            // Manually stop refresh jobs
            viewModel.viewModelScope.cancel()
        }
    }

    @Test
    fun `test view state when getting world boss data returns an error`() {
        logTestName()

        // Mock network calls
        coEvery { diablo4API.getNextWorldBoss() } returns Response.error(
            500,
            "{\"message\": \"internal error\"}".toResponseBody("application/json".toMediaTypeOrNull())
        )
        coEvery { diablo4API.getNextHellTide() } returns Response.success(GetNextHellTideResponse(time = 42))

        runTest {
            buildViewModel()

            // Check UI state
            val state = viewModel.viewState.value
            assertNull(state?.diablo4TrackerData?.nextBoss, "Next boss")
            assertNull(state?.diablo4TrackerData?.timeUntilNextBoss, "Time until next boss")
            assertEquals("42 min", state?.diablo4TrackerData?.timeUntilNextHellTide, "Time until next hell tide")

            // Manually stop refresh jobs
            viewModel.viewModelScope.cancel()
        }
    }

    @Test
    fun `test view state when getting hell tide data returns an error`() {
        logTestName()

        // Mock network calls
        coEvery { diablo4API.getNextWorldBoss() } returns Response.success(GetNextWorldBossResponse(name = "ashava", time = 1337))
        coEvery { diablo4API.getNextHellTide() } returns Response.error(
            401,
            "{\"message\": \"unauthorized\"}".toResponseBody("application/json".toMediaTypeOrNull())
        )

        runTest {
            buildViewModel()

            // Check UI state
            val state = viewModel.viewState.value
            assertEquals(Diablo4WorldBoss.ASHAVA, state?.diablo4TrackerData?.nextBoss, "Next boss")
            assertEquals("22 h 17 min", state?.diablo4TrackerData?.timeUntilNextBoss, "Time until next boss")
            assertNull(state?.diablo4TrackerData?.timeUntilNextHellTide, "Time until next hell tide")

            // Manually stop refresh jobs
            viewModel.viewModelScope.cancel()
        }
    }

    @Test
    fun `test refresh behavior`() {
        logTestName()

        runTest {
            // Mock network calls
            coEvery { diablo4API.getNextWorldBoss() } returns Response.success(GetNextWorldBossResponse(name = "ashava", time = 1337))
            coEvery { diablo4API.getNextHellTide() } returns Response.success(GetNextHellTideResponse(time = 101))

            buildViewModel()

            viewModel.viewState.asFlow().test {
                // Check that network calls are made at start
                coVerify { diablo4API.getNextWorldBoss() }
                coVerify { diablo4API.getNextHellTide() }

                // Check UI state
                awaitItem().let { state ->
                    assertEquals(Diablo4WorldBoss.ASHAVA, state.diablo4TrackerData.nextBoss, "Next boss")
                    assertEquals("22 h 17 min", state.diablo4TrackerData.timeUntilNextBoss, "Time until next boss")
                    assertEquals("1 h 41 min", state.diablo4TrackerData.timeUntilNextHellTide, "Time until next hell tide")
                    assertFalse(state.isRefreshing, "Is refreshing")
                }

                // Wait 30 sec -> nothing should happen
                delay(30_000)
                expectNoEvents()

                // Mock network calls
                coEvery { diablo4API.getNextWorldBoss() } returns Response.success(GetNextWorldBossResponse(name = "avarice", time = 42))
                coEvery { diablo4API.getNextHellTide() } returns Response.success(GetNextHellTideResponse(time = 1337))

                // Trigger refresh
                viewModel.handleAction(RestApiViewAction.RefreshData)

                // Check that next UI state is "refreshing"
                assertTrue(awaitItem().isRefreshing, "Is refreshing")

                // Check that next UI state is not "refreshing" anymore
                expectMostRecentItem().let { state ->
                    assertEquals(Diablo4WorldBoss.AVARICE, state.diablo4TrackerData.nextBoss, "Next boss")
                    assertEquals("42 min", state.diablo4TrackerData.timeUntilNextBoss, "Time until next boss")
                    assertEquals("22 h 17 min", state.diablo4TrackerData.timeUntilNextHellTide, "Time until next hell tide")
                    assertFalse(state.isRefreshing, "Is refreshing")
                }

                // World boss and hell tide data should be fetched one more time
                coVerify(exactly = 2) { diablo4API.getNextWorldBoss() }
                coVerify(exactly = 2) { diablo4API.getNextHellTide() }

                // Manually stop refresh jobs
                viewModel.viewModelScope.cancel()

                cancelAndIgnoreRemainingEvents()
            }
        }
    }
}
