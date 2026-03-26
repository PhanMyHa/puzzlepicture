package com.example.picturepuzzle.data.local

import android.content.Context
import java.util.UUID

class UserSessionManager(context: Context) {

    private val prefs = context.getSharedPreferences("user_session", Context.MODE_PRIVATE)

    fun getDeviceId(): String {
        var id = prefs.getString("device_id", null)
        if (id == null) {
            id = UUID.randomUUID().toString()
            prefs.edit().putString("device_id", id).apply()
        }
        return id
    }

    fun setCurrentUserId(userId: String) {
        prefs.edit().putString("current_user_id", userId).apply()
    }

    fun getCurrentUserId(): String? {
        return prefs.getString("current_user_id", null)
    }

    fun clearUserId() {
        prefs.edit().remove("current_user_id").apply()
    }
}