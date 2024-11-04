package com.keeghan.reciplan2

import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.View
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.preference.PreferenceManager
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.keeghan.reciplan2.database.RecipeDatabase
import com.keeghan.reciplan2.utils.PreferenceConstants.PREF_HAPTICS
import org.hamcrest.Matchers.any
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.eq
import org.mockito.Mock
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations


@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)


    @Mock
    private lateinit var mockVibrator: Vibrator

    @Mock
    private lateinit var navController: NavController

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
    }

 //   Todo: fix Test
//    @Test
//    fun testHapticsEnabled() {
//        val prefs = PreferenceManager.getDefaultSharedPreferences(ApplicationProvider.getApplicationContext())
//        prefs.edit().putBoolean(PREF_HAPTICS, true).apply()
//
//        activityRule.scenario.onActivity { activity ->
//            // Mock vibrator service
//            `when`(mockVibrator.hasVibrator()).thenReturn(true)
//
//            // Navigate to trigger haptics
//            navController.navigate(R.id.navigation_plan)
//
//            // Verify vibration was triggered based on API level
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//                // For API level 29 (Android 10) and above
//                verify(mockVibrator).vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_HEAVY_CLICK))
//            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                // For API level 26 to 28 (Android 8.0 to 9.0)
//                verify(mockVibrator).vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
//            } else {// For below API level 26
//                verify(mockVibrator).vibrate(eq(50L))
//            }
//        }
//    }

    @Test
    fun testHapticsDisabled() {
        val prefs = PreferenceManager.getDefaultSharedPreferences(ApplicationProvider.getApplicationContext())
        prefs.edit().putBoolean(PREF_HAPTICS, false).apply()

        activityRule.scenario.onActivity { activity ->
            // Mock vibrator service
            `when`(mockVibrator.hasVibrator()).thenReturn(true)

            // Navigate to verify no haptics
            navController.navigate(R.id.navigation_plan)

            // Verify vibration was not triggered
            verify(mockVibrator, never()).vibrate(50L)
        }
    }

    @Test
    fun testNavigationSetup() {
        val navController = TestNavHostController(ApplicationProvider.getApplicationContext())

        activityRule.scenario.onActivity { activity ->
            // Set up nav controller
            activity.runOnUiThread {
                navController.setGraph(R.navigation.mobile_navigation)
                Navigation.setViewNavController(activity.findViewById(R.id.nav_host_fragment), navController)
            }

            // Verify start destination
            assert(navController.currentDestination?.id == R.id.navigation_recipe)
        }
    }
}