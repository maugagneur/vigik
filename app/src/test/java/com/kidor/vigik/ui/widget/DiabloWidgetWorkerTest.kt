package com.kidor.vigik.ui.widget

import android.content.Context
import androidx.datastore.preferences.core.MutablePreferences
import androidx.glance.GlanceId
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters
import androidx.work.impl.utils.taskexecutor.SerialExecutor
import androidx.work.impl.utils.taskexecutor.TaskExecutor
import com.kidor.vigik.data.Localization
import com.kidor.vigik.data.diablo.Diablo4API
import com.kidor.vigik.data.diablo.model.GetNextWorldBossResponse
import com.kidor.vigik.utils.AssertUtils.assertEquals
import com.kidor.vigik.utils.SystemWrapper
import com.kidor.vigik.utils.TestUtils.logTestName
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockkObject
import io.mockk.slot
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Before
import org.junit.Test
import retrofit2.Response
import java.net.UnknownHostException

/**
 * Unit tests for [DiabloWidgetWorker].
 */
class DiabloWidgetWorkerTest {

    private lateinit var worker: DiabloWidgetWorker

    @MockK
    private lateinit var context: Context
    @MockK
    private lateinit var workerParameters: WorkerParameters
    @MockK
    private lateinit var taskExecutor: TaskExecutor
    @MockK
    private lateinit var serialTaskExecutor: SerialExecutor
    @MockK
    private lateinit var glanceAppWidgetManager: GlanceAppWidgetManager
    @MockK
    private lateinit var updateDiabloWidgetStateUseCase: UpdateDiabloWidgetStateUseCase
    @MockK
    private lateinit var preferences: MutablePreferences
    @MockK
    private lateinit var glanceId: GlanceId
    @MockK
    private lateinit var diablo4API: Diablo4API
    @MockK
    private lateinit var localization: Localization
    @MockK
    private lateinit var systemWrapper: SystemWrapper

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        every { workerParameters.taskExecutor } returns taskExecutor
        every { taskExecutor.serialTaskExecutor } returns serialTaskExecutor
        coEvery { glanceAppWidgetManager.getGlanceIds(DiabloWidget::class.java) } returns listOf(glanceId)
        val update = slot<(MutablePreferences) -> Unit>()
        coEvery { updateDiabloWidgetStateUseCase.execute(context, glanceId, capture(update)) } answers {
            update.captured.invoke(preferences)
        }
        mockkObject(DiabloWidgetStateHelper)
        every { DiabloWidgetStateHelper.setLoading(preferences, any()) } returns Unit
        every { DiabloWidgetStateHelper.setData(preferences, any(), any(), localization, systemWrapper) } returns Unit
        worker = DiabloWidgetWorker(
            context = context,
            workerParams = workerParameters,
            glanceAppWidgetManager = glanceAppWidgetManager,
            updateDiabloWidgetStateUseCase = updateDiabloWidgetStateUseCase,
            diablo4API = diablo4API,
            localization = localization,
            systemWrapper = systemWrapper
        )
    }

    @Test
    fun `test worker result when no provider defined for diablo widget`() {
        logTestName()

        coEvery { glanceAppWidgetManager.getGlanceIds(DiabloWidget::class.java) } returns listOf()

        runTest {
            // Do the work
            val result = worker.doWork()

            // Check that result is failure
            assertEquals(ListenableWorker.Result.failure(), result, "Worker result")
            // Check that no API request was made
            coVerify(inverse = true) { diablo4API.getNextWorldBoss() }
        }
    }

    @Test
    fun `test worker result when getting world boss data throw an exception`() {
        logTestName()

        // Mock network calls
        coEvery { diablo4API.getNextWorldBoss() } answers { throw UnknownHostException("test-exception") }

        runTest {
            // Do the work
            val result = worker.doWork()

            // Check that result is failure
            assertEquals(ListenableWorker.Result.failure(), result, "Worker result")
            // Check that API request was made
            coVerify { diablo4API.getNextWorldBoss() }

            // Check widget state was updated twice (to set the loading state to true then false)
            coVerify(exactly = 2) { updateDiabloWidgetStateUseCase.execute(context, glanceId, any()) }
        }
    }

    @Test
    fun `test worker result when getting world boss data returns an error`() {
        logTestName()

        // Mock network calls
        coEvery { diablo4API.getNextWorldBoss() } returns Response.error(
            404,
            "{\"message\": \"No world boss found\"}".toResponseBody("application/json".toMediaTypeOrNull())
        )

        runTest {
            // Do the work
            val result = worker.doWork()

            // Check that result is failure
            assertEquals(ListenableWorker.Result.failure(), result, "Worker result")
            // Check that API request was made
            coVerify { diablo4API.getNextWorldBoss() }

            // Check widget state was updated twice (to set the loading state to true then false)
            coVerify(exactly = 2) { updateDiabloWidgetStateUseCase.execute(context, glanceId, any()) }
        }
    }

    @Test
    fun `test worker result when getting world boss data returns valid data`() {
        logTestName()

        // Mock network calls
        coEvery { diablo4API.getNextWorldBoss() } returns Response.success(
            GetNextWorldBossResponse(name = "avarice", time = 42)
        )

        runTest {
            // Do the work
            val result = worker.doWork()

            // Check that result is success
            assertEquals(ListenableWorker.Result.success(), result, "Worker result")
            // Check that API request was made
            coVerify { diablo4API.getNextWorldBoss() }

            // Check widget state was updated twice (to set the loading state to true then to save data)
            coVerify(exactly = 2) { updateDiabloWidgetStateUseCase.execute(context, glanceId, any()) }
        }
    }
}
