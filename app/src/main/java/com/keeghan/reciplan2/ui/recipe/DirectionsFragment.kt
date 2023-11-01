package com.keeghan.reciplan2.ui.recipe

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.keeghan.reciplan2.R
import com.keeghan.reciplan2.database.Recipe
import com.keeghan.reciplan2.databinding.FragmentDirectionsBinding
import com.keeghan.reciplan2.utils.YoutubeLinks
import kotlinx.serialization.json.Json
import kotlin.math.abs


class DirectionsFragment : Fragment() {
    private val binding by lazy { FragmentDirectionsBinding.inflate(layoutInflater) }
    private val args: DirectionsFragmentArgs by navArgs()
    private lateinit var recipe: Recipe

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Retrieve and decode recipe from safeArgs
        recipe = Json.decodeFromString(Uri.decode(args.recipe))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val toolbar = binding.toolbar
        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)
        (requireActivity() as AppCompatActivity).supportActionBar?.title = recipe.name

        //Hide video button on collapsing AppBar
        binding.directionsAppBar.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
            //   val percentage = abs(verticalOffset).toFloat() / appBarLayout!!.totalScrollRange
            if (abs(verticalOffset) >= appBarLayout.totalScrollRange / 2) {
                binding.btnYoutube.visibility = View.GONE
            } else if (verticalOffset == 0) {
                binding.btnYoutube.visibility = View.VISIBLE
            }
        }

        //Populate fields based on whether Recipe is created by user or prefilled
        if (recipe.userCreated) {
            binding.recipeDirectionTxt.text = recipe.userDirection
            binding.ingredientListTxt.text = recipe.userIngredient
        } else {
            binding.recipeDirectionTxt.text = resources.getStringArray(recipe.direction)[1]
            binding.ingredientListTxt.text = resources.getStringArray(recipe.direction)[0]
        }
        binding.btnYoutube.setOnClickListener { setYtLink(recipe) }
        binding.backBtn.setOnClickListener {
            it.findNavController().popBackStack()
            //requireActivity().supportFragmentManager.popBackStack()
        }

        // Load Toolbar Image
        Glide.with(this).load(recipe.imageUrl).into(binding.directionsImage)
    }


    // Todo: Add user video link whether youtube or not
    // Method to set intent for youtube button
    // * [LINKS]
    private fun setYtLink(recipe: Recipe) {
        var link: String

        if (recipe.userCreated) {
//            if (recipe.userYTLink.isNotBlank()) {
//                startYouTubeActivity(recipe.userYTLink)
//                return
//            }
        } else {
            YoutubeLinks.LINKS.forEach { entry ->
                if (entry.key == recipe._id && entry.value != YoutubeLinks.NOT_FOUND) {
                    link = entry.value
                    startYouTubeActivity(link)
                    return
                }
            }
        }

        Toast.makeText(
            requireContext(), R.string.recipe_unavailable, Toast.LENGTH_SHORT
        ).show()
    }

    //start youtube app or video app later...
    private fun startYouTubeActivity(recipeLink: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(recipeLink)).apply {
            setPackage("com.google.android.youtube")
        }
        try {
            startActivity(intent)
        } catch (ex: ActivityNotFoundException) {
            startActivity(intent.setPackage(null))
        }
    }

}