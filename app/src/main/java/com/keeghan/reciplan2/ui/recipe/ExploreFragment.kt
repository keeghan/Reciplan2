package com.keeghan.reciplan2.ui.recipe

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.keeghan.reciplan2.databinding.FragmentExploreBinding
import com.keeghan.reciplan2.ui.MainViewModel
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
}