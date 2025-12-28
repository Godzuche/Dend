package com.godzuche.dend.core.data.utils

import android.content.Context
import android.telephony.TelephonyManager
import android.util.Log
import com.google.i18n.phonenumbers.NumberParseException
import com.google.i18n.phonenumbers.PhoneNumberUtil

/**
 * A sealed class representing the different reasons why phone number normalization can fail.
 * Using a sealed class provides more context than a simple null or a generic exception.
 */
sealed class NormalizationFailure : Throwable() {
    data object BlankNumber : NormalizationFailure()
    data class InvalidNumber(val reason: String) : NormalizationFailure()
}

/**
 * A robust phone number normalization utility using Google's libphonenumber.
 * @param context The application context to get the TelephonyManager.
 */
class PhoneNumberNormalizer(
    private val context: Context,
) {

    private val phoneUtil: PhoneNumberUtil = PhoneNumberUtil.getInstance()

    /**
     * Normalizes a phone number to the E.164 standard format (e.g., +2348012345678).
     * It uses the device's current country as the context for parsing local numbers.
     *
     * @param number The raw phone number string to normalize.
     * @return The normalized number in E.164 format, or null if parsing fails.
     */
    fun normalize(number: String): /*String?*/ Result<String> {
        if (number.isBlank()) {
//            return null
            return Result.failure(NormalizationFailure.BlankNumber)
        }

        // Get the user's current country code (e.g., "US", "NG", "GB"). This is crucial.
        val defaultRegion = getDeviceCountryCode()

        return try {
            val phoneNumber = phoneUtil.parse(
                number,
                defaultRegion,
            )

            if (phoneUtil.isPossibleNumber(phoneNumber)) {
                // Format the number into the standard E.164 format.
                val formattedNumber = phoneUtil.format(
                    phoneNumber,
                    PhoneNumberUtil.PhoneNumberFormat.E164,
                )
//                formattedNumber
                Result.success(formattedNumber)
            } else {
//                null
                Result.failure(
                    NormalizationFailure.InvalidNumber(
                        "The number is not considered possible."
                    )
                )
            }
        } catch (e: NumberParseException) {
            Log.e(
                "PhoneNumberNormalizer",
                "Failed to parse number '$number' with region '$defaultRegion'",
                e
            )
//            null
            Result.failure(
                NormalizationFailure.InvalidNumber(
                    e.message ?: "Unknown parsing error"
                )
            )
        }
    }

    /**
     * Retrieves the device's current country ISO code from the TelephonyManager.
     * Falls back to the device's locale if the telephony service is unavailable.
     */
    private fun getDeviceCountryCode(): String {
        return try {
            val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            // The network country is often more reliable than the SIM country.
            val countryCode = telephonyManager.networkCountryIso?.uppercase()

            if (countryCode.isNullOrBlank()) {
                // Fallback to the device's locale setting
                context.resources.configuration.locales.get(0).country.uppercase()
            } else {
                countryCode
            }
        } catch (e: Exception) {
            Log.w("PhoneNumberNormalizer", "Could not get country code from TelephonyManager. Falling back to locale.", e)
            context.resources.configuration.locales.get(0).country.uppercase()
        }
    }
}

