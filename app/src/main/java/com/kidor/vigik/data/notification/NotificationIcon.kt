package com.kidor.vigik.data.notification

import androidx.annotation.DrawableRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.TipsAndUpdates
import androidx.compose.ui.graphics.vector.ImageVector
import com.kidor.vigik.R

/**
 * Available icons for notification.
 *
 * @param vectorImage The [ImageVector] that can be used in Compose UI.
 * @param drawableId  The [DrawableRes] used to build the notification.
 */
enum class NotificationIcon(val vectorImage: ImageVector, @DrawableRes val drawableId: Int) {
    /** Default notification icon. **/
    DEFAULT(Icons.Default.Notifications, R.drawable.ic_notifications),
    /** Tips & Updates notification icon. **/
    TIPS_AND_UPDATES(Icons.Default.TipsAndUpdates, R.drawable.ic_tips_and_updates),
    /** Rocket notification icon. **/
    ROCKET_LAUNCH(Icons.Default.RocketLaunch, R.drawable.ic_rocket_launch)
}
