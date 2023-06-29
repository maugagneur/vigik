package com.kidor.vigik.ui.nfc.hub

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
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
 * View that display all sections of the application.
 */
@Preview(widthDp = 400, heightDp = 700)
@Composable
fun HubScreen(
    navigateToScanTag: () -> Unit = {},
    navigateToTagHistory: () -> Unit = {},
    navigateToEmulateTag: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .padding(
                start = AppTheme.dimensions.commonSpaceXLarge,
                end = AppTheme.dimensions.commonSpaceXLarge
            ),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = navigateToScanTag,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = stringResource(id = R.string.nfc_scan_button_label).uppercase(),
                fontSize = AppTheme.dimensions.textSizeMedium
            )
        }
        Button(
            onClick = navigateToTagHistory,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = stringResource(id = R.string.nfc_history_button_label).uppercase(),
                fontSize = AppTheme.dimensions.textSizeMedium
            )
        }
        Button(
            onClick = navigateToEmulateTag,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = stringResource(id = R.string.nfc_emulate_button_label).uppercase(),
                fontSize = AppTheme.dimensions.textSizeMedium
            )
        }
    }
}
