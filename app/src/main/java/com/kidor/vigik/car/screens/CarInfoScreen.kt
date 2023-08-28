package com.kidor.vigik.car.screens

import androidx.car.app.CarContext
import androidx.car.app.hardware.CarHardwareManager
import androidx.car.app.hardware.info.Model
import androidx.car.app.model.Action
import androidx.car.app.model.Pane
import androidx.car.app.model.PaneTemplate
import androidx.car.app.model.Row
import androidx.car.app.model.Template
import com.kidor.vigik.R
import com.kidor.vigik.car.screens.common.HardwareDataCarScreen
import java.util.concurrent.Executor

/**
 * Screens that displays information about the car.
 */
class CarInfoScreen(carContext: CarContext) : HardwareDataCarScreen(carContext) {

    private var carModel: Model? = null

    override fun collectData(carHardwareManager: CarHardwareManager, executor: Executor) {
        // Requires Car API 3
        val carInfo = carHardwareManager.carInfo
        carInfo.fetchModel(executor) { model ->
            carModel = model
            notifyDataChanged()
        }
    }

    override fun getLoadingTemplate(): Template {
        return PaneTemplate
            .Builder(
                Pane.Builder()
                    .setLoading(true)
                    .build()
            )
            .setTitle(carContext.getString(R.string.car_car_info_label))
            .setHeaderAction(Action.BACK)
            .build()
    }

    override fun getTemplate(): Template {
        val notAvailableLabel = carContext.getString(R.string.car_data_not_available_label)
        return PaneTemplate
            .Builder(
                Pane.Builder()
                    .addRow(
                        Row.Builder()
                            .setTitle(carContext.getString(R.string.car_car_info_name_label))
                            .addText(carModel?.name?.value ?: notAvailableLabel)
                            .build()
                    )
                    .addRow(
                        Row.Builder()
                            .setTitle(carContext.getString(R.string.car_car_info_manufacturer_label))
                            .addText(carModel?.manufacturer?.value ?: notAvailableLabel)
                            .build()
                    )
                    .addRow(
                        Row.Builder()
                            .setTitle(carContext.getString(R.string.car_car_info_year_label))
                            .addText(carModel?.year?.value?.toString() ?: notAvailableLabel)
                            .build()
                    )
                    .setLoading(false)
                    .build()
            )
            .setTitle(carContext.getString(R.string.car_car_info_label))
            .setHeaderAction(Action.BACK)
            .build()
    }
}
