package com.kidor.vigik.navigation

import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.provider.Settings
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import com.kidor.vigik.extensions.findActivity
import com.kidor.vigik.extensions.navigateSingleTopTo
import com.kidor.vigik.extensions.navigateTo
import com.kidor.vigik.ui.animations.AnimationsHubScreen
import com.kidor.vigik.ui.animations.circleloader.CircleLoaderScreen
import com.kidor.vigik.ui.animations.followingarrows.FollowingArrowsScreen
import com.kidor.vigik.ui.animations.gauge.GaugeScreen
import com.kidor.vigik.ui.animations.glitterrainbow.GlitterRainbowScreen
import com.kidor.vigik.ui.animations.lookahead.LookaheadScreen
import com.kidor.vigik.ui.animations.pulse.PulseScreen
import com.kidor.vigik.ui.animations.shape.ShapeScreen
import com.kidor.vigik.ui.animations.snowfall.SnowfallScreen
import com.kidor.vigik.ui.animations.typewriter.TypewriterScreen
import com.kidor.vigik.ui.biometric.home.BiometricHomeScreen
import com.kidor.vigik.ui.biometric.login.BiometricLoginScreen
import com.kidor.vigik.ui.bluetooth.BluetoothScreen
import com.kidor.vigik.ui.bottomsheet.BottomSheetScreen
import com.kidor.vigik.ui.camera.CameraScreen
import com.kidor.vigik.ui.emoji.EmojiScreen
import com.kidor.vigik.ui.home.HomeScreen
import com.kidor.vigik.ui.nfc.check.CheckScreen
import com.kidor.vigik.ui.nfc.emulate.EmulateScreen
import com.kidor.vigik.ui.nfc.history.HistoryScreen
import com.kidor.vigik.ui.nfc.hub.HubScreen
import com.kidor.vigik.ui.nfc.scan.ScanScreen
import com.kidor.vigik.ui.notification.NotificationScreen
import com.kidor.vigik.ui.paging.PagingScreen
import com.kidor.vigik.ui.restapi.RestApiScreen
import com.kidor.vigik.ui.snackbar.SnackBarScreen
import com.kidor.vigik.ui.telephony.TelephonyScreen

/**
 * Implementation of [NavHost] for this application.
 */
@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    NavHost(
        navController = navController,
        startDestination = AppScreen.HomeScreen.route,
        modifier = modifier
    ) {
        addAnimationScreens(navGraphBuilder = this, navController = navController)
        addHomeScreens(navGraphBuilder = this, navController = navController)
        addBiometricScreens(navGraphBuilder = this, navController = navController, context = context)
        addBluetoothScreens(navGraphBuilder = this)
        addBottomSheetScreens(navGraphBuilder = this)
        addCameraScreens(navGraphBuilder = this)
        addEmojiScreens(navGraphBuilder = this)
        addNfcScreens(navGraphBuilder = this, navController = navController, context = context)
        addNotificationScreens(navGraphBuilder = this)
        addPagingScreens(navGraphBuilder = this)
        addRestApiScreens(navGraphBuilder = this)
        addSnackBarScreens(navGraphBuilder = this)
        addTelephonyScreens(navGraphBuilder = this)
    }
}

/**
 * Add screens related to Home into the graph.
 *
 * @param navGraphBuilder The builder used to construct the graph.
 * @param navController   The navigation controller.
 */
private fun addHomeScreens(navGraphBuilder: NavGraphBuilder, navController: NavHostController) {
    navGraphBuilder.composable(route = AppScreen.HomeScreen.route) {
        HomeScreen(
            navigateTo = { destination -> navController.navigateTo(destination) }
        )
    }
}

/**
 * Add screens related to Animations into the graph.
 *
 * @param navGraphBuilder The builder used to construct the graph.
 */
private fun addAnimationScreens(navGraphBuilder: NavGraphBuilder, navController: NavHostController) {
    navGraphBuilder.let {
        it.composable(route = AppScreen.AnimationsHubScreen.route) {
            AnimationsHubScreen(
                navigateTo = { destination -> navController.navigateTo(destination) }
            )
        }
        it.composable(route = AppScreen.AnimationCircleLoaderScreen.route) { CircleLoaderScreen() }
        it.composable(route = AppScreen.AnimationFollowingArrowsScreen.route) { FollowingArrowsScreen() }
        it.composable(route = AppScreen.AnimationGaugeScreen.route) { GaugeScreen() }
        it.composable(route = AppScreen.AnimationGlitterRainbowScreen.route) { GlitterRainbowScreen() }
        it.composable(route = AppScreen.AnimationLookaheadScreen.route) { LookaheadScreen() }
        it.composable(route = AppScreen.AnimationPulseScreen.route) { PulseScreen() }
        it.composable(route = AppScreen.AnimationShapeScreen.route) { ShapeScreen() }
        it.composable(route = AppScreen.AnimationSnowfallScreen.route) { SnowfallScreen() }
        it.composable(route = AppScreen.AnimationTypewriterScreen.route) { TypewriterScreen() }
    }
}

/**
 * Add screens related to Biometric into the graph.
 *
 * @param navGraphBuilder The builder used to construct the graph.
 * @param navController   The navigation controller.
 * @param context         The context of the application.
 */
