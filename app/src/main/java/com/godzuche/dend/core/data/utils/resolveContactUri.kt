package com.godzuche.dend.core.data.utils

import android.content.Context
import android.net.Uri
import android.provider.ContactsContract
import android.util.Log
import com.godzuche.dend.core.domain.model.ContactDetails

/**
 * A utility extension function on Context to resolve a contact URI from the ContactsContract
 * into a name and phone number.
 *
 * @param contactUri The URI returned by the contact picker intent.
 * @return A [ContactDetails] object containing the name and number, or null if the contact
 *         could not be resolved or does not have a phone number.
 */
fun Context.resolveContactUri(contactUri: Uri): ContactDetails? {
    // Define the columns we want to retrieve.
    // We are interested in the contact's display name and their phone number.
    val projection: Array<String> = arrayOf(
        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
        ContactsContract.CommonDataKinds.Phone.NUMBER
    )

    // Query the ContentResolver. The 'use' block ensures the cursor is automatically closed.
    contentResolver.query(contactUri, projection, null, null, null)?.use { cursor ->

        // Check if the cursor is valid and has at least one row.
        if (cursor.moveToFirst()) {
            // Get the column indices for our desired data.
            val nameIndex =
                cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
            val numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)

            // Check if columns were found
            if (nameIndex == -1 || numberIndex == -1) {
                Log.e("ContactResolver", "Required columns not found in cursor.")
                return null
            }

            // Extract the data from the cursor.
            val name = cursor.getString(nameIndex)
            val number = cursor.getString(numberIndex)

            // Return the data in our custom data class.
            return ContactDetails(name = name, phoneNumber = number)
        }
    }

    // If the cursor was null or empty, return null.
    Log.w("ContactResolver", "Could not resolve contact URI: $contactUri")
    return null
}
