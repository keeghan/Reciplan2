package com.keeghan.reciplan2.ui.recipe

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.keeghan.reciplan2.databinding.FragmentExploreBinding

class ExploreFragment : Fragment() {

    private var _binding: FragmentExploreBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        _binding = FragmentExploreBinding.inflate(inflater, container, false)
        val view = binding.root

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

        return view
    }


}