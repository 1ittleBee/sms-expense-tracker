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
import com.trackapp.smsexpensetracker.smsingestion.presentation.ImportProgressScreen
import com.trackapp.smsexpensetracker.smsingestion.presentation.PermissionScreen
import com.trackapp.smsexpensetracker.ui.theme.SmsExpenseTrackerTheme
import dagger.hilt.android.AndroidEntryPoint

private const val ROUTE_PERMISSION = "permission"
private const val ROUTE_IMPORT = "import"

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
                                    navController.navigate(ROUTE_IMPORT) {
                                        popUpTo(ROUTE_PERMISSION) { inclusive = true }
                                    }
                                },
                            )
                        }
                        composable(ROUTE_IMPORT) {
                            ImportProgressScreen()
                        }
                    }
                }
            }
        }
    }
}
