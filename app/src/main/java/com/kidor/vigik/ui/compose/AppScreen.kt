package com.kidor.vigik.ui.compose

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.res.stringResource
import com.kidor.vigik.R

private const val APP_BASE_URI = "myapp://navigation"

/**
 * Metadata of each screen of the application.
 *
 * A screen is defined by its [name] and its [route] in the navigation graph.
 * You can also hide the back arrow in action by setting [showNavigateBack] to false.
 * If the screen is associated to an URI, this value can be find in [deeplinkPath].
 */
enum class AppScreen(
    @StringRes private val nameId: Int,
    val route: String,
    val deeplinkPath: String = "",
    val showNavigateBack: Boolean = true
) {

    /**
     * Metadata of 'home" screen.
     */
    HomeScreen(
        nameId = R.string.home_title,
        route = "home"
    ),

    /**
     * Metadata of "Animations" hub screen.
     */
    AnimationsHubScreen(
        nameId = R.string.animations_hub_title,
        route = "animations_hub"
    ),

    /**
     * Metadata of "Following arrows" animation screen.
     */
    AnimationFollowingArrowsScreen(
        nameId = R.string.animation_following_arrows_title,
        route = "animation_following_arrows"
    ),

    /**
     * Metadata of "Glitter Rainbow" animation screen.
     */
    AnimationGlitterRainbowScreen(
        nameId = R.string.animation_glitter_rainbow_title,
        route = "animation_glitter_rainbow"
    ),

    /**
     * Metadata of "Lookahead" animation screen.
     */
    AnimationLookaheadScreen(
        nameId = R.string.animation_lookahead_title,
        route = "animation_lookahead"
    ),

    /**
     * Metadata of "Shape" animation screen.
     */
    AnimationShapeScreen(
        nameId = R.string.animation_shape_title,
        route = "animation_shape"
    ),

    /**
     * Metadata of "Snowfall" animation screen.
     */
    AnimationSnowfallScreen(
        nameId = R.string.animation_snowfall_title,
        route = "animation_snowfall"
    ),

    /**
     * Metadata of "Typewriter" animation screen.
     */
    AnimationTypewriterScreen(
        nameId = R.string.animation_typewriter_title,
        route = "animation_typewriter"
    ),

    /**
     * Metadata of "biometric login" screen.
     */
    BiometricLoginScreen(
        nameId = R.string.biometric_title,
        route = "biometric_login"
    ),

    /**
     * Metadata of "biometric home" screen.
     */
    BiometricHomeScreen(
        nameId = R.string.biometric_title,
        route = "biometric_home",
        showNavigateBack = false
    ),

    /**
     * Metadata of "Bluetooth" screen.
     */
    BluetoothScreen(
        nameId = R.string.bluetooth_title,
        route = "bluetooth"
    ),

    /**
     * Metadata of "Bottom Sheet" screen.
     */
    BottomSheetScreen(
        nameId = R.string.bottom_sheet_title,
        route = "bottom_sheet"
    ),

    /**
     * Metadata of "camera" screen.
     */
    CameraScreen(
        nameId = R.string.camera_title,
        route = "camera"
    ),

    /**
     * Metadata of "Emoji" screen.
     */
    EmojiScreen(
        nameId = R.string.emoji_title,
        route = "emoji"
    ),

    /**
     * Metadata of "check" screen.
     */
    NfcCheckScreen(
        nameId = R.string.nfc_check_title,
        route = "check"
    ),

    /**
     * Metadata of "hub" screen.
     */
    NfcHubScreen(
        nameId = R.string.nfc_hub_title,
        route = "hub"
    ),

    /**
     * Metadata of "scan" screen.
     */
    NfcScanScreen(
        nameId = R.string.nfc_scan_title,
        route = "scan"
    ),

    /**
     * Metadata of "history" screen.
     */
    NfcHistoryScreen(
        nameId = R.string.nfc_tag_history_title,
        route = "history"
    ),

    /**
     * Metadata of "emulate" screen.
     */
    NfcEmulateScreen(
        nameId = R.string.nfc_emulate_title,
        route = "emulate"
    ),

    /**
     * Metadata of "notification" screen.
     */
    NotificationScreen(
        nameId = R.string.notification_title,
        route = "notification",
        deeplinkPath = "${APP_BASE_URI}/notification"
    ),

    /**
     * Metadata of "paging" screen.
     */
    PagingScreen(
        nameId = R.string.paging_title,
        route = "paging"
    ),

    /**
     * Metadata of "REST API" screen.
     */
    RestApiScreen(
        nameId = R.string.rest_api_title,
        route = "rest_api"
    ),

    /**
     * Metadata of "telephony" screen.
     */
    TelephonyScreen(
        nameId = R.string.telephony_title,
        route = "telephony"
    );

    /**
     * Returns the string resource of the screen's name.
     */
    @Composable
    @ReadOnlyComposable
    fun name(): String = stringResource(id = nameId)

    companion object {
        /**
         * Returns the screen matching given [route] if exists.
         */
        fun getScreen(route: String?): AppScreen? = entries.find { it.route == route }
    }
}
