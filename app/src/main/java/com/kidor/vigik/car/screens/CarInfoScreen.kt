package com.kidor.vigik.car.screens

import androidx.activity.addCallback
import androidx.annotation.OptIn
import androidx.car.app.CarContext
import androidx.car.app.annotations.ExperimentalCarApi
import androidx.car.app.hardware.CarHardwareManager
import androidx.car.app.hardware.common.CarUnit
import androidx.car.app.hardware.common.CarValue
import androidx.car.app.hardware.common.OnCarDataAvailableListener
import androidx.car.app.hardware.info.EnergyLevel
import androidx.car.app.hardware.info.Mileage
import androidx.car.app.hardware.info.Model
import androidx.car.app.hardware.info.Speed
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
    private var energyLevel: EnergyLevel? = null
    private var speed: Speed? = null
    private var mileage: Mileage? = null

    private val energyLevelListener = OnCarDataAvailableListener<EnergyLevel> {
        energyLevel = it
        notifyDataChanged()
    }
    private val speedListener = OnCarDataAvailableListener<Speed> {
        speed = it
        notifyDataChanged()
    }
    private val mileageListener = OnCarDataAvailableListener<Mileage> {
        val isMileageValueValid = it.odometerMeters.status == CarValue.STATUS_SUCCESS &&
                it.distanceDisplayUnit.status == CarValue.STATUS_SUCCESS
        if (isMileageValueValid) {
            mileage = it
            notifyDataChanged()
        }
    }

    override fun collectData(carHardwareManager: CarHardwareManager, executor: Executor) {
        // Requires Car API 3
        val carInfo = carHardwareManager.carInfo
        carInfo.fetchModel(executor) { model ->
            carModel = model
            notifyDataChanged()
        }

        // Add listeners
        carInfo.addEnergyLevelListener(executor, energyLevelListener)
        carInfo.addSpeedListener(executor, speedListener)
        carInfo.addMileageListener(executor, mileageListener)

        // Remove listeners when leaving the screen
        carContext.onBackPressedDispatcher.addCallback {
            carInfo.removeEnergyLevelListener(energyLevelListener)
            carInfo.removeSpeedListener(speedListener)
            carInfo.removeMileageListener(mileageListener)

            screenManager.pop()
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
                            .setTitle(carContext.getString(R.string.car_car_info_model_label))
                            .addText(formatCarModel(notAvailableLabel))
                            .build()
                    )
                    .addRow(
                        Row.Builder()
                            .setTitle(carContext.getString(R.string.car_car_info_energy_level_label))
                            .addText(formatEnergyLevel(notAvailableLabel))
                            .build()
                    )
                    .addRow(
                        Row.Builder()
                            .setTitle(carContext.getString(R.string.car_car_info_speed_label))
                            .addText(formatSpeed(notAvailableLabel))
                            .build()
                    )
                    .addRow(
                        Row.Builder()
                            .setTitle(carContext.getString(R.string.car_car_info_mileage_label))
                            .addText(formatMileage(notAvailableLabel))
                            .build()
                    )
                    .setLoading(false)
                    .build()
            )
            .setTitle(carContext.getString(R.string.car_car_info_label))
            .setHeaderAction(Action.BACK)
            .build()
    }

    private fun formatCarModel(notAvailableLabel: String): String {
        val name = carModel?.name?.value ?: notAvailableLabel
        val year = carModel?.year?.value?.toString() ?: notAvailableLabel
        val manufacturer = carModel?.manufacturer?.value ?: notAvailableLabel
        return carContext.getString(R.string.car_car_info_model_value, name, year, manufacturer)
    }

    @OptIn(ExperimentalCarApi::class)
    private fun formatEnergyLevel(notAvailableLabel: String): String {
        val batteryPercent = energyLevel?.batteryPercent?.value ?: notAvailableLabel
        val fuelPercent = energyLevel?.fuelPercent?.value ?: notAvailableLabel
        val energyIsLow = energyLevel?.energyIsLow?.value ?: notAvailableLabel
        val rangeRemaining = energyLevel?.rangeRemainingMeters?.value ?: notAvailableLabel
        val distanceDisplayUnit = energyLevel?.distanceDisplayUnit?.value ?: notAvailableLabel
        val fuelVolumeDisplayUnit = when (energyLevel?.fuelVolumeDisplayUnit?.value) {
            CarUnit.MILLILITER -> "mL"
            CarUnit.LITER -> "L"
            CarUnit.US_GALLON -> "gal US"
            CarUnit.IMPERIAL_GALLON -> "gal GB"
            else -> notAvailableLabel
        }
        return carContext.getString(
            R.string.car_car_info_energy_level_value,
            batteryPercent,
            fuelPercent,
            energyIsLow,
            rangeRemaining,
            distanceDisplayUnit,
            fuelVolumeDisplayUnit
        )
    }

    private fun formatSpeed(notAvailableLabel: String): String {
        val rawSpeed = speed?.rawSpeedMetersPerSecond?.value.let {
            if (it != null) {
                "$it m/s"
            } else {
                notAvailableLabel
            }
        }
        val displaySpeed = speed?.displaySpeedMetersPerSecond?.value.let {
            if (it != null) {
                "$it m/s"
            } else {
                notAvailableLabel
            }
        }
        val speedDisplayUnit = when (speed?.speedDisplayUnit?.value) {
            CarUnit.METERS_PER_SEC -> "m/s"
            CarUnit.KILOMETERS_PER_HOUR -> "km/h"
            CarUnit.MILES_PER_HOUR -> "mi/h"
            else -> notAvailableLabel
        }
        return carContext.getString(R.string.car_car_info_speed_value, rawSpeed, displaySpeed, speedDisplayUnit)
    }

    private fun formatMileage(notAvailableLabel: String): String {
        val value = mileage?.odometerMeters?.value
        val unit = when (mileage?.distanceDisplayUnit?.value) {
            CarUnit.KILOMETER -> "km"
            CarUnit.MILE -> "mi"
            else -> null
        }
        return if (value == null && unit == null) {
            notAvailableLabel
        } else {
            "$value $unit"
        }
    }
}
