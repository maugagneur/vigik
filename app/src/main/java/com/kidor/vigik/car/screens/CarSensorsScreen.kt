package com.kidor.vigik.car.screens

import android.location.Location
import androidx.car.app.CarContext
import androidx.car.app.constraints.ConstraintManager
import androidx.car.app.hardware.CarHardwareManager
import androidx.car.app.hardware.common.CarValue
import androidx.car.app.hardware.info.CarSensors
import androidx.car.app.model.Action
import androidx.car.app.model.Pane
import androidx.car.app.model.PaneTemplate
import androidx.car.app.model.Row
import androidx.car.app.model.Template
import com.kidor.vigik.R
import com.kidor.vigik.car.screens.common.HardwareDataCarScreen
import java.util.concurrent.Executor
import kotlin.math.min

/**
 * Screens that displays information about sensors.
 */
class CarSensorsScreen(carContext: CarContext) : HardwareDataCarScreen(carContext) {

    private var accelerometerForces: MutableList<Float>? = null
    private var compassOrientations: MutableList<Float>? = null
    private var gyroscopeRotations: MutableList<Float>? = null
    private var hardwareLocation: Location? = null

    override fun collectData(carHardwareManager: CarHardwareManager, executor: Executor) {
        // Requires Car API 3
        val carSensors = carHardwareManager.carSensors

        carSensors.addAccelerometerListener(CarSensors.UPDATE_RATE_NORMAL, executor) { accelerometer ->
            accelerometer.forces.let { forces ->
                if (forces.status == CarValue.STATUS_SUCCESS) {
                    accelerometerForces = forces.value
                    notifyDataChanged()
                }
            }
        }
        carSensors.addCompassListener(CarSensors.UPDATE_RATE_NORMAL, executor) { compass ->
            compass.orientations.let { orientations ->
                if (orientations.status == CarValue.STATUS_SUCCESS) {
                    compassOrientations = orientations.value
                    notifyDataChanged()
                }
            }
        }
        carSensors.addGyroscopeListener(CarSensors.UPDATE_RATE_NORMAL, executor) { gyroscope ->
            gyroscope.rotations.let { rotations ->
                if (rotations.status == CarValue.STATUS_SUCCESS) {
                    gyroscopeRotations = rotations.value
                    notifyDataChanged()
                }
            }
        }
        carSensors.addCarHardwareLocationListener(CarSensors.UPDATE_RATE_NORMAL, executor) { carHardwareLocation ->
            carHardwareLocation.location.let { location ->
                if (location.status == CarValue.STATUS_SUCCESS) {
                    hardwareLocation = location.value
                    notifyDataChanged()
                }
            }
        }
    }

    override fun getLoadingTemplate(): Template {
        return PaneTemplate
            .Builder(
                Pane.Builder()
                    .setLoading(true)
                    .build()
            )
            .setTitle(carContext.getString(R.string.car_car_sensors_label))
            .setHeaderAction(Action.BACK)
            .build()
    }

    override fun getTemplate(): Template {
        val rows = listOf(
            Row.Builder()
                .setTitle(carContext.getString(R.string.car_car_sensors_accelerometer_forces))
                .addText(formatAccelerometerForces())
                .build(),
            Row.Builder()
                .setTitle(carContext.getString(R.string.car_car_sensors_compass_orientations))
                .addText(formatCompassOrientations())
                .build(),
            Row.Builder()
                .setTitle(carContext.getString(R.string.car_car_sensors_gyroscope_rotations))
                .addText(formatGyroscopeRotations())
                .build(),
            Row.Builder()
                .setTitle(carContext.getString(R.string.car_car_sensors_hardware_location))
                .addText(formatHardwareLocation())
                .build()
        )
        return PaneTemplate
            .Builder(
                Pane.Builder()
                    .apply {
                        val rowLimit = constraintManager.getContentLimit(ConstraintManager.CONTENT_LIMIT_TYPE_PANE)
                        val numberOfRowsToAdd = min(rows.size, rowLimit)
                        for (index in 0 until numberOfRowsToAdd) {
                            addRow(rows[index])
                        }
                    }
                    .setLoading(false)
                    .build()
            )
            .setTitle(carContext.getString(R.string.car_car_sensors_label))
            .setHeaderAction(Action.BACK)
            .build()
    }

    private fun formatAccelerometerForces(): String = accelerometerForces.let {
        if (it.isNullOrEmpty()) {
            carContext.getString(R.string.car_data_not_available_label)
        } else {
            val xAxis = it.getOrNull(0)
            val yAxis = it.getOrNull(1)
            val zAxis = it.getOrNull(2)
            "X-Axis: $xAxis, Y-Axis: $yAxis, Z-Axis: $zAxis"
        }
    }

    private fun formatCompassOrientations(): String = compassOrientations.let {
        if (it.isNullOrEmpty()) {
            carContext.getString(R.string.car_data_not_available_label)
        } else {
            val azimuth = it.getOrNull(0)
            val pitch = it.getOrNull(1)
            val roll = it.getOrNull(2)
            "Azimuth: $azimuth, Pitch: $pitch, Roll: $roll"
        }
    }

    private fun formatGyroscopeRotations(): String = gyroscopeRotations.let {
        if (it.isNullOrEmpty()) {
            carContext.getString(R.string.car_data_not_available_label)
        } else {
            val xRotation = it.getOrNull(0)
            val yRotation = it.getOrNull(1)
            val zRotation = it.getOrNull(2)
            "X-rotation: $xRotation, Y-rotation: $yRotation, Z-rotation: $zRotation"
        }
    }

    private fun formatHardwareLocation(): String =
        hardwareLocation?.toString() ?: carContext.getString(R.string.car_data_not_available_label)
}
