package com.trackapp.smsexpensetracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.trackapp.smsexpensetracker.smsingestion.presentation.PermissionScreen
import com.trackapp.smsexpensetracker.ui.theme.SmsExpenseTrackerTheme
import dagger.hilt.android.AndroidEntryPoint

private const val ROUTE_PERMISSION = "permission"

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SmsExpenseTrackerTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val navController = rememberNavController()
                    NavHost(navController = navController, startDestination = ROUTE_PERMISSION) {
                        composable(ROUTE_PERMISSION) {
                            PermissionScreen(
                                onPermissionGranted = {
                                    // Bolt 002-sms-ingestion (import + live detection) reacts to the
                                    // granted state via PermissionCoordinator; no further destination
                                    // exists yet in this bolt's scope.
                                },
                            )
                        }
                    }
                }
            }
        }
    }
}
