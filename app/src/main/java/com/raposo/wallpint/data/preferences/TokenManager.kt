package com.raposo.wallpint.data.preferences

import android.content.Context
import android.content.SharedPreferences

class TokenManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

    fun saveToken(token: String) {
        // CAMBIADO a commit() para obligar a guardar al instante
        prefs.edit().putString("jwt_token", token).commit()
    }

    fun getToken(): String? {
        return prefs.getString("jwt_token", null)
    }

    fun clearToken() {
        prefs.edit().clear().commit()
    }

    fun saveRol(rol: String) {
        prefs.edit().putString("rol", rol).commit()
    }

    fun getRol(): String? {
        return prefs.getString("rol", null)
    }

    fun saveNombre(nombre: String) {
        prefs.edit().putString("user_nombre", nombre).commit()
    }

    fun getNombre(): String? {
        return prefs.getString("user_nombre", null)
    }

    fun saveUserId(id: Long) {
        prefs.edit().putLong("user_id", id).commit()
    }

    fun getUserId(): Long {
        return prefs.getLong("user_id", -1L)
    }
}