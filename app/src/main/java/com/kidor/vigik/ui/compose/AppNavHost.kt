package com.kidor.vigik.ui.compose

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = AppNavigation.CheckScreen.route,
        modifier = modifier
    ) {
        composable(route = AppNavigation.CheckScreen.route) {
            Text(text = "DEBUG: CHECK")
        }
        composable(route = AppNavigation.HubScreen.route) {
            Text(text = "DEBUG: HUB")
        }
        composable(route = AppNavigation.ScanScreen.route) {
            Text(text = "DEBUG: SCAN")
        }
        composable(route = AppNavigation.HistoryScreen.route) {
            Text(text = "DEBUG: HISTORY")
        }
        composable(route = AppNavigation.EmulateScreen.route) {
            Text(text = "DEBUG: EMULATE")
        }
    }
}

fun NavHostController.navigateSingleTopTo(route: String) =
    this.navigate(route) {
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
