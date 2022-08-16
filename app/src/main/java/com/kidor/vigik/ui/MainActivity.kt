package com.kidor.vigik.ui

import android.content.Intent
import android.nfc.NfcAdapter
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.InvertColors
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.kidor.vigik.R
import com.kidor.vigik.nfc.api.NfcApi
import com.kidor.vigik.ui.compose.AppNavHost
import com.kidor.vigik.ui.compose.AppNavigation
import com.kidor.vigik.ui.compose.AppTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * Main activity of the application.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    internal lateinit var nfcApi: NfcApi

    override fun onCreate(savedInstanceState: Bundle?) {
        // Handle the splash screen transition
        installSplashScreen()

        super.onCreate(savedInstanceState)

        setContent {
            AppTheme {
                MainComposable()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        nfcApi.enableNfcForegroundDispatch(this, javaClass)
    }

    override fun onPause() {
        super.onPause()
        nfcApi.disableNfcForegroundDispatch(this)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (intent?.action == NfcAdapter.ACTION_TAG_DISCOVERED) {
            nfcApi.onNfcIntentReceived(intent)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview(widthDp = 400, heightDp = 700)
internal fun MainComposable() {
    val navController = rememberNavController()
    val currentBackStack by navController.currentBackStackEntryAsState()
    val currentDestination = currentBackStack?.destination
    val currentScreen = AppNavigation.getScreen(currentDestination?.route)

    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = {
                    Text(
                        text = currentScreen?.name() ?: "",
                        fontSize = AppTheme.dimensions.textSizeXLarge,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                },
                navigationIcon = {
                    if (navController.previousBackStackEntry != null) {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                Icons.Default.ArrowBack,
                                contentDescription = stringResource(id = R.string.menu_action_back),
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                },
                actions = {
                    if (currentScreen == AppNavigation.ScanScreen) {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                Icons.Default.Stop,
                                contentDescription = stringResource(id = R.string.menu_action_stop_scan),
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                    IconButton(onClick = { AppTheme.invertTheme() }) {
                        Icon(
                            Icons.Default.InvertColors,
                            contentDescription = stringResource(id = R.string.menu_action_invert_color),
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        contentColor = MaterialTheme.colorScheme.onPrimary
    ) { innerPadding ->
        AppNavHost(
            context = LocalContext.current,
            navController = navController,
            modifier = Modifier.padding(innerPadding)
        )
    }
}
