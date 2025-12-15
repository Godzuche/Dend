package com.godzuche.dend.app

import android.app.role.RoleManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.viewmodel.compose.viewModel
import com.godzuche.dend.features.onboarding.impl.presentation.OnboardingViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val onboardingViewModel = viewModel<OnboardingViewModel>()

            val callScreeningRoleLauncher = rememberLauncherForActivityResult(
                ActivityResultContracts.StartActivityForResult()
            ) { result: ActivityResult ->
                if (result.resultCode == RESULT_OK) {
                    //  you will get result here in result.data
                    Log.d("MainActivity", "Role granted. data: ${result.data?.data}")
                    onboardingViewModel.onRolePermissionResult(true)
//                    permissionLauncher.launch(CALL_SCREENING_PERMISSIONS)
                } else {
                    Log.d("MainActivity", "Role denied")
                    onboardingViewModel.onRolePermissionResult(false)
                }
            }

            val roleManager = getSystemService(ROLE_SERVICE) as RoleManager
            fun launchRoleRequest() {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//                val roleManager = getSystemService(ROLE_SERVICE) as RoleManager
                val intent = roleManager.createRequestRoleIntent(RoleManager.ROLE_CALL_SCREENING)
                callScreeningRoleLauncher.launch(intent)
//            }
            }

            App(
                onRequestRolePermission = {
                    if (roleManager.isRoleHeld(RoleManager.ROLE_CALL_SCREENING)) {
                        // The role is already granted. Maybe the user went to settings and did it manually.
                        // Proceed to the next step.
                        onboardingViewModel.onRolePermissionResult(true)
                    } else {
                        launchRoleRequest()
                    }
                },
                onboardingViewModel = onboardingViewModel,
            )
        }
    }
}