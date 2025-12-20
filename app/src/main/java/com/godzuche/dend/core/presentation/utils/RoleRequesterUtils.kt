package com.godzuche.dend.core.presentation.utils

import android.app.Activity
import android.app.role.RoleManager
import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.godzuche.dend.core.services.callscreening.RoleRequester

/**
 * A Composable that provides a remembered instance of a RoleRequester.
 *
 * This acts as a "controller" at the Composable level, bridging the UI
 * with the business logic in the RoleRequester class. It neatly encapsulates
 * the `rememberLauncherForActivityResult` boilerplate.
 *
 * @param onResult Callback invoked with the result of the role request (true for success, false for denial).
 */
@Composable
fun rememberRoleRequester(
    onResult: (isGranted: Boolean) -> Unit
): RoleRequester {
    val context = LocalContext.current
    val roleManager = remember {
        context.getSystemService(Context.ROLE_SERVICE) as RoleManager
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        onResult(result.resultCode == Activity.RESULT_OK)
    }

    // remember creates a stable instance of our helper class that survives recomposition.
    return remember(roleManager, launcher) {
        RoleRequester(
            roleManager = roleManager,
            onLaunchIntent = { intent ->
                launcher.launch(intent)
            }
        )
    }
}