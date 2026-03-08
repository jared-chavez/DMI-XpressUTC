package com.example.xpressutc

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey


object SecurityManager {
    private const val PREFS_FILENAME = "secure_prefs"
    private const val KEY_AUTH_TOKEN = "auth_token"


    fun getEncryptedPrefs(context: Context): EncryptedSharedPreferences {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
        
        return EncryptedSharedPreferences.create(
            context,
            PREFS_FILENAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        ) as EncryptedSharedPreferences
    }


    fun saveAuthToken(context: Context, token: String) {
        getEncryptedPrefs(context).edit().putString(KEY_AUTH_TOKEN, token).apply()
    }


    fun getAuthToken(context: Context): String? {
        return getEncryptedPrefs(context).getString(KEY_AUTH_TOKEN, null)
    }


    fun clearAuthToken(context: Context) {
        getEncryptedPrefs(context).edit().remove(KEY_AUTH_TOKEN).apply()
    }
}
