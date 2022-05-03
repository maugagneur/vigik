package com.kidor.vigik.ui

import android.content.Intent
import android.nfc.NfcAdapter
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.kidor.vigik.R
import com.kidor.vigik.databinding.ActivityMainBinding
import com.kidor.vigik.nfc.api.NfcApi
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * Main activity of the application.
 */
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    @Inject internal lateinit var nfcApi: NfcApi

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityMainBinding.inflate(layoutInflater).let {
            setContentView(it.root)
            setSupportActionBar(it.toolbar)
        }

        navController = (supportFragmentManager.findFragmentById(R.id.navFragmentContainerView) as NavHostFragment)
            .navController.also {
                appBarConfiguration = AppBarConfiguration.Builder(it.graph).build()
                NavigationUI.setupActionBarWithNavController(this, it)
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

    override fun onSupportNavigateUp() =
        NavigationUI.navigateUp(navController, appBarConfiguration) || super.onSupportNavigateUp()

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (intent?.action == NfcAdapter.ACTION_TAG_DISCOVERED) {
            nfcApi.onNfcIntentReceived(intent)
        }
    }
}
