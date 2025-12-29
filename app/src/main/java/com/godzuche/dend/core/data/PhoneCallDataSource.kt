package com.godzuche.dend.core.data

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.provider.CallLog
import android.provider.ContactsContract
import android.util.Log
import androidx.core.database.getStringOrNull
import com.godzuche.dend.core.domain.model.CallLogItem
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PhoneCallDataSource(
    private val context: Context,
    private val ioDispatcher: CoroutineDispatcher,
    private val scope: CoroutineScope,
) {
    /**
     * Loads the most recent entries from the device's call log.
     * This operation is performed on the IO dispatcher as it involves a ContentResolver query.
     * Permissions MUST be checked by the UI layer before this method is called.
     */
    @SuppressLint("MissingPermission", "Range")
    suspend fun loadCallLog(limit: Int): List<CallLogItem> {
        return scope.async(ioDispatcher) {
            val callLogItems = mutableListOf<CallLogItem>()
            val projection = arrayOf(
                CallLog.Calls.NUMBER,
                CallLog.Calls.DATE,
                CallLog.Calls.CACHED_NAME,
                CallLog.Calls.TYPE
            )
            val sortOrder = "${CallLog.Calls.DATE} DESC" // Most recent calls first

            try {
                context.contentResolver.query(
                    CallLog.Calls.CONTENT_URI,
                    projection,
                    null,
                    null,
                    sortOrder,
                )?.use { cursor ->
                    val numberIndex = cursor.getColumnIndexOrThrow(CallLog.Calls.NUMBER)
                    val dateIndex = cursor.getColumnIndexOrThrow(CallLog.Calls.DATE)
                    val nameIndex = cursor.getColumnIndexOrThrow(CallLog.Calls.CACHED_NAME)
                    val typeIndex = cursor.getColumnIndexOrThrow(CallLog.Calls.TYPE)

                    // Limit to the most recent limit number of calls for performance and relevance
                    while (cursor.moveToNext() && callLogItems.size < limit) {
                        val number = cursor.getStringOrNull(numberIndex)
                        val dateMillis = cursor.getLong(dateIndex)
                        val cachedName = cursor.getStringOrNull(nameIndex)
                        val type = cursor.getInt(typeIndex)

                        val displayName: String? = if (!cachedName.isNullOrBlank()) {
                            cachedName
                        } else {
                            // Perform a manual, real-time lookup.
                            findContactName(number)
                        }

                        val callTypeString = when (type) {
                            CallLog.Calls.INCOMING_TYPE -> "Incoming"
                            CallLog.Calls.OUTGOING_TYPE -> "Outgoing"
                            CallLog.Calls.MISSED_TYPE -> "Missed"
                            CallLog.Calls.REJECTED_TYPE -> "Rejected"
                            CallLog.Calls.BLOCKED_TYPE -> "Blocked"
                            CallLog.Calls.VOICEMAIL_TYPE -> "Voicemail"
                            CallLog.Calls.ANSWERED_EXTERNALLY_TYPE -> "Answered externally"
                            else -> "Unknown"
                        }

//                        val contactName = displayName?.takeIf { it.isNotBlank() }
//                            ?: number?.takeIf { it.isNotBlank() }
//                            ?: "Private number"

                        callLogItems.add(
                            CallLogItem(
                                phoneNumber = number,
                                formattedDate = SimpleDateFormat(
                                    "MMM d, h:mm a",
                                    Locale.getDefault()
                                ).format(Date(dateMillis)),
                                contactName = displayName,
                                callType = callTypeString,
                            )
                        )
                    }
                }
            } catch (e: Exception) {
                // Handle potential exceptions, e.g., SecurityException if permission is revoked mid-operation
                Log.e("RulesViewModel", "Error loading call log", e)
                // could expose an error state to the UI here as well
            }

            callLogItems
        }.await()
    }

    /**
     * A fast, efficient lookup function to find a contact's display name
     * using just their phone number. It uses the indexed PhoneLookup table, which is
     * optimized for this type of query.
     *
     * This function is essential for reliably finding contact names when the
     * CallLog.Calls.CACHED_NAME field is null or empty.
     *
     * @param context The application context, needed to access the ContentResolver.
     * @param phoneNumber The phone number to search for.
     * @return The contact's display name if a match is found, otherwise null.
     */
    @SuppressLint("MissingPermission") // Permissions should be checked by the calling code before invoking this.
    fun findContactName(phoneNumber: String?): String? {
        if (phoneNumber.isNullOrBlank()) {
            return null
        }
        var contactName: String? = null

        // Use the PhoneLookup URI, which is designed for fast number-based lookups.
        // Append the phone number to the filter URI.
        val uri = Uri.withAppendedPath(
            ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
            Uri.encode(phoneNumber)
        )

        // Define the single column we need: the display name.
        val projection = arrayOf(ContactsContract.PhoneLookup.DISPLAY_NAME)

        try {
            // Query the content resolver. The 'use' block ensures the cursor is always closed.
            context.contentResolver.query(uri, projection, null, null, null)?.use { cursor ->
                // If the cursor is not empty and we can move to the first row...
                if (cursor.moveToFirst()) {
                    // Get the index of the display name column.
                    val nameIndex =
                        cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME)
                    if (nameIndex != -1) {
                        // Extract the name string from the cursor.
                        contactName = cursor.getString(nameIndex)
                    }
                }
            }
        } catch (e: Exception) {
            // Log any exceptions that occur during the query.
            Log.e("ContactUtils", "Error finding contact name for $phoneNumber", e)
        }
        return contactName
    }
}