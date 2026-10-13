package com.kidor.vigik.ui.widget

import android.content.Context
import androidx.datastore.preferences.core.MutablePreferences
import androidx.glance.GlanceId
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters
import androidx.work.impl.utils.taskexecutor.SerialExecutor
import androidx.work.impl.utils.taskexecutor.TaskExecutor
import com.kidor.vigik.data.apes.ApesApi
import com.kidor.vigik.data.apes.model.AvailabilityOut
import com.kidor.vigik.data.apes.model.VoiceMember
import com.kidor.vigik.data.apes.model.VoiceOut
import com.kidor.vigik.utils.AssertUtils.assertEquals
import com.kidor.vigik.utils.TestUtils.logTestName
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockkObject
import io.mockk.slot
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import retrofit2.Response

/**
 * Unit tests for [ApesWidgetWorker].
 */
class ApesWidgetWorkerTest {

    private lateinit var worker: ApesWidgetWorker

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
    private lateinit var updateApesWidgetStateUseCase: UpdateApesWidgetStateUseCase
    @MockK
    private lateinit var preferences: MutablePreferences
    @MockK
    private lateinit var glanceId: GlanceId
    @MockK
    private lateinit var apesApi: ApesApi

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        every { workerParameters.taskExecutor } returns taskExecutor
        every { taskExecutor.serialTaskExecutor } returns serialTaskExecutor
        coEvery { glanceAppWidgetManager.getGlanceIds(ApesWidget::class.java) } returns listOf(glanceId)
        val update = slot<(MutablePreferences) -> Unit>()
        coEvery { updateApesWidgetStateUseCase.execute(context, glanceId, capture(update)) } answers {
            update.captured.invoke(preferences)
        }
        mockkObject(ApesWidgetStateHelper)
        every { context.getString(any()) } returns ""
        every { context.getString(any(), *anyVararg()) } returns ""
        every { ApesWidgetStateHelper.setLoading(preferences, any()) } returns Unit
        every { ApesWidgetStateHelper.setData(preferences, any(), any(), any()) } returns Unit

        worker = ApesWidgetWorker(
            context = context,
            workerParams = workerParameters,
            glanceAppWidgetManager = glanceAppWidgetManager,
            updateApesWidgetStateUseCase = updateApesWidgetStateUseCase,
            apesApi = apesApi
        )
    }

    @Test
    fun `test worker result when no provider defined for apes widget`() = runTest {
        logTestName()

        coEvery { glanceAppWidgetManager.getGlanceIds(ApesWidget::class.java) } returns listOf()

        val result = worker.doWork()

        assertEquals(ListenableWorker.Result.failure(), result, "Worker result")
        coVerify(inverse = true) { apesApi.getVoice() }
    }

    @Test
    fun `test worker result when getting data succeeds`() = runTest {
        logTestName()

        val dummyVoice = VoiceOut(
            now = "2026-10-13T20:00:00Z",
            members = listOf(VoiceMember(id = "1", displayName = "Arthur", since = "2026-10-13T19:00:00Z"))
        )
        val dummyAvail = AvailabilityOut(
            fromDate = "2026-10-13",
            toDate = "2026-10-15",
            timezone = "Europe/Paris",
            slots = emptyList(),
            members = emptyList()
        )

        coEvery { apesApi.getVoice() } returns Response.success(dummyVoice)
        coEvery { apesApi.getAvailability(any(), any()) } returns Response.success(dummyAvail)

        val result = worker.doWork()

        assertEquals(ListenableWorker.Result.success(), result, "Worker result")
        coVerify { apesApi.getVoice() }
        coVerify { apesApi.getAvailability(any(), any()) }

        // State updated to set loading and then to set data
        coVerify(exactly = 2) { updateApesWidgetStateUseCase.execute(context, glanceId, any()) }
    }
}
