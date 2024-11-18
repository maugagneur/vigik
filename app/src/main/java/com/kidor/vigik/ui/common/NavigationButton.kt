package com.kidor.vigik.ui.common

import android.view.SoundEffectConstants
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.style.TextAlign
import com.kidor.vigik.navigation.AppScreen
import com.kidor.vigik.ui.theme.dimensions

/**
 * Button used to navigate between screens.
 *
 * @param destination The destination associated with the button.
 * @param onClick     Called when the button is clicked.
 */
@Composable
fun NavigationButton(
    destination: AppScreen,
    onClick: (AppScreen) -> Unit
) {
    val view = LocalView.current
    Button(
        onClick = {
            view.playSoundEffect(SoundEffectConstants.CLICK)
            onClick(destination)
        },
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = destination.name().uppercase(),
            fontSize = MaterialTheme.dimensions.textSizeMedium,
            textAlign = TextAlign.Center
        )
    }
}
