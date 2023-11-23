package com.keeghan.reciplan2.ui.recipe

import android.content.SharedPreferences
import android.graphics.text.LineBreaker
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import com.keeghan.reciplan2.R
import com.keeghan.reciplan2.databinding.FragmentExploreBinding
import com.keeghan.reciplan2.databinding.WelcomeDialogBinding
import com.keeghan.reciplan2.ui.MainViewModel
import com.keeghan.reciplan2.utils.Constants
import com.keeghan.reciplan2.utils.Constants.BREAKFAST
import com.keeghan.reciplan2.utils.Constants.DINNER
import com.keeghan.reciplan2.utils.Constants.LUNCH
import com.keeghan.reciplan2.utils.Constants.SNACK

class ExploreFragment : Fragment() {
    private val binding by lazy { FragmentExploreBinding.inflate(layoutInflater) }

    private lateinit var viewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]

        binding.snackCardView.setOnClickListener { navigateToManageCollectionFragment(SNACK) }
        binding.breakfastCardView.setOnClickListener { navigateToManageCollectionFragment(BREAKFAST) }
        binding.lunchCardView.setOnClickListener { navigateToManageCollectionFragment(LUNCH) }
        binding.dinnerCardView.setOnClickListener { navigateToManageCollectionFragment(DINNER) }

        return binding.root
    }


    //Create Intent and send string to ChooseRecipeActivity with the type of meals to display
    private fun navigateToManageCollectionFragment(recipeType: String) {
        //Calling from fragment which host this fragment in a tabLayout to prevent navigation error
        val dd =
            RecipeFragmentDirections.actionNavigationRecipeToManageCollectionFragment(recipeType)
        findNavController().navigate(dd)

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