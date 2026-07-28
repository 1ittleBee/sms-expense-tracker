package com.trackapp.smsexpensetracker.smsingestion.data.gateway

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

/** The only two SMS permissions this app ever declares or requests. SEND_SMS is never included. */
object SmsPermissionSpec {
    val REQUIRED_PERMISSIONS: Array<String> = arrayOf(
        Manifest.permission.READ_SMS,
        Manifest.permission.RECEIVE_SMS,
    )
}

fun hasSmsPermission(context: Context): Boolean =
    SmsPermissionSpec.REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
    }
