package com.keeghan.reciplan2.ui.recipe

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.keeghan.reciplan2.R
import com.keeghan.reciplan2.database.Recipe
import com.keeghan.reciplan2.databinding.ActivityDirectionsBinding
import com.keeghan.reciplan2.utils.YoutubeLinks.LINKS
import com.keeghan.reciplan2.utils.YoutubeLinks.NOT_FOUND
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.net.URLDecoder
import java.net.URLEncoder
import kotlin.math.abs


class DirectionsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDirectionsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDirectionsBinding.inflate(layoutInflater)
        val view = binding.root

        // Deserialize Recipe pass from previous activity
        val sRecipe = Json.decodeFromString<Recipe>(URLDecoder.decode(intent.getStringExtra(RECIPE_JSON)))

        val toolbar = view.findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        binding.backBtn.setOnClickListener {
            onBackPressed()
        }

        val directionAddress = sRecipe.direction
        val imageUrl = sRecipe.imageUrl
        val recipeId = sRecipe._id

        //Populate fields based on whether Recipe is created by user or prefilled
        if (sRecipe.userCreated) {
            binding.recipeDirectionTxt.text = sRecipe.userDirection
            binding.ingredientListTxt.text = sRecipe.userIngredient
            // binding.btnYoutube.setOnClickListener { setUserYtLink(sRecipe.YouTubeLInk) }
        } else {
            binding.btnYoutube.setOnClickListener { setYtLink(recipeId) }

            binding.recipeDirectionTxt.text = resources.getStringArray(directionAddress)[1]
            binding.ingredientListTxt.text = resources.getStringArray(directionAddress)[0]
        }
        //end of .isUserCreated check

        //Hide video button on collapsing AppBar
        binding.directionsAppBar.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
            //   val percentage = abs(verticalOffset).toFloat() / appBarLayout!!.totalScrollRange
            if (abs(verticalOffset) >= appBarLayout.totalScrollRange / 2) {
                binding.btnYoutube.visibility = View.GONE
            } else if (verticalOffset == 0) {
                binding.btnYoutube.visibility = View.VISIBLE
            }
        }

        // Load Toolbar Image
        Glide.with(this).load(imageUrl).into(binding.directionsImage)
        setContentView(view)
    }

    /*Intent (Recipe Object) ID's receive from other fragment of
    recipes to present*/
    companion object {
        const val RECIPE_JSON = "com.keeghan.reciplan2.DirectionsActivity.recipeJson"
    }

    /* Method to set intent for youtube button
    * [LINKS] */
    private fun setYtLink(recipeId: Int) {
        var link: String

        LINKS.forEach { entry ->
            if (entry.key == recipeId && entry.value != NOT_FOUND) {
                link = entry.value
                var intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
                intent.setPackage("com.google.android.youtube")
                try {
                    startActivity(intent)
                } catch (ex: ActivityNotFoundException) {
                    intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
                    //Toast.makeText(applicationContext, "Youtube App not Found", Toast.LENGTH_SHORT).show()
                    startActivity(intent)
                }
                return
            }
        }
        Toast.makeText(
            this, R.string.recipe_unavailable, Toast.LENGTH_SHORT
        ).show()
    }

    private fun setUserYtLink(recipeLink: String) {
        if (recipeLink.isNotBlank()) {
            var intent = Intent(Intent.ACTION_VIEW, Uri.parse(recipeLink))
            try {
                startActivity(intent)
            } catch (ex: ActivityNotFoundException) {
                intent = Intent(Intent.ACTION_VIEW, Uri.parse(recipeLink))
                startActivity(intent)
            }
        } else {
            Toast.makeText(
                this, "No link provided for this recipe", Toast.LENGTH_SHORT
            ).show()
        }
    }
}