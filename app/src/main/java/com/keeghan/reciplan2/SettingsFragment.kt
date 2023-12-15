package com.keeghan.reciplan2

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.text.LineBreaker
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.keeghan.reciplan2.database.Recipe
import com.keeghan.reciplan2.databinding.DeveloperInfoBinding
import com.keeghan.reciplan2.databinding.ExportDialogBinding
import com.keeghan.reciplan2.databinding.FragmentSettingsBinding
import com.keeghan.reciplan2.utils.PreferenceConstants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerializationException
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader


class SettingsFragment : Fragment(), OnClickListener {
    private lateinit var viewModel: SettingsViewModel
    private val binding by lazy { FragmentSettingsBinding.inflate(layoutInflater) }
    private val prefs: SharedPreferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(requireContext())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[SettingsViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        //fill info in settings screen
        binding.apply {
            BuildInfoSub.text =
                getString(R.string.version_info, BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE)
            prefThemeSwitch.isChecked = prefs.getBoolean(PreferenceConstants.PREF_THEME, true)
            prefHapticsSwitch.isChecked = prefs.getBoolean(PreferenceConstants.PREF_HAPTICS, true)
            themePref.text =
                if (prefThemeSwitch.isChecked) getString(R.string.disable_darkmode) else getString(R.string.enable_dark_mode)
            hapticsPref.text =
                if (prefHapticsSwitch.isChecked) getString(R.string.disable_haptics) else getString(
                    R.string.enable_haptics
                )
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.backBtn.setOnClickListener {
            findNavController().popBackStack()
        }
        //Theme change
        binding.prefThemeSwitch.setOnCheckedChangeListener { _, state ->
            prefs.edit().putBoolean(PreferenceConstants.PREF_THEME, state).apply()
            if (state) AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            else AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
        //Haptics change
        binding.prefHapticsSwitch.setOnCheckedChangeListener { _, state ->
            prefs.edit().putBoolean(PreferenceConstants.PREF_HAPTICS, state).apply()
            binding.hapticsPref.text =
                if (state) getString(R.string.disable_haptics) else getString(R.string.enable_haptics)
        }
        viewModel.errorMsg.observe(viewLifecycleOwner) { if (it.isNotBlank()) showToast(it) }

        //set onClickListeners
        binding.DeveloperInfo.setOnClickListener(this)
        binding.DeveloperInfoSub.setOnClickListener(this)
        binding.contactDeveloper.setOnClickListener(this)
        binding.contactDeveloperSub.setOnClickListener(this)
        binding.copyRightDisclaimer.setOnClickListener(this)
        binding.copyRightDisclaimerSub.setOnClickListener(this)
        binding.ExportPref.setOnClickListener(this)
        binding.ExportPrefSub.setOnClickListener(this)
        binding.ImportPref.setOnClickListener(this)
        binding.ImportPrefSub.setOnClickListener(this)
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
            binding.ExportPref -> if (checkStoragePermissions()) exportRecipes() else requestForStoragePermissions()
            binding.ExportPrefSub -> if (checkStoragePermissions()) exportRecipes() else requestForStoragePermissions()
            binding.ImportPref -> if (checkStoragePermissions()) importRecipeFile() else requestForStoragePermissions()
            binding.ImportPrefSub -> if (checkStoragePermissions()) importRecipeFile() else requestForStoragePermissions()
            else -> throw IllegalStateException("Button Click doesn't have onclick set")
        }
    }

    private fun checkStoragePermissions(): Boolean {
        return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            val write = ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            val read = ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE
            )
            read == PackageManager.PERMISSION_GRANTED && write == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    private val STORAGE_PERMISSION_CODE = 23

    private fun requestForStoragePermissions() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            activity?.requestPermissions(
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                ), STORAGE_PERMISSION_CODE
            )
        }
    }

    //Export Recipe to list
    private fun exportRecipes() {
        lifecycleScope.launch {
            try {
                val recipes = withContext(Dispatchers.IO) { viewModel.getAllUserCreatedRecipes() }
                if (recipes.isNotEmpty()) {
                    val json = Json { prettyPrint = true }
                    val jsonString =
                        json.encodeToString(ListSerializer(Recipe.serializer()), recipes)

                    val binding = ExportDialogBinding.inflate(LayoutInflater.from(context))
                    MaterialAlertDialogBuilder(requireContext()).apply {
                        setView(binding.root)
                        setPositiveButton("Save") { _, _ ->
                            val fileName = binding.exportFileText.text.toString()
                            viewModel.writeToFile(fileName, jsonString)
                        }
                        setNegativeButton("Cancel", null)
                    }.show()
                } else showToast("No local recipes to export")
            } catch (e: Exception) {
                showToast("Error fetching recipes: ${e.message}")
            }
        }
        viewModel.resetErrorMessage()
    }

    private fun importRecipeFile() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) "*/*" else "application/json"
        }
        importFileLauncher.launch(intent)
    }

    private val importFileLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.data?.also { uri ->
                    val extension = MimeTypeMap.getFileExtensionFromUrl(uri.path)
                    if (extension.equals("json", true)) {
                        try {
                            requireContext().contentResolver.openInputStream(uri)
                                ?.use { inputStream ->
                                    BufferedReader(InputStreamReader(inputStream)).use { reader ->
                                        val recipes = jsonStringToRecipes(reader.readText())
                                        if (!recipes.isNullOrEmpty()) {
                                            viewModel.importRecipes(recipes)
                                        }
                                    }
                                }
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    } else showToast("File selected not a json file")
                }
            }
        }

    private fun jsonStringToRecipes(jsonString: String): List<Recipe>? {
        return try {
            Json.decodeFromString<List<Recipe>>(jsonString)
        } catch (e: SerializationException) {
            showToast(e.localizedMessage!!)
            null
        }
    }

    private fun showDevInfo() {
        // Assuming the generated binding class for your layout is DeveloperInfoBinding
        val binding = DeveloperInfoBinding.inflate(LayoutInflater.from(context))
        binding.developerProfile.apply {
            setText(R.string.link_developer_profile)
            movementMethod = LinkMovementMethod.getInstance()
        }
        MaterialAlertDialogBuilder(requireContext(), R.style.RoundedAlertDialog)
            .setView(binding.root)
            .setNegativeButton(R.string.close) { dialog, _ -> dialog.dismiss() }.show()
    }

    private fun contactDev() {
        Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf("Eghan20@gmail.com"))
            putExtra(Intent.EXTRA_SUBJECT, "Reciplan App")
        }.also { startActivity(it) }
    }

    private fun showCopyRightDisclaimer() {
        val textView = TextView(context)
        textView.text = getString(R.string.copyright_disclaimer)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            textView.justificationMode = LineBreaker.JUSTIFICATION_MODE_INTER_WORD
        }
        MaterialAlertDialogBuilder(requireContext(), R.style.RoundedAlertDialog)
            .setView(textView)
            .setNegativeButton(getString(R.string.close)) { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun showToast(msg: String) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
    }


}