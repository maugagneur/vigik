package com.kidor.vigik.car

import android.content.Intent
import androidx.car.app.Screen
import androidx.car.app.ScreenManager
import androidx.car.app.Session
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.kidor.vigik.car.screens.CarMainScreen
import com.kidor.vigik.car.screens.CarNeedPermissionsScreen

/**
 * Custom [Session] for this app.
 */
class CarSession : Session() {

    private val requiredPermissions = listOf(
        "com.google.android.gms.permission.CAR_FUEL",
        "com.google.android.gms.permission.CAR_SPEED",
        "com.google.android.gms.permission.CAR_MILEAGE"
    )

    init {
        lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onCreate(owner: LifecycleOwner) {
                val screenManager: ScreenManager = carContext.getCarService(ScreenManager::class.java)
                // Check permissions
                carContext.requestPermissions(requiredPermissions) { _, rejectedPermissions ->
                    if (rejectedPermissions.isEmpty()) {
                        screenManager.push(CarMainScreen(carContext))
                    } else {
                        screenManager.push(CarNeedPermissionsScreen(carContext))
                    }
                }
            }
        })
    }

    // Until the permissions check is done, display the screen that ask for them
    override fun onCreateScreen(intent: Intent): Screen = CarNeedPermissionsScreen(carContext)
}
