package com.example.todolistinkotlin.preferences

import android.content.Context
import android.content.SharedPreferences

class AppPreferences private constructor(context: Context) {

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFS_NAME = "TodoAppPreferences"

        private const val APP_START_TIMESTAMP = "app_start_timestamp"

        @Volatile
        private var INSTANCE: AppPreferences? = null

        fun getInstance(context: Context): AppPreferences {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: AppPreferences(context).also { INSTANCE = it }
            }
        }
    }

    fun setAppStartTimestamp(value: Long) {
        sharedPreferences.edit().putLong(APP_START_TIMESTAMP, value).apply()
    }

    fun getAppStartTimestamp(): Long {
        return sharedPreferences.getLong(APP_START_TIMESTAMP, 0)
    }

}
