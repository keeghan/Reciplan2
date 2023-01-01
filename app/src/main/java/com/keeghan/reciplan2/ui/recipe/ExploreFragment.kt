package com.keeghan.reciplan2.ui.recipe

import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.text.Layout.JUSTIFICATION_MODE_INTER_WORD
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import com.keeghan.reciplan2.Constants
import com.keeghan.reciplan2.R
import com.keeghan.reciplan2.databinding.FragmentExploreBinding
import com.keeghan.reciplan2.ui.MainViewModel

class ExploreFragment : Fragment() {

    private var _binding: FragmentExploreBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: MainViewModel
    private lateinit var prefs: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExploreBinding.inflate(inflater, container, false)
        val view = binding.root
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        prefs = PreferenceManager.getDefaultSharedPreferences(requireContext())

        val intent = Intent(context, ChooseRecipeActivity::class.java)

        binding.snackCardView.setOnClickListener {
            intent.putExtra(ChooseRecipeActivity.RECIPETYPE, "snack")
            startActivity(intent)
        }

        binding.breakfastCardView.setOnClickListener {
            intent.putExtra(ChooseRecipeActivity.RECIPETYPE, "breakfast")
            startActivity(intent)
        }

        binding.lunchCardView.setOnClickListener {
            intent.putExtra(ChooseRecipeActivity.RECIPETYPE, "lunch")
            startActivity(intent)
        }

        binding.dinnerCardView.setOnClickListener {
            intent.putExtra(ChooseRecipeActivity.RECIPETYPE, "dinner")
            startActivity(intent)
        }

        //updateVersion2()
        showWelcomeDialog()

        return view
    }

    private fun showWelcomeDialog() {

        if (prefs.getBoolean(Constants.IS_FIRST_RUN, true)) {

            val builder = AlertDialog.Builder(requireContext())
            val v: View = LayoutInflater.from(context).inflate(R.layout.welcome_dialog, null, false)

            val textView = v.findViewById<TextView>(R.id.welcome_txt)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                textView.justificationMode = JUSTIFICATION_MODE_INTER_WORD
            }
            builder.setView(v)
            builder.setNegativeButton(R.string.str_close_welcome_dialog) { dialog, _ ->
                prefs.edit().putBoolean(Constants.IS_FIRST_RUN, false).apply();
                dialog?.dismiss()
            }
            builder.show()
        }
    }

    //Viable update path for new recipes to be added based on whether app is updated
    //TODO: IF UPDATE IS NECESSARY IN LATER VERSIONS
//    private fun updateVersion2() {
//        if (prefs.getBoolean(Constants.IS_VERSION_TWO_UPDATE, true)) {
//            if (BuildConfig.VERSION_CODE > 1) {
//                //version 1 update
//                viewModel.insertRecipe(
//                    Recipe(
//                        24, "paano shew", R.array.kontomire_stew_array, 11, 45,
//                        "https://firebasestorage.googleapis.com/v0/b/firesignindemo.appspot.com/o/recipeimages%2Fkontomire.webp?alt=media&token=06b881c0-9c1f-4e7f-b574-cfd9465a3620",
//                        collection = true, favorite = false, type = "dinner"
//                    )
//                )
//            }
//            prefs.edit().putBoolean(Constants.IS_VERSION_TWO_UPDATE, false).apply();
//        }
//    }
}