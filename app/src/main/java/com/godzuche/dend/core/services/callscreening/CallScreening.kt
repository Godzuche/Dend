package com.godzuche.dend.core.services.callscreening

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ComponentName
import android.content.Context
import android.content.Context.BIND_AUTO_CREATE
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.IBinder
import android.telecom.Call
import android.telecom.CallScreeningService
import android.telecom.Connection
import android.util.Log
import androidx.core.app.NotificationCompat
import com.godzuche.dend.R
import com.godzuche.dend.core.data.PhoneCallDataSource
import com.godzuche.dend.core.data.utils.PhoneNumberNormalizer
import com.godzuche.dend.core.domain.model.FirewallState
import com.godzuche.dend.core.domain.repository.UserDataRepository
import com.godzuche.dend.features.activity.impl.domain.repository.ActivityRepository
import com.godzuche.dend.features.rules.impl.domain.repository.RulesRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.time.Clock

fun Context.bindMyService() {
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

class ScreeningService : CallScreeningService(), KoinComponent {
    val userDataRepository by inject<UserDataRepository>()
    val rulesRepository by inject<RulesRepository>()
    val activityRepository by inject<ActivityRepository>()
    val phoneNumberNormalizer by inject<PhoneNumberNormalizer>()
    val phoneCallDataSource by inject<PhoneCallDataSource>()

    private val NOTIFICATION_CHANNEL_ID = "ScreeningServiceChannel"
    private val NOTIFICATION_ID = 1337

//    private val appContext: Context by inject()
//    private var serviceScope: CoroutineScope? = null

//    override fun onBind(intent: Intent?): IBinder? {
//        Log.d("ScreeningService", "Service bound.")
//        serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
//        return super.onBind(intent)
//    }
//
//    override fun onUnbind(intent: Intent?): Boolean {
//        Log.d("ScreeningService", "Service unbound.")
//        serviceScope?.cancel() // Cancel the scope to clean up any running coroutines
//        return false
//    }

    override fun onScreenCall(callDetails: Call.Details) {
        startForeground(NOTIFICATION_ID, createNotification())

        runBlocking {
            try {
                withTimeout(1000L) {
                    // Check if the call is from a number (not an enterprise or unknown source)
                    if (callDetails.handle.scheme != "tel") {
                        allowCall(callDetails)
                        return@withTimeout
                    }

                    val firewallState =
                        userDataRepository
                            .userPreferencesData
                            .first()
                            .firewallState

                    Log.d(
                        "MyCallScreeningService",
                        "onScreenCall called with firewallState: $firewallState"
                    )
                    // Can check the direction of the call
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    val isIncoming = callDetails.callDirection == Call.Details.DIRECTION_INCOMING

                    if (isIncoming) {
                        // the handle (e.g. phone number) that the Call is currently connected to
//            val handle: Uri = callDetails.handle
                        // Extract the incoming phone number
                        val incomingNumber = extractPhoneNumber(callDetails)
                        Log.d("MyCallScreeningService", "Incoming Call number = $incomingNumber")

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

                        if (firewallState == FirewallState.OFF) {
                            allowCall(callDetails)
                            return@withTimeout
                        }

                        if (incomingNumber.isNullOrBlank()) {
                            Log.d(
                                "MyCallScreeningService",
                                "Incoming Call = number = $incomingNumber Blocking call"
                            )
                            blockCall(callDetails) // it is null for private numbers with hidden caller id
                        } else {
                            val normalizationResult =
                                phoneNumberNormalizer.normalize(incomingNumber)

                            normalizationResult.fold(
                                onSuccess = { normalizedNumber ->
                                    performScreening(firewallState, normalizedNumber, callDetails)
                                    /*                            val contactName =
                                                                    phoneCallDataSource.findContactName(normalizedNumber)

                                                                Log.d(
                                                                    "MyCallScreeningService",
                                                                    "Incoming Call normalized = $normalizedNumber name = $contactName"
                                                                )
                                                                // The incoming number was successfully parsed. Check against rules.
                                    //                        runBlocking {
                                                                when (firewallState) {
                                                                    FirewallState.OFF -> allowCall(callDetails)

                                                                    FirewallState.ON -> {
                                                                        if (rulesRepository.isBlacklisted(normalizedNumber)) {
                                                                            Log.d("MyCallScreeningService", "blacklisted. blocking...")

                                                                            activityRepository.logBlockActivity(
                                                                                number = normalizedNumber,
                                                                                name = contactName,
                                                                                timestamp = Clock.System.now(),
                                                                                firewallState = FirewallState.ON,
                                                                            )

                                                                            blockCall(callDetails)
                                                                        } else {
                                                                            Log.d(
                                                                                "MyCallScreeningService",
                                                                                "not blacklisted. allowed..."
                                                                            )
                                                                            allowCall(callDetails)
                                                                        }
                                                                    }

                                                                    FirewallState.ZEN -> {
                                                                        if (rulesRepository.isWhitelisted(normalizedNumber)) {
                                                                            Log.d("MyCallScreeningService", "whitelisted. allowed...")
                                                                            allowCall(callDetails)
                                                                        } else {
                                                                            Log.d(
                                                                                "MyCallScreeningService",
                                                                                "not whitelisted. blocking..."
                                                                            )

                                                                            activityRepository.logBlockActivity(
                                                                                number = normalizedNumber,
                                                                                name = contactName,
                                                                                timestamp = Clock.System.now(),
                                                                                firewallState = FirewallState.ZEN,
                                                                            )

                                                                            blockCall(callDetails)
                                                                        }
                                                                    }
                                                                }
                                                            }*/
                                },
                                onFailure = { failure ->
                                    // If the incoming number is invalid or can't be parsed,
                                    // it is always safer to allow the call than to block it incorrectly.
                                    Log.w(
                                        "MyCallScreeningService",
                                        "Could not parse incoming number. Allowing call. Reason: $failure"
                                    )
                                    allowCall(callDetails)
                                }
                            )
                        }


                    }
                }
            } catch (e: TimeoutCancellationException) {
                // If we time out for ANY reason, we MUST allow the call to prevent a stuck state.
                Log.e(
                    "ScreeningService",
                    "Screening timed out! Allowing call to prevent network issues."
                )
                allowCall(callDetails)
            } finally {
                // --- STOP FOREGROUND SERVICE ---
                // This is just as critical. We must stop the service immediately
                // after responding to release the high-priority lock.
                stopSelf()
                Log.d("ScreeningService", "Screening complete. Foreground service stopped.")
            }
        }
    }

    /**
     * Creates the mandatory notification for the foreground service.
     * This notification will be visible for only a fraction of a second and will
     * likely never even be seen by the user, but it is required by the system.
     */
    private fun createNotification(): Notification {
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "Call Screening",
                NotificationManager.IMPORTANCE_LOW // Low importance is key!
            ).apply {
                description = "Active when screening an incoming call."
                enableLights(false)
                enableVibration(false)
            }
            notificationManager.createNotificationChannel(channel)
        }

        return NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle("Screening Call")
            .setContentText("Checking incoming call...")
            .setSmallIcon(R.drawable.shield_24dp)
            .setPriority(NotificationCompat.PRIORITY_MIN)
            .setCategory(Notification.CATEGORY_SERVICE)
            .build()
    }

    private suspend fun performScreening(
        firewallState: FirewallState,
        normalizedNumber: String,
        callDetails: Call.Details
    ) {
        val contactName =
            phoneCallDataSource.findContactName(normalizedNumber)

        Log.d(
            "MyCallScreeningService",
            "Incoming Call normalized = $normalizedNumber name = $contactName"
        )
        // The incoming number was successfully parsed. Check against rules.
        when (firewallState) {
            FirewallState.OFF -> allowCall(callDetails)

            FirewallState.ON -> {
                if (rulesRepository.isBlacklisted(normalizedNumber)) {
                    Log.d("MyCallScreeningService", "blacklisted. blocking...")

                    logAndBlockCall(
                        normalizedNumber = normalizedNumber,
                        contactName = contactName,
                        mode = firewallState,
                        callDetails = callDetails,
                    )
                } else {
                    Log.d(
                        "MyCallScreeningService",
                        "not blacklisted. allowed..."
                    )
                    allowCall(callDetails)
                }
            }

            FirewallState.ZEN -> {
                if (rulesRepository.isWhitelisted(normalizedNumber)) {
                    Log.d("MyCallScreeningService", "whitelisted. allowed...")
                    allowCall(callDetails)
                } else {
                    Log.d(
                        "MyCallScreeningService",
                        "not whitelisted. blocking..."
                    )

                    logAndBlockCall(
                        normalizedNumber = normalizedNumber,
                        contactName = contactName,
                        mode = firewallState,
                        callDetails = callDetails,
                    )
                }
            }
        }

    }

    private suspend fun logAndBlockCall(
        normalizedNumber: String,
        contactName: String?,
        mode: FirewallState,
        callDetails: Call.Details
    ) {
        Log.i("ScreeningService", "Blocking call from $normalizedNumber in mode $mode")
//        val contactName = findContactName(appContext, normalizedNumber)

        activityRepository.logBlockActivity(
            number = normalizedNumber,
            name = contactName,
            timestamp = Clock.System.now(),
            firewallState = FirewallState.ZEN,
        )

        blockCall(callDetails)
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

    private fun blockCall(callDetails: Call.Details) {
        respondToCall(callDetails, blockAndRejectResponse)
    }

    private fun allowCall(callDetails: Call.Details) {
        respondToCall(callDetails, normalResponse)
    }

    // To disallow (but not rejected) and send to voicemail - Soft Block (Still blocks network)
    val blockAndAcceptResponse = CallResponse.Builder()
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

    // Allow, but silent like DND
    val allowAndSilentResponse =
        CallResponse.Builder()
            .setSilenceCall(true)
            .build()

    val normalResponse = CallResponse.Builder()
//                // Sets whether the incoming call should be blocked.
//                .setDisallowCall(false)
//                // Sets whether the incoming call should be rejected as if the user did so manually.
//                .setRejectCall(false)
//                // Sets whether ringing should be silenced for the incoming call.
//                .setSilenceCall(false)
//                // Sets whether the incoming call should not be displayed in the call log.
//                .setSkipCallLog(false)
//                // Sets whether a missed call notification should not be shown for the incoming call.
//                .setSkipNotification(false)
        .build()


}

val CALL_SCREENING_PERMISSIONS = arrayOf(
    Manifest.permission.ANSWER_PHONE_CALLS,
    Manifest.permission.READ_CONTACTS,
    Manifest.permission.READ_CALL_LOG,
    Manifest.permission.WRITE_CALL_LOG,
)
