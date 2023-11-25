package com.keeghan.reciplan2

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceManager
import com.keeghan.reciplan2.utils.PreferenceConstants.PREF_THEME

class ReciplanApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        //check user settings for theme
        val prefs = PreferenceManager.getDefaultSharedPreferences(applicationContext)

        if (prefs.getBoolean(PREF_THEME, true)) AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        else AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }
}