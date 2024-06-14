package com.example.assa.Data

import android.content.Context
import android.content.SharedPreferences

object SharedPreferencesManager {
    private const val PREF_NAME = "user_prefs"
    private val KEY_FIRST_NAME = "firstname"
    private val KEY_SURNAME = "surname"

    private fun getSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun saveUserNames(context: Context, firstName: String, surname: String) {
        val editor = getSharedPreferences(context).edit()
        editor.putString(KEY_FIRST_NAME, firstName)
        editor.putString(KEY_SURNAME, surname)
        editor.apply()
    }

    fun getFirstName(context: Context): String {
        return getSharedPreferences(context).getString(KEY_FIRST_NAME,"") ?: ""
    }

    fun getSurname(context: Context): String {
        return getSharedPreferences(context).getString(KEY_SURNAME, "") ?: ""
    }
}