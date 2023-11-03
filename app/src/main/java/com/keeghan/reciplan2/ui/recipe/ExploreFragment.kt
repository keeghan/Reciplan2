package com.keeghan.reciplan2.ui.recipe

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.text.LineBreaker
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import com.keeghan.reciplan2.R
import com.keeghan.reciplan2.databinding.FragmentExploreBinding
import com.keeghan.reciplan2.ui.MainViewModel
import com.keeghan.reciplan2.utils.Constants

class ExploreFragment : Fragment() {

    private var _binding: FragmentExploreBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExploreBinding.inflate(inflater, container, false)
        val view = binding.root
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]


        binding.snackCardView.setOnClickListener { startChooseRecipeActivity("snack") }
        binding.breakfastCardView.setOnClickListener { startChooseRecipeActivity("breakfast") }
        binding.lunchCardView.setOnClickListener { startChooseRecipeActivity("lunch") }
        binding.dinnerCardView.setOnClickListener { startChooseRecipeActivity("dinner") }

        //updateVersion2()
        return view
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    //Create Intent and send string to ChooseRecipeActivity with the type of meals to display
    private fun startChooseRecipeActivity(recipeType: String) {
        val intent = Intent(context, ChooseRecipeActivity::class.java)
        intent.putExtra(ChooseRecipeActivity.RECIPETYPE, recipeType)
        startActivity(intent)
    }

    //Viable update path for new recipes to be added based on whether app is updated
    //TODO: IF UPDATE IS NECESSARY IN LATER VERSIONS
//    private fun updateVersion2() {
//        if (prefs.getBoolean(Constants.IS_VERSION_TWO_UPDATE, true)) {
//            if (BuildConfig.VERSION_CODE > 1) {
//                //version 1 update
//                viewModel.insertRecipe(
//                    Recipe(
//                        24, "Paano shew", R.array.kontomire_stew_array, 11, 45,
//                        "https://firebasestorage.googleapis.com/v0/b/firesignindemo.appspot.com/o/recipeimages%2Fkontomire.webp?alt=media&token=06b881c0-9c1f-4e7f-b574-cfd9465a3620",
//                        collection = true, favorite = false, type = "dinner"
//                    )
//                )
//            }
//            prefs.edit().putBoolean(Constants.IS_VERSION_TWO_UPDATE, false).apply();
//        }
//    }
}