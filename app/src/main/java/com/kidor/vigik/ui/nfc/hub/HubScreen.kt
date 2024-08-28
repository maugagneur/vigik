package com.kidor.vigik.ui.nfc.hub

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.kidor.vigik.R
import com.kidor.vigik.ui.compose.dimensions

/**
 * View that display all sub-sections of NFC.
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
            .padding(horizontal = MaterialTheme.dimensions.commonSpaceXLarge),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = navigateToScanTag,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = stringResource(id = R.string.nfc_scan_button_label).uppercase(),
                fontSize = MaterialTheme.dimensions.textSizeMedium
            )
        }
        Button(
            onClick = navigateToTagHistory,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = stringResource(id = R.string.nfc_history_button_label).uppercase(),
                fontSize = MaterialTheme.dimensions.textSizeMedium
            )
        }
        Button(
            onClick = navigateToEmulateTag,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = stringResource(id = R.string.nfc_emulate_button_label).uppercase(),
                fontSize = MaterialTheme.dimensions.textSizeMedium
            )
        }
    }
}
