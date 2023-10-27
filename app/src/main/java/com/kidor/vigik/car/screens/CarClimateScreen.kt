package com.kidor.vigik.car.screens

import android.util.Pair
import androidx.car.app.CarContext
import androidx.car.app.annotations.ExperimentalCarApi
import androidx.car.app.hardware.CarHardwareManager
import androidx.car.app.hardware.climate.CabinTemperatureProfile
import androidx.car.app.hardware.climate.CarClimateFeature
import androidx.car.app.hardware.climate.CarClimateProfileCallback
import androidx.car.app.hardware.climate.ClimateProfileRequest
import androidx.car.app.hardware.climate.FanSpeedLevelProfile
import androidx.car.app.hardware.common.CarZone
import androidx.car.app.model.Action
import androidx.car.app.model.Pane
import androidx.car.app.model.PaneTemplate
import androidx.car.app.model.Row
import androidx.car.app.model.Template
import com.kidor.vigik.R
import com.kidor.vigik.car.screens.common.HardwareDataCarScreen
import java.util.concurrent.Executor

/**
 * Screens that displays information about climate.
 */
class CarClimateScreen(carContext: CarContext) : HardwareDataCarScreen(carContext) {

    private var cabinTemperature: CabinTemperatureProfile? = null
    private var fanSpeedLevelRanges: Map<Set<CarZone>, Pair<Int, Int>> = emptyMap()

    @ExperimentalCarApi
    override fun collectData(carHardwareManager: CarHardwareManager, executor: Executor) {
        // Requires Car API 5
        val carClimate = carHardwareManager.carClimate

        val request = ClimateProfileRequest.Builder()
            .addClimateProfileFeatures(
                CarClimateFeature.Builder(
                    ClimateProfileRequest.FEATURE_CABIN_TEMPERATURE
                ).build()
            )
            .addClimateProfileFeatures(
                CarClimateFeature.Builder(
                    ClimateProfileRequest.FEATURE_FAN_SPEED
                ).build()
            )
            .build()
        carClimate.fetchClimateProfile(
            executor,
            request,
            object : CarClimateProfileCallback {
                override fun onCabinTemperatureProfileAvailable(cabinTemperatureProfile: CabinTemperatureProfile) {
                    cabinTemperature = cabinTemperatureProfile
                    notifyDataChanged()
                }

                override fun onFanSpeedLevelProfileAvailable(fanSpeedLevelProfile: FanSpeedLevelProfile) {
                    fanSpeedLevelRanges = fanSpeedLevelProfile.carZoneSetsToFanSpeedLevelRanges
                    notifyDataChanged()
                }
            }
        )
    }

    override fun getLoadingTemplate(): Template {
        return PaneTemplate
            .Builder(
                Pane.Builder()
                    .setLoading(true)
                    .build()
            )
            .setTitle(carContext.getString(R.string.car_car_climate_label))
            .setHeaderAction(Action.BACK)
            .build()
    }

    @ExperimentalCarApi
    override fun getTemplate(): Template {
        return PaneTemplate
            .Builder(
                Pane.Builder()
                    .addRow(
                        Row.Builder()
                            .setTitle(carContext.getString(R.string.car_car_climate_cabin_temperature))
                            .addText(formatCabinTemperature())
                            .build()
                    )
                    .addRow(
                        Row.Builder()
                            .setTitle(carContext.getString(R.string.car_car_climate_fan_speed_level))
                            .addText(formatFanSpeedLevel())
                            .build()
                    )
                    .setLoading(false)
                    .build()
            )
            .setTitle(carContext.getString(R.string.car_car_climate_label))
            .setHeaderAction(Action.BACK)
            .build()
    }

    @ExperimentalCarApi
    private fun formatCabinTemperature(): String = cabinTemperature?.let {
        if (it.hasCarZoneSetsToCabinCelsiusTemperatureRanges()) {
            it.carZoneSetsToCabinCelsiusTemperatureRanges.entries.joinToString(separator = " | ") { entry ->
                "${entry.key} -> [${entry.value.first}, ${entry.value.second}]"
            }
        } else {
            null
        }
    } ?: carContext.getString(R.string.car_data_not_available_label)

    private fun formatFanSpeedLevel(): String = fanSpeedLevelRanges.entries.joinToString(separator = " | ") { entry ->
        "${entry.key} -> [${entry.value.first}, ${entry.value.second}]"
    }
}
