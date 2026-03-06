package com.example.xpressutc

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

object SecurityManager {
    private const val PREFS_FILENAME = "secure_prefs"

    fun getEncryptedPrefs(context: Context) = EncryptedSharedPreferences.create(
        context,
        PREFS_FILENAME,
        MasterKey.Builder(context).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build(),
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun saveAuthToken(context: Context, token: String) {
        getEncryptedPrefs(context).edit().putString("auth_token", token).apply()
    }

    fun getAuthToken(context: Context): String? {
        return getEncryptedPrefs(context).getString("auth_token", null)
    }
}