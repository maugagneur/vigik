package com.kidor.vigik.ui.snackbar

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.kidor.vigik.R
import com.kidor.vigik.ui.theme.dimensions
import kotlinx.coroutines.launch

/**
 * View that display the section dedicated to a custom snackbar.
 */
@Preview
@Composable
fun SnackBarScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(all = MaterialTheme.dimensions.commonSpaceLarge),
        contentAlignment = Alignment.Center
    ) {
        Button(
            onClick = {
                scope.launch {
                    // Show the snackbar
                    val result = snackbarHostState.showSnackbar(
                        message = context.getString(R.string.snackbar_message),
                        actionLabel = context.getString(R.string.snackbar_action_button_label),
                        withDismissAction = true,
                        duration = SnackbarDuration.Indefinite
                    )
                    // Handle the snackbar result
                    when (result) {
                        SnackbarResult.Dismissed ->
                            Toast.makeText(
                                context,
                                context.getString(R.string.snackbar_action_confirmed),
                                Toast.LENGTH_SHORT
                            ).show()

                        SnackbarResult.ActionPerformed ->
                            Toast.makeText(
                                context,
                                context.getString(R.string.snackbar_action_canceled),
                                Toast.LENGTH_SHORT
                            ).show()
                    }
                }
            }
        ) {
            Text(
                text = stringResource(id = R.string.snackbar_dummy_action_button_label).uppercase(),
                fontSize = MaterialTheme.dimensions.textSizeMedium,
                textAlign = TextAlign.Center
            )
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        ) { data ->
            CountdownSnackbar(
                snackbarData = data,
                shape = RoundedCornerShape(size = MaterialTheme.dimensions.commonSpaceMedium),
                contentColor = MaterialTheme.colorScheme.onPrimary,
                containerColor = MaterialTheme.colorScheme.primary,
                actionColor = MaterialTheme.colorScheme.secondary
            )
        }
    }
}
