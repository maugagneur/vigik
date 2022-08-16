package com.kidor.vigik.extensions

import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.kidor.vigik.ui.compose.AppDestination

/**
 * Navigate to a destination in the current NavGraph as single-top and poping up the start destination.
 * If an invalid route is given, an [IllegalArgumentException] will be thrown.
 *
 * @param destination the destination
 */
fun NavHostController.navigateSingleTopTo(destination: AppDestination) =
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
 * @param destination the destination
 */
fun NavHostController.navigate(destination: AppDestination) = this.navigate(destination.route)