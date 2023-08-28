package com.kidor.vigik.car.screens

import androidx.car.app.CarContext
import androidx.car.app.CarToast
import androidx.car.app.Screen
import androidx.car.app.ScreenManager
import androidx.car.app.model.Action
import androidx.car.app.model.ActionStrip
import androidx.car.app.model.CarColor
import androidx.car.app.model.GridItem
import androidx.car.app.model.GridTemplate
import androidx.car.app.model.ItemList
import com.kidor.vigik.R
import com.kidor.vigik.extensions.setImageFromDrawable

/**
 * Main screen of the car application.
 */
class CarMainScreen(carContext: CarContext) : Screen(carContext) {

    override fun onGetTemplate(): GridTemplate {
        val screenManager: ScreenManager = carContext.getCarService(ScreenManager::class.java)

        return GridTemplate.Builder()
            .setTitle(carContext.getString(R.string.car_app_name))
            .setHeaderAction(Action.APP_ICON)
            .setActionStrip(
                ActionStrip.Builder()
                    .addAction(
                        Action.Builder()
                            .setTitle(carContext.getString(R.string.car_car_api_level_action_label))
                            .setOnClickListener {
                                CarToast.makeText(
                                    carContext,
                                    carContext.getString(
                                        R.string.car_car_api_level_toast_message,
                                        carContext.carAppApiLevel
                                    ),
                                    CarToast.LENGTH_SHORT
                                ).show()
                            }.build()
                    ).build()
            )
            .setSingleList(
                ItemList.Builder()
                    .addItem(
                        GridItem.Builder()
                            .setTitle(carContext.getString(R.string.car_car_info_label))
                            .setImageFromDrawable(carContext, R.drawable.ic_car)
                            .setOnClickListener { screenManager.push(CarInfoScreen(carContext)) }
                            .build()
                    )
                    .addItem(
                        GridItem.Builder()
                            .setTitle(carContext.getString(R.string.car_car_sensors_label))
                            .setImageFromDrawable(carContext, R.drawable.ic_sensors, CarColor.YELLOW)
                            .setOnClickListener { screenManager.push(CarSensorsScreen(carContext)) }
                            .build()
                    )
                    .addItem(
                        GridItem.Builder()
                            .setTitle(carContext.getString(R.string.car_car_climate_label))
                            .setImageFromDrawable(carContext, R.drawable.ic_cloud, CarColor.BLUE)
                            .setOnClickListener { screenManager.push(CarClimateScreen(carContext)) }
                            .build()
                    )
                    .build()
            )
            .build()
    }
}
