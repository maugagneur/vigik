package com.kidor.vigik.car.screens

import androidx.car.app.CarContext
import androidx.car.app.CarToast
import androidx.car.app.Screen
import androidx.car.app.model.Action
import androidx.car.app.model.ActionStrip
import androidx.car.app.model.Pane
import androidx.car.app.model.PaneTemplate
import androidx.car.app.model.Row
import androidx.car.app.model.Template

/**
 * Main screen of the car application.
 */
class CarMainScreen(carContext: CarContext) : Screen(carContext) {

    override fun onGetTemplate(): Template {
        val row = Row.Builder()
            .setTitle("Titre dans une ligne")
            .build()

        val action = Action.Builder()
            .setTitle("Action A")
            .setOnClickListener {
                CarToast.makeText(carContext, "Clic de Action A détecté", CarToast.LENGTH_SHORT).show()
            }
            .build()

        return PaneTemplate.Builder(
            Pane.Builder()
                .addRow(row)
                .build()
        )
            .setHeaderAction(Action.APP_ICON)
            .setActionStrip(
                ActionStrip.Builder()
                    .addAction(action)
                    .build()
            )
            .build()
    }
}
