package com.kidor.vigik.car

import android.content.Intent
import androidx.car.app.Screen
import androidx.car.app.Session
import com.kidor.vigik.car.screens.CarMainScreen

/**
 * Custom [Session] for this app.
 */
class CarSession : Session() {

    override fun onCreateScreen(intent: Intent): Screen = CarMainScreen(carContext)
}
