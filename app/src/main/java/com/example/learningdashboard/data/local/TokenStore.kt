package com.example.learningdashboard.data.local

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

/**
 * Persists the session token in EncryptedSharedPreferences, backed by an Android Keystore master key.
 * The token never touches plain SharedPreferences, logs or the database.
 */
class TokenStore(context: Context) {

    private val prefs: SharedPreferences by lazy {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
        EncryptedSharedPreferences.create(
            context,
            PREFS_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
        )
    }

    fun read(): String? = prefs.getString(KEY_TOKEN, null)

    fun write(token: String) = prefs.edit { putString(KEY_TOKEN, token) }

    fun clear() = prefs.edit { remove(KEY_TOKEN) }

    private companion object {
        const val PREFS_NAME = "auth_session"
        const val KEY_TOKEN = "token"
    }
}
