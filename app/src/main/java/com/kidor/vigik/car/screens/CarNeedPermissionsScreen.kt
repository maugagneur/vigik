package com.kidor.vigik.car.screens

import androidx.car.app.CarContext
import androidx.car.app.Screen
import androidx.car.app.model.Action
import androidx.car.app.model.CarIcon
import androidx.car.app.model.MessageTemplate
import androidx.car.app.model.Template
import com.kidor.vigik.R

/**
 * Screen that tells to enable required permissions.
 */
class CarNeedPermissionsScreen(carContext: CarContext) : Screen(carContext) {

    override fun onGetTemplate(): Template {
        return MessageTemplate.Builder(carContext.getString(R.string.car_need_permissions_message))
            .setTitle(carContext.getString(R.string.car_need_permissions_title))
            .setIcon(CarIcon.ALERT)
            .addAction(
                Action.Builder()
                    .setTitle(carContext.getString(R.string.car_need_permissions_ok_action_label))
                    .setOnClickListener { carContext.finishCarApp() }
                    .build()
            )
            .build()
    }
}
