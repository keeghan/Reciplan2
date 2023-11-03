package com.keeghan.reciplan2

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.text.LineBreaker
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.preference.PreferenceManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.keeghan.reciplan2.database.Recipe
import com.keeghan.reciplan2.ui.recipe.DirectionsActivity
import com.keeghan.reciplan2.utils.Constants
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.net.URLEncoder

class MainActivity : AppCompatActivity() {
    private lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val bottomNavView = findViewById<View>(R.id.nav_view) as BottomNavigationView
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration.Builder(
            R.id.navigation_recipe, R.id.navigation_plan, R.id.navigation_add
        ).build()

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        // val navController = Navigation.findNavController(this, R.id.nav_host_fragment)
        NavigationUI.setupWithNavController(bottomNavView, navController)
        prefs = PreferenceManager.getDefaultSharedPreferences(this)

        showWelcomeDialog()


        //sent to application class
        /*Dark mode implementation on app startup*/
//        val prefs = PreferenceManager.getDefaultSharedPreferences(
//            applicationContext
//        )
//
//
//        //check user settings for theme
//        if (prefs.getBoolean("pref_theme", true)) {
//            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
//        } else {
//            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
//        }

    }

    //Show One Time Welcome message
    private fun showWelcomeDialog() {
        if (prefs.getBoolean(Constants.IS_FIRST_RUN, true)) {

            val builder = AlertDialog.Builder(this)
            val v: View = LayoutInflater.from(this).inflate(R.layout.welcome_dialog, null, false)

            val textView = v.findViewById<TextView>(R.id.welcome_txt)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                textView.justificationMode = LineBreaker.JUSTIFICATION_MODE_INTER_WORD
            }
            builder.setView(v)
            builder.setNegativeButton(R.string.str_close_welcome_dialog) { dialog, _ ->
                prefs.edit().putBoolean(Constants.IS_FIRST_RUN, false).apply()
                dialog?.dismiss()
            }
            builder.show()
        }
    }
}