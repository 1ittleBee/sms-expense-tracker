package com.trackapp.smsexpensetracker.smsingestion.presentation

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.trackapp.smsexpensetracker.smsingestion.data.gateway.SmsPermissionSpec
import com.trackapp.smsexpensetracker.smsingestion.data.gateway.hasSmsPermission
import com.trackapp.smsexpensetracker.smsingestion.domain.model.DefaultRationaleCopy
import com.trackapp.smsexpensetracker.smsingestion.domain.model.PermissionState

@Composable
fun PermissionScreen(
    onPermissionGranted: () -> Unit,
    viewModel: PermissionViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val state by viewModel.state.collectAsStateWithLifecycle()

    fun shouldShowRationaleNow(): Boolean =
        activity?.let { act ->
            SmsPermissionSpec.REQUIRED_PERMISSIONS.any { permission ->
                ActivityCompat.shouldShowRequestPermissionRationale(act, permission)
            }
        } ?: false

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions(),
    ) { results ->
        val granted = results.values.all { it }
        viewModel.onPermissionResult(granted, shouldShowRationaleNow())
    }

    LaunchedEffect(Unit) {
        viewModel.onScreenResumed(hasSmsPermission(context), shouldShowRationaleNow())
    }

    LaunchedEffect(state) {
        if (state is PermissionState.Granted) {
            onPermissionGranted()
        }
    }

    PermissionContent(
        state = state,
        onRequestClick = { launcher.launch(SmsPermissionSpec.REQUIRED_PERMISSIONS) },
        onOpenSettingsClick = {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.fromParts("package", context.packageName, null)
            }
            context.startActivity(intent)
        },
    )
}

/**
 * Stateless rendering of the permission flow, extracted from [PermissionScreen] so it can be
 * exercised in Compose UI tests without a Hilt-provided ViewModel.
 */
@Composable
fun PermissionContent(
    state: PermissionState,
    onRequestClick: () -> Unit,
    onOpenSettingsClick: () -> Unit,
) {
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            when (state) {
                is PermissionState.NotRequested, is PermissionState.Denied -> {
                    Text(text = DefaultRationaleCopy.value.title, style = MaterialTheme.typography.headlineSmall)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = DefaultRationaleCopy.value.body, style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(onClick = onRequestClick) {
                        Text(text = if (state is PermissionState.Denied) "Retry" else "Continue")
                    }
                }

                is PermissionState.PermanentlyDenied -> {
                    Text(text = "Permission needed", style = MaterialTheme.typography.headlineSmall)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "You've denied SMS access permanently. Enable it from Settings to " +
                            "use transaction capture.",
                        style = MaterialTheme.typography.bodyLarge,
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(onClick = onOpenSettingsClick) {
                        Text(text = "Open Settings")
                    }
                }

                is PermissionState.Granted -> {
                    Text(text = "Permission granted", style = MaterialTheme.typography.headlineSmall)
                }
            }
        }
    }
}
