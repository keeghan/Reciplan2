package com.keeghan.reciplan2

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.preference.PreferenceManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.keeghan.reciplan2.database.Recipe
import com.keeghan.reciplan2.ui.recipe.DirectionsActivity
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.net.URLEncoder

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val bottomNavView = findViewById<View>(R.id.nav_view) as BottomNavigationView
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration.Builder(
            R.id.navigation_recipe, R.id.navigation_plan
        ).build()

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        // val navController = Navigation.findNavController(this, R.id.nav_host_fragment)
        NavigationUI.setupWithNavController(bottomNavView, navController)


        /*Dark mode implementation on app startup*/
        //Toolbar set in various individual top level fragments instead

        /*Dark mode implementation on app startup*/
        val prefs = PreferenceManager.getDefaultSharedPreferences(
            applicationContext
        )


        //check user settings for theme
        if (prefs.getBoolean("pref_theme", true)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

    }

    fun encodeRecipeToDirectionsActivity(context: Context, recipe: Recipe) {
        val sRecipe = URLEncoder.encode(Json.encodeToString(recipe))
        val intent = Intent(context, DirectionsActivity::class.java)
        intent.putExtra(DirectionsActivity.RECIPE_JSON, sRecipe)
        startActivity(intent)
    }

}