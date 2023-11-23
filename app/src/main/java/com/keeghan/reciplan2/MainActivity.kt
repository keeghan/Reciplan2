package com.keeghan.reciplan2

import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.preference.PreferenceManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.keeghan.reciplan2.utils.PreferenceConstants.PREF_HAPTICS

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val prefs = PreferenceManager.getDefaultSharedPreferences(applicationContext)

        val bottomNavView = findViewById<View>(R.id.nav_view) as BottomNavigationView

        AppBarConfiguration.Builder(R.id.navigation_recipe, R.id.navigation_plan, R.id.navigation_add).build()

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        NavigationUI.setupWithNavController(bottomNavView, navController)



        navController.addOnDestinationChangedListener { _, destination, _ ->

            if (prefs.getBoolean(PREF_HAPTICS, true)) {
                val vibrator = ContextCompat.getSystemService(this, Vibrator::class.java)
                @Suppress("DEPRECATION") when {
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> {
                        vibrator?.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_HEAVY_CLICK))
                    }

                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> vibrator?.vibrate(
                        VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE)
                    )

                    else -> vibrator?.vibrate(50)
                }
            }

            //ensure correct BottomBar icon is highlighted on each navigation
            when (destination.id) {
                R.id.navigation_recipe -> bottomNavView.menu.findItem(R.id.navigation_recipe).isChecked = true
                R.id.navigation_plan -> bottomNavView.menu.findItem(R.id.navigation_plan).isChecked = true
                R.id.navigation_add -> bottomNavView.menu.findItem(R.id.navigation_add).isChecked = true
            }

            //Hide or Un_hide bottom bar based on BottomBar destinations
            when (destination.id) {
                R.id.navigation_recipe, R.id.navigation_plan, R.id.navigation_add -> bottomNavView.visibility =
                    View.VISIBLE

                else -> bottomNavView.visibility = View.GONE
            }
        }
    }
}