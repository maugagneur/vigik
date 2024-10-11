package com.kidor.vigik.extensions

import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import com.kidor.vigik.ui.compose.AppScreen

/**
 * Navigate to a destination in the current NavGraph as single-top and poping up the start destination.
 * If an invalid route is given, an [IllegalArgumentException] will be thrown.
 *
 * @param destination The destination.
 */
fun NavHostController.navigateSingleTopTo(destination: AppScreen): Unit =
    this.navigate(destination.route) {
        // Pop up to the start destination of the graph to avoid building up a large stack of destinations on the back
        // stack as users select items.
        popUpTo(
            this@navigateSingleTopTo.graph.findStartDestination().id
        ) {
            saveState = true
        }
        // Avoid multiple copies of the same destination when reselecting the same item
        launchSingleTop = true
        // Restore state when reselecting a previously selected item
        restoreState = true
    }

/**
 * Navigate to a destination in the current NavGraph.
 * If an invalid route is given, an [IllegalArgumentException] will be thrown.
 *
 * @param destination The destination.
 * @param popUpTo     If set to true, pop up screens until the destination.
 */
fun NavHostController.navigateTo(destination: AppScreen, popUpTo: Boolean = false): Unit = this.navigate(
    route = destination.route,
    navOptions = if (popUpTo) {
        NavOptions.Builder()
            .setPopUpTo(destination.route, inclusive = true)
            .build()
    } else {
        null
    }
)
