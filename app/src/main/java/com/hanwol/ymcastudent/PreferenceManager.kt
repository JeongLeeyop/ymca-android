package com.hanwol.ymcastudent

import android.content.Context
import android.content.SharedPreferences

class PreferenceManager(context: Context) {
    private val prefsFilename = "thek_prefs"
    private val prefsAutoLogin = "auto_login"
    private val prefsUserName = "user_name"
    private val prefsUserPhone = "user_phone"
    private val prefs: SharedPreferences = context.getSharedPreferences(prefsFilename, 0)

    var autoLogin: Boolean?
        get() = prefs.getBoolean(prefsAutoLogin, false)
        set(value) = prefs.edit().putBoolean(prefsAutoLogin, value!!).apply()
    var userName: String?
        get() = prefs.getString(prefsUserName, "")
        set(value) = prefs.edit().putString(prefsUserName, value).apply()
    var userPhone: String?
        get() = prefs.getString(prefsUserPhone, "")
        set(value) = prefs.edit().putString(prefsUserPhone, value).apply()
}