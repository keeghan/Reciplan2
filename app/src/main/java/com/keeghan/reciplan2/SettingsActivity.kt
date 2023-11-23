package com.keeghan.reciplan2

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Layout
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import androidx.preference.PreferenceManager.OnPreferenceTreeClickListener
import androidx.preference.SwitchPreference
import com.keeghan.reciplan2.utils.PreferenceConstants.PREF_BUILD_VERSION
import com.keeghan.reciplan2.utils.PreferenceConstants.PREF_CONTACT_DEVELOPER
import com.keeghan.reciplan2.utils.PreferenceConstants.PREF_COPYRIGHT_DISCLAIMER
import com.keeghan.reciplan2.utils.PreferenceConstants.PREF_DEVELOPER_INFO
import com.keeghan.reciplan2.utils.PreferenceConstants.PREF_HAPTICS
import com.keeghan.reciplan2.utils.PreferenceConstants.PREF_THEME

class SettingsActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().replace(R.id.settings, SettingsFragment()).commit()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    class SettingsFragment : PreferenceFragmentCompat(), OnPreferenceTreeClickListener {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
            val buildVersionPreference: Preference? = findPreference(PREF_BUILD_VERSION)
            buildVersionPreference?.summary = Build.VERSION.RELEASE

            val themePreference: SwitchPreference? = findPreference(PREF_THEME)
            if (themePreference?.isChecked == true) themePreference.title = getString(R.string.disable_darkmode)
            else themePreference?.title = getString(R.string.enable_dark_mode)

            val vibrationPreference: SwitchPreference? = findPreference(PREF_HAPTICS)
            if (vibrationPreference?.isChecked == true) vibrationPreference.title = getString(R.string.disable_haptics)
            else vibrationPreference?.title = getString(R.string.enable_haptics)
        }

        override fun onPreferenceTreeClick(preference: Preference): Boolean {
            when (preference.key) {
                PREF_CONTACT_DEVELOPER -> {
                    val intent = Intent(Intent.ACTION_SENDTO)
                    intent.data = Uri.parse("mailto:")
                    intent.putExtra(Intent.EXTRA_EMAIL, arrayOf("Eghan20@gmail.com"))
                    intent.putExtra(Intent.EXTRA_SUBJECT, "Reciplan App")
                    startActivity(intent)
                    return true
                }

                PREF_THEME -> if (preference.sharedPreferences!!.getBoolean(PREF_THEME, true)) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    this.startActivity(requireActivity().intent)
                    requireActivity().finish()
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                }

                PREF_DEVELOPER_INFO -> {
                    val builder = AlertDialog.Builder(
                        requireActivity()
                    )
                    val v: View = LayoutInflater.from(context).inflate(R.layout.developer_info, null, false)
                    val textView2 = v.findViewById<TextView>(R.id.developer_profile)
                    textView2.setText(R.string.link_developer_profile)
                    textView2.movementMethod = LinkMovementMethod.getInstance()
                    builder.setView(v)
                    builder.setNegativeButton(
                        R.string.close
                    ) { dialog, _ -> dialog.dismiss() }
                    builder.show()
                }

                PREF_COPYRIGHT_DISCLAIMER -> {
                    val builder = AlertDialog.Builder(
                        requireActivity()
                    )
                    val v: View =
                        LayoutInflater.from(context).inflate(R.layout.copyright_disclaimer_dialog, null, false)
                    val textView = v.findViewById<TextView>(R.id.disclaimer_txt)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        textView.justificationMode = Layout.JUSTIFICATION_MODE_INTER_WORD

                    }
                    builder.setView(v)
                    builder.setNegativeButton(
                        R.string.close
                    ) { dialog, _ -> dialog.dismiss() }
                    builder.show()
                }
            }
            return false
        }
    }
}