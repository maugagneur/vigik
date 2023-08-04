package com.kidor.vigik.ui.notification

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.kidor.vigik.R
import com.kidor.vigik.ui.compose.AppTheme

/**
 * View that display the section dedicated to notifications.
 */
@Composable
@Preview(widthDp = 400, heightDp = 700)
fun NotificationScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(all = AppTheme.dimensions.commonSpaceXLarge),
        contentAlignment = Alignment.Center
    ) {
        Button(
            onClick = { /*TODO*/ },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = stringResource(id = R.string.notification_generate_notification_button_label).uppercase(),
                fontSize = AppTheme.dimensions.textSizeMedium
            )
        }
    }
}
