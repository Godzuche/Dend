package com.godzuche.dend.core.presentation.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings

fun Context.openAppSettings() {
    Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.fromParts("package", packageName, null)
    ).also(::startActivity)
}

fun Context.haveAllPermissions(permissions: Array<String>): Boolean {
    return permissions.all { checkSelfPermission(it) == PackageManager.PERMISSION_GRANTED }
}

fun Context.checkPermissions(permissions: Array<String>): Map<String, Boolean> {
    return permissions.associateWith {
        checkSelfPermission(it) == PackageManager.PERMISSION_GRANTED
    }
}

fun Activity.isPermissionPermanentlyDeclined(permission: String) =
    !this.shouldShowRequestPermissionRationale(
        permission
    )
