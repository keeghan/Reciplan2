package com.keeghan.reciplan2

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.text.LineBreaker
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import com.keeghan.reciplan2.databinding.CopyrightDisclaimerDialogBinding
import com.keeghan.reciplan2.databinding.DeveloperInfoBinding
import com.keeghan.reciplan2.databinding.FragmentSettingsBinding
import com.keeghan.reciplan2.utils.PreferenceConstants

class SettingsFragment : Fragment(), OnClickListener {
    private lateinit var viewModel: SettingsViewModel
    private val binding by lazy { FragmentSettingsBinding.inflate(layoutInflater) }
    private val prefs: SharedPreferences by lazy { PreferenceManager.getDefaultSharedPreferences(requireContext()) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[SettingsViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        //fill info in settings screen
        binding.apply {
            BuildInfoSub.text = Build.VERSION.RELEASE
            prefThemeSwitch.isChecked = prefs.getBoolean(PreferenceConstants.PREF_THEME, true)
            prefHapticsSwitch.isChecked = prefs.getBoolean(PreferenceConstants.PREF_HAPTICS, true)
            themePref.text =
                if (prefThemeSwitch.isChecked) getString(R.string.disable_darkmode) else getString(R.string.enable_dark_mode)
            hapticsPref.text =
                if (prefHapticsSwitch.isChecked) getString(R.string.disable_haptics) else getString(R.string.enable_haptics)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //Theme change
        binding.prefThemeSwitch.setOnCheckedChangeListener { _, state ->
            prefs.edit().putBoolean(PreferenceConstants.PREF_THEME, state).apply()
            if (state) AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            else AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
        //Haptics change
        binding.prefHapticsSwitch.setOnCheckedChangeListener { _, state ->
            prefs.edit().putBoolean(PreferenceConstants.PREF_HAPTICS, state).apply()
        }
        //set onClickListeners
        binding.DeveloperInfo.setOnClickListener(this)
        binding.DeveloperInfoSub.setOnClickListener(this)
        binding.contactDeveloper.setOnClickListener(this)
        binding.contactDeveloperSub.setOnClickListener(this)
        binding.copyRightDisclaimer.setOnClickListener(this)
        binding.copyRightDisclaimerSub.setOnClickListener(this)
        binding.ExportPref.setOnClickListener(this)
    }

    //onButtonClick
    override fun onClick(view: View?) {
        when (view) {
            binding.DeveloperInfo -> showDevInfo()
            binding.DeveloperInfoSub -> showDevInfo()
            binding.contactDeveloper -> contactDev()
            binding.contactDeveloperSub -> contactDev()
            binding.copyRightDisclaimer -> showCopyRightDisclaimer()
            binding.copyRightDisclaimerSub -> showCopyRightDisclaimer()
            binding.ExportPref -> {
                //Todo: Implement Export feature
//                val export = viewModel.getAllUserCreatedRecipes()
//                Toast.makeText(requireContext(), export.size, Toast.LENGTH_LONG).show()
            }

            else -> throw IllegalStateException("Button Click doesn't have onclick set")
        }
    }


    private fun showDevInfo() {
        // Assuming the generated binding class for your layout is DeveloperInfoBinding
        val binding = DeveloperInfoBinding.inflate(LayoutInflater.from(context))
        binding.developerProfile.apply {
            setText(R.string.link_developer_profile)
            movementMethod = LinkMovementMethod.getInstance()
        }
        AlertDialog.Builder(requireActivity()).apply {
            setView(binding.root)
            setNegativeButton(R.string.close) { dialog, _ -> dialog.dismiss() }
        }.show()

    }

    private fun contactDev() {
        Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf("Eghan20@gmail.com"))
            putExtra(Intent.EXTRA_SUBJECT, "Reciplan App")
        }.also { startActivity(it) }
    }

    private fun showCopyRightDisclaimer() {
        // Assuming the generated binding class for your layout is CopyrightDisclaimerDialogBinding
        val binding = CopyrightDisclaimerDialogBinding.inflate(LayoutInflater.from(context))
        binding.disclaimerTxt.apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) justificationMode =
                LineBreaker.JUSTIFICATION_MODE_INTER_WORD
        }
        AlertDialog.Builder(requireActivity()).apply {
            setView(binding.root)
            setNegativeButton(R.string.close) { dialog, _ -> dialog.dismiss() }
        }.show()
    }
}