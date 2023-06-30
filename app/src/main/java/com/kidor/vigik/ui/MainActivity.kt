package com.kidor.vigik.ui

import android.content.Intent
import android.nfc.NfcAdapter
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.InvertColors
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.kidor.vigik.R
import com.kidor.vigik.data.nfc.api.NfcApi
import com.kidor.vigik.ui.compose.AppNavHost
import com.kidor.vigik.ui.compose.AppScreen
import com.kidor.vigik.ui.compose.AppTheme
import com.kidor.vigik.ui.compose.getScreen
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

internal const val ACTION_BAR_TEST_TAG = "Action bar"
internal const val ACTION_MENU_STOP_SCAN = "Action menu - Stop scan"
internal const val ACTION_MENU_INVERT_COLORS = "Action menu - Invert colors"
internal const val ACTION_MENU_APP_INFO = "Action menu - App info"

/**
 * Main activity of the application.
 */
@AndroidEntryPoint
class MainActivity : FragmentActivity() {

    @Inject
    internal lateinit var nfcApi: NfcApi

    @ExperimentalMaterial3Api
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

@ExperimentalMaterial3Api
@Composable
@Preview(widthDp = 400, heightDp = 700)
internal fun MainComposable() {
    val navController = rememberNavController()
    val currentBackStack by navController.currentBackStackEntryAsState()
    val currentDestination = currentBackStack?.destination
    val currentScreen = getScreen(currentDestination?.route)

    Scaffold(
        topBar = { AppActionBar(currentScreen = currentScreen, navController = navController) },
        contentColor = MaterialTheme.colorScheme.onPrimary
    ) { innerPadding ->
        AppNavHost(
            context = LocalContext.current,
            navController = navController,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@ExperimentalMaterial3Api
@Composable
private fun AppActionBar(
    currentScreen: AppScreen?,
    navController: NavHostController
) = TopAppBar(
    title = {
        Text(
            text = currentScreen?.name() ?: "",
            fontSize = AppTheme.dimensions.textSizeXLarge,
            color = MaterialTheme.colorScheme.onPrimary
        )
    },
    modifier = Modifier.testTag(ACTION_BAR_TEST_TAG),
    navigationIcon = {
        if (navController.previousBackStackEntry != null && currentScreen?.showNavigateBack == true) {
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
        if (currentScreen == AppScreen.NfcScanScreen) {
            IconButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier.testTag(ACTION_MENU_STOP_SCAN)
            ) {
                Icon(
                    Icons.Default.Stop,
                    contentDescription = stringResource(id = R.string.nfc_menu_action_stop_scan),
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
        IconButton(
            onClick = { AppTheme.invertTheme() },
            modifier = Modifier.testTag(ACTION_MENU_INVERT_COLORS)
        ) {
            Icon(
                Icons.Default.InvertColors,
                contentDescription = stringResource(id = R.string.menu_action_invert_color),
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }
        IconButton(
            onClick = { },
            modifier = Modifier.testTag(ACTION_MENU_APP_INFO)
        ) {
            Icon(
                Icons.Default.Info,
                contentDescription = stringResource(id = R.string.menu_action_app_info),
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }
    },
    colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary)
)
