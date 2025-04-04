package com.kidor.vigik.car.screens

import androidx.annotation.OptIn
import androidx.car.app.CarContext
import androidx.car.app.CarToast
import androidx.car.app.Screen
import androidx.car.app.annotations.ExperimentalCarApi
import androidx.car.app.model.Action
import androidx.car.app.model.CarColor
import androidx.car.app.model.GridItem
import androidx.car.app.model.GridTemplate
import androidx.car.app.model.Header
import androidx.car.app.model.ItemList
import com.kidor.vigik.R
import com.kidor.vigik.extensions.setImageFromDrawable

/**
 * Main screen of the car application.
 */
class CarMainScreen(carContext: CarContext) : Screen(carContext) {

    @OptIn(ExperimentalCarApi::class)
    override fun onGetTemplate(): GridTemplate {
        return GridTemplate.Builder()
            .setHeader(
                Header.Builder()
                    .setTitle(carContext.getString(R.string.car_app_name))
                    .setStartHeaderAction(Action.APP_ICON)
                    .build()
            )
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
                    }
                    .build()
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
                    .addItem(
                        GridItem.Builder()
                            .setTitle(carContext.getString(R.string.car_login_label))
                            .setImageFromDrawable(carContext, R.drawable.ic_key)
                            .setOnClickListener { screenManager.push(CarLoginScreen(carContext)) }
                            .build()
                    )
                    .build()
            )
            .build()
    }
}
