package com.godzuche.dend.core.services.callscreening

import android.app.role.RoleManager
import android.content.Intent

/**
 * A helper class to abstract away the logic for checking and requesting the
 * Call Screening role.
 *
 * @param roleManager The system RoleManager instance.
 * @param onLaunchIntent A lambda function that the UI layer will provide. This
 * is the inversion of control.
 */
class RoleRequester(
    private val roleManager: RoleManager,
    private val onLaunchIntent: (Intent) -> Unit,
) {

    /**
     * Checks if the app currently holds the Call Screening role.
     */
    fun isRoleHeld(): Boolean {
        return roleManager.isRoleHeld(RoleManager.ROLE_CALL_SCREENING)
    }

    /**
     * Creates the intent and then invokes the provided lambda to launch it.
     */
    fun requestRole() {
        val intent = roleManager.createRequestRoleIntent(RoleManager.ROLE_CALL_SCREENING)
        onLaunchIntent(intent)
    }
}
