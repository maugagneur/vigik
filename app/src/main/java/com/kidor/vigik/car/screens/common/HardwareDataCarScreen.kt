package com.kidor.vigik.car.screens.common

import androidx.car.app.CarContext
import androidx.car.app.Screen
import androidx.car.app.ScreenManager
import androidx.car.app.hardware.CarHardwareManager
import androidx.car.app.model.Action
import androidx.car.app.model.CarIcon
import androidx.car.app.model.MessageTemplate
import androidx.car.app.model.Template
import androidx.core.content.ContextCompat
import com.kidor.vigik.R
import timber.log.Timber
import java.util.concurrent.Executor

/**
 * Base class for screens collecting hardware data.
 */
abstract class HardwareDataCarScreen(carContext: CarContext) : Screen(carContext) {

    private var screenState = CarDataScreenState.NOT_INITIALIZED

    override fun onGetTemplate(): Template {
        if (screenState == CarDataScreenState.NOT_INITIALIZED) {
            try {
                // Requires Car API 3
                val carHardwareManager = carContext.getCarService(CarHardwareManager::class.java)
                val executor = ContextCompat.getMainExecutor(carContext)
                collectData(carHardwareManager, executor)
            } catch (exception: IllegalArgumentException) {
                Timber.e(exception, "Fail to get car service")
                screenState = CarDataScreenState.ERROR
            } catch (exception: IllegalStateException) {
                Timber.e(exception, "Fail to instantiate car service")
                screenState = CarDataScreenState.ERROR
            } catch (exception: UnsupportedOperationException) {
                Timber.e(exception, "Getting car information not supported")
                screenState = CarDataScreenState.ERROR
            }
        }

        return when (screenState) {
            CarDataScreenState.NOT_INITIALIZED -> getLoadingTemplate() ?: getTemplate()
            CarDataScreenState.DATA_AVAILABLE -> getTemplate()
            CarDataScreenState.ERROR -> getErrorTemplate()
        }
    }

    /**
     * Functions called to collect data.
     * When data are ready to be displayed, call [notifyDataChanged].
     *
     * @param carHardwareManager The manager to access car hardware data.
     * @param executor           The executor to use for invoking listeners.
     */
    protected open fun collectData(carHardwareManager: CarHardwareManager, executor: Executor) {
        // Default implementation
    }

    /**
     * Notifies that new data are ready to be displayed.
     */
    protected fun notifyDataChanged() {
        screenState = CarDataScreenState.DATA_AVAILABLE
        invalidate()
    }

    /**
     * Returns the template to display when fetching data.
     */
    protected open fun getLoadingTemplate(): Template? = null

    /**
     * Returns the template to display when data are collected.
     */
    abstract fun getTemplate(): Template

    /**
     * Returns the template to display when an error occurred when collecting data.
     */
    private fun getErrorTemplate(): MessageTemplate =
        MessageTemplate.Builder(carContext.getString(R.string.car_hardaware_data_error_message))
            .setTitle(carContext.getString(R.string.car_hardaware_data_error_title))
            .setIcon(CarIcon.ALERT)
            .addAction(
                Action.Builder()
                    .setTitle(carContext.getString(R.string.car_hardaware_data_error_ok_action_label))
                    .setOnClickListener {
                        val screenManager: ScreenManager = carContext.getCarService(ScreenManager::class.java)
                        screenManager.pop()
                    }
                    .build()
            )
            .build()
}
