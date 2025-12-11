package com.godzuche.dend

import android.Manifest
import android.app.Activity
import android.app.role.RoleManager
import android.content.ComponentName
import android.content.Context
import android.content.Context.BIND_AUTO_CREATE
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.telecom.Call
import android.telecom.CallScreeningService
import android.telecom.Connection
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.godzuche.dend.ui.theme.DendTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val permissionLauncher = rememberLauncherForActivityResult(
                ActivityResultContracts.RequestMultiplePermissions()
            ) { perms ->
                val grantedAll = perms.values.all { it }
                if (grantedAll) {
                    Log.d("MainActivity", "Perm Granted All")
                    applicationContext.bindMyService()
                } else {
                    // Todo: Handle rejection.
                    Log.d(
                        "MainActivity",
                        "Perm Not Granted: ${perms.filter { !it.value }.map { it.key }}"
                    )
                }
            }

            val callScreeningRoleLauncher = rememberLauncherForActivityResult(
                ActivityResultContracts.StartActivityForResult()
            ) { result: androidx.activity.result.ActivityResult ->
                if (result.resultCode == Activity.RESULT_OK) {
                    //  you will get result here in result.data
                    Log.d("MainActivity", "Role granted. data: ${result.data?.data}")
//                    applicationContext.bindMyService()
                    permissionLauncher.launch(CALL_SCREENING_PERMISSIONS)
                } else {
                    Log.d("MainActivity", "Role denied")
                }
            }

            fun launchRoleRequest() {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val roleManager = getSystemService(ROLE_SERVICE) as RoleManager
                val intent = roleManager.createRequestRoleIntent(RoleManager.ROLE_CALL_SCREENING)
                callScreeningRoleLauncher.launch(intent)
//            }
            }

            LaunchedEffect(Unit) {
                launchRoleRequest()
            }

            DendTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    DendTheme {
        Greeting("Android")
    }
}

private fun Context.bindMyService() {
    Log.d("MainActivity", "binding my service")
    val mCallServiceIntent = Intent("android.telecom.CallScreeningService")
    mCallServiceIntent.setPackage(applicationContext.packageName)
    val mServiceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(componentName: ComponentName, iBinder: IBinder) {
            // iBinder is an instance of CallScreeningService.CallScreenBinder
            // CallScreenBinder is an inner class present inside CallScreenService
            Log.d("MyCallScreeningService", "onServiceConnected")
        }

        override fun onServiceDisconnected(componentName: ComponentName) {
            Log.d("MyCallScreeningService", "onServiceDisconnected")
        }

        override fun onBindingDied(name: ComponentName) {
            Log.d("MyCallScreeningService", "onServiceBindingDied")
        }
    }
    bindService(mCallServiceIntent, mServiceConnection, BIND_AUTO_CREATE)
}

class ScreeningService : CallScreeningService() {
    // This function is called when an ingoing or outgoing call
    // is from a number not in the user's contacts list
    override fun onScreenCall(callDetails: Call.Details) {
        Log.d("MyCallScreeningService", "onScreenCall called")
        // Can check the direction of the call
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val isIncoming = callDetails.callDirection == Call.Details.DIRECTION_INCOMING

        if (isIncoming) {
            // the handle (e.g. phone number) that the Call is currently connected to
            val handle: Uri = callDetails.handle
            // Extract the incoming phone number
            val incomingNumber = extractPhoneNumber(callDetails)
            Log.d("MyCallScreeningService", "Incoming Call = $incomingNumber")

            // determine if you want to allow or reject the call
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                when (callDetails.callerNumberVerificationStatus) {
                    Connection.VERIFICATION_STATUS_FAILED -> {
                        // Network verification failed, likely an invalid/spam call.
                        Log.d(
                            "MyCallScreeningService",
                            "Network verification failed, likely an invalid/spam call"
                        )
                    }

                    Connection.VERIFICATION_STATUS_PASSED -> {
                        // Network verification passed, likely a valid call.
                        Log.d(
                            "MyCallScreeningService",
                            "Network verification passed, likely a valid call"
                        )
                    }

                    else -> {
                        // Network could not perform verification.
                        // This branch matches Connection.VERIFICATION_STATUS_NOT_VERIFIED.
                        Log.d(
                            "MyCallScreeningService",
                            "Network could not perform verification. Not verified"
                        )
                    }
                }
            }


            // To disallow (but not rejected) and send to voicemail - Soft Block
            val response1 = CallResponse.Builder()
                .setDisallowCall(true)
                .setRejectCall(false)
                .setSkipCallLog(true) // Recommended
                .setSkipNotification(true) // Recommended
                .build()

            // To disallow and hang up (reject) - Hard Block
            val blockAndRejectResponse = CallResponse.Builder()
                .setDisallowCall(true)
                .setRejectCall(true) // This hangs up the call
                .setSkipCallLog(true)
                .setSkipNotification(true)
                .build()

            // Allow, but silent
            val allowAndSilentResponse =
                CallResponse.Builder()
                    .setSilenceCall(true)
                    .build()

            // Tell the system how to respond to the incoming call
            // and if it should notify the user of the call.
            val normalResponse = CallResponse.Builder()
                // Sets whether the incoming call should be blocked.
                .setDisallowCall(false)
                // Sets whether the incoming call should be rejected as if the user did so manually.
                .setRejectCall(false)
                // Sets whether ringing should be silenced for the incoming call.
                .setSilenceCall(false)
                // Sets whether the incoming call should not be displayed in the call log.
                .setSkipCallLog(false)
                // Sets whether a missed call notification should not be shown for the incoming call.
                .setSkipNotification(false)
                .build()

            // Call this function to provide your screening response.
            respondToCall(callDetails, normalResponse)
        }
//        }
    }
}

private fun extractPhoneNumber(callDetails: Call.Details): String? {
    val handle = callDetails.handle
    if (handle != null) {
        // Attempt to extract phone number from the handle
        return handle.schemeSpecificPart
    } else {
        // Handle is null, try other methods if available
        val gatewayInfo = callDetails.gatewayInfo
        if (gatewayInfo != null) {
            return gatewayInfo.originalAddress.schemeSpecificPart
        }
    }
    return null
}

val CALL_SCREENING_PERMISSIONS = arrayOf(
    Manifest.permission.ANSWER_PHONE_CALLS,
    Manifest.permission.READ_CONTACTS,
    Manifest.permission.READ_CALL_LOG,
    Manifest.permission.WRITE_CALL_LOG,
)

fun Context.haveAllPermissions(permissions: Array<String>): Boolean {
    return permissions.all { checkSelfPermission(it) == PackageManager.PERMISSION_GRANTED }
}