private fun addBiometricScreens(navGraphBuilder: NavGraphBuilder, navController: NavHostController, context: Context) {
    navGraphBuilder.composable(route = AppScreen.BiometricLoginScreen.route) {
        BiometricLoginScreen(
            startBiometricEnrollment = { enrollIntent -> context.startActivity(enrollIntent) },
            navigateToBiometricHome = { navController.navigateTo(AppScreen.BiometricHomeScreen) }
        )
    }
    navGraphBuilder.composable(route = AppScreen.BiometricHomeScreen.route) {
        BackHandler(enabled = true) { }
        BiometricHomeScreen(
            navigateToBiometricLogin = {
                navController.navigateTo(destination = AppScreen.BiometricLoginScreen, popUpTo = true)
            }
        )
    }
}

/**
 * Add screens related to Bluetooth into the graph.
 *
 * @param navGraphBuilder The builder used to construct the graph.
 */
private fun addBluetoothScreens(navGraphBuilder: NavGraphBuilder) {
    navGraphBuilder.composable(route = AppScreen.BluetoothScreen.route) {
        LockScreenOrientation(orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        BluetoothScreen()
    }
}

/**
 * Add screens related to Bottom Sheet into the graph.
 *
 * @param navGraphBuilder The builder used to construct the graph.
 */
private fun addBottomSheetScreens(navGraphBuilder: NavGraphBuilder) {
    navGraphBuilder.composable(route = AppScreen.BottomSheetScreen.route) {
        BottomSheetScreen()
    }
}

/**
 * Add screens related to Camera into the graph.
 *
 * @param navGraphBuilder The builder used to construct the graph.
 */
private fun addCameraScreens(navGraphBuilder: NavGraphBuilder) {
    navGraphBuilder.composable(route = AppScreen.CameraScreen.route) {
        CameraScreen()
    }
}

/**
 * Add screens related to Emoji into the graph.
 *
 * @param navGraphBuilder The builder used to construct the graph.
 */
private fun addEmojiScreens(navGraphBuilder: NavGraphBuilder) {
    navGraphBuilder.composable(route = AppScreen.EmojiScreen.route) {
        EmojiScreen()
    }
}

/**
 * Add screens related to NFC into the graph.
 *
 * @param navGraphBuilder The builder used to construct the graph.
 * @param navController   The navigation controller.
 * @param context         The context of the application.
 */
private fun addNfcScreens(navGraphBuilder: NavGraphBuilder, navController: NavHostController, context: Context) {
    navGraphBuilder.composable(route = AppScreen.NfcCheckScreen.route) {
        CheckScreen(
            navigateToHub = { navController.navigateSingleTopTo(AppScreen.NfcHubScreen) },
            navigateToSettings = { context.startActivity(Intent(Settings.ACTION_NFC_SETTINGS)) }
        )
    }
    navGraphBuilder.composable(route = AppScreen.NfcHubScreen.route) {
        HubScreen(
            navigateToScanTag = { navController.navigateTo(AppScreen.NfcScanScreen) },
            navigateToTagHistory = { navController.navigateTo(AppScreen.NfcHistoryScreen) },
            navigateToEmulateTag = { navController.navigateTo(AppScreen.NfcEmulateScreen) }
        )
    }
    navGraphBuilder.composable(route = AppScreen.NfcScanScreen.route) {
        ScanScreen()
    }
    navGraphBuilder.composable(route = AppScreen.NfcHistoryScreen.route) {
        HistoryScreen()
    }
    navGraphBuilder.composable(route = AppScreen.NfcEmulateScreen.route) {
        EmulateScreen()
    }
}

/**
 * Add screens related to Notification into the graph.
 *
 * @param navGraphBuilder The builder used to construct the graph.
 */
private fun addNotificationScreens(navGraphBuilder: NavGraphBuilder) {
    navGraphBuilder.composable(
        route = AppScreen.NotificationScreen.route,
        deepLinks = listOf(navDeepLink { uriPattern = AppScreen.NotificationScreen.deeplinkPath })
    ) {
        NotificationScreen()
    }
}

/**
 * Add screens related to Paging into the graph.
 *
 * @param navGraphBuilder The builder used to construct the graph.
 */
private fun addPagingScreens(navGraphBuilder: NavGraphBuilder) {
    navGraphBuilder.composable(route = AppScreen.PagingScreen.route) {
        PagingScreen()
    }
}

/**
 * Add screens related to REST API into the graph.
 *
 * @param navGraphBuilder The builder used to construct the graph.
 */
private fun addRestApiScreens(navGraphBuilder: NavGraphBuilder) {
    navGraphBuilder.composable(route = AppScreen.RestApiScreen.route) {
        RestApiScreen()
    }
}

/**
 * Add screens related to SnackBar into the graph.
 *
 * @param navGraphBuilder The builder used to construct the graph.
 */
private fun addSnackBarScreens(navGraphBuilder: NavGraphBuilder) {
    navGraphBuilder.composable(route = AppScreen.SnackBarScreen.route) {
        SnackBarScreen()
    }
}

/**
 * Add screens related to Telephony into the graph.
 *
 * @param navGraphBuilder The builder used to construct the graph.
 */
private fun addTelephonyScreens(navGraphBuilder: NavGraphBuilder) {
    navGraphBuilder.composable(route = AppScreen.TelephonyScreen.route) {
        TelephonyScreen()
    }
}

/**
 * Locks the screen orientation then restores the original orientation when the view disappears.
 *
 * @param orientation The orientation to force. Must be a ScreenOrientation from [ActivityInfo].
 */
@Composable
private fun LockScreenOrientation(orientation: Int) {
    LocalContext.current.findActivity()?.let { activity ->
        DisposableEffect(Unit) {
            val originalOrientation = activity.requestedOrientation
            activity.requestedOrientation = orientation
            onDispose {
                // Restore original orientation when view disappears
                activity.requestedOrientation = originalOrientation
            }
        }
    }
}
