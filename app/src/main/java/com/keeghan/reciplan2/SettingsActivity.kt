package com.keeghan.reciplan2

import android.content.Intent
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
import androidx.preference.PreferenceManager.OnPreferenceTreeClickListener

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().replace(R.id.settings, SettingsFragment())
                .commit()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    class SettingsFragment : PreferenceFragmentCompat(), OnPreferenceTreeClickListener {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
        }

        override fun onPreferenceTreeClick(preference: Preference): Boolean {
            when (preference.key) {
                "pref_contact_developer" -> {
                    val intent = Intent(Intent.ACTION_SENDTO)
                    intent.data = Uri.parse("mailto:")
                    intent.putExtra(Intent.EXTRA_EMAIL, arrayOf("Eghan20@gmail.com"))
                    intent.putExtra(Intent.EXTRA_SUBJECT, "Reciplan App")
                    startActivity(intent)
                    return true
                }
                "pref_theme" -> if (preference.sharedPreferences!!.getBoolean("pref_theme", true)) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    this.startActivity(requireActivity().intent)
                    requireActivity().finish()
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                }
                "pref_developer_info" -> {
                    val builder = AlertDialog.Builder(
                        requireActivity()
                    )
                    val v: View =
                        LayoutInflater.from(context).inflate(R.layout.developer_info, null, false)
                    val textView2 = v.findViewById<TextView>(R.id.developer_profile)
                    textView2.setText(R.string.link_developer_profile)
                    textView2.movementMethod = LinkMovementMethod.getInstance()
                    builder.setView(v)
                    builder.setNegativeButton(
                        R.string.close
                    ) { dialog, _ -> dialog.dismiss() }
                    builder.show()
                }
                "pref_copyright_disclaimer" -> {
                    val builder = AlertDialog.Builder(
                        requireActivity()
                    )
                    val v: View = LayoutInflater.from(context)
                        .inflate(R.layout.copyright_disclaimer_dialog, null, false)
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