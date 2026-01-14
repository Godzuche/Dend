package com.godzuche.dend.core.data.datastore

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.godzuche.dend.core.data.model.UserPreferencesData
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream

object DenDPreferencesSerializer: Serializer<UserPreferencesData> {
    override val defaultValue: UserPreferencesData
        get() = UserPreferencesData()

    override suspend fun readFrom(input: InputStream): UserPreferencesData {
        return try {
            Json.decodeFromString(
                deserializer = UserPreferencesData.serializer(),
                string = input.readBytes().decodeToString()
            )
        } catch (e: SerializationException) {
//            e.printStackTrace()
//            defaultValue
            throw CorruptionException(
                message = "Unable to read UserPreferences",
                e
            )
        }
    }

    override suspend fun writeTo(
        t: UserPreferencesData,
        output: OutputStream
    ) {
        output.write(
            Json.encodeToString(
                serializer = UserPreferencesData.serializer(),
                value = t,
            ).encodeToByteArray()
        )
    }
}