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
import com.keeghan.reciplan2.databinding.ActivityDirectionsBinding
import com.keeghan.reciplan2.utils.YoutubeLinks.LINKS
import com.keeghan.reciplan2.utils.YoutubeLinks.NOT_FOUND
import kotlin.math.abs


class DirectionsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDirectionsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDirectionsBinding.inflate(layoutInflater)
        val view = binding.root

        val toolbar = view.findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowTitleEnabled(false)

        // Get information of Recipe from Intent
        val directionAddress = intent.getIntExtra(RECIPE_DIRECTION, R.string.default_recipe_txt)
        val imageUrl = intent.getStringExtra(RECIPE_IMAGE)
        val recipeId = intent.getIntExtra(RECIPE_ID, 200)

        binding.btnYoutube.setOnClickListener { setYtLink(recipeId) }


        //Hide video botton on collapsing AppBar
        binding.directionsAppBar.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
            //   val percentage = abs(verticalOffset).toFloat() / appBarLayout!!.totalScrollRange
            if (abs(verticalOffset) >= appBarLayout.totalScrollRange / 2) {
                binding.btnYoutube.visibility = View.GONE
            } else if (verticalOffset == 0) {
                binding.btnYoutube.visibility = View.VISIBLE
            }
        }


        Glide.with(this)
            .load(imageUrl)
            .placeholder(R.drawable.ic_launcher_background)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .centerCrop()
            .into(binding.directionsImage)

        binding.recipeDirectionTxt.text = resources.getStringArray(directionAddress)[1]
        binding.ingredientListTxt.text = resources.getStringArray(directionAddress)[0]
        setContentView(view)
    }

    /*Intent (Recipe Object) ID's receive from other fragment of
    recipes to present*/
    companion object {
        const val RECIPE_NAME = "com.keeghan.reciplan2.DirectionsActivity.recipeName"
        const val RECIPE_DIRECTION = "com.keeghan.reciplan2.DirectionsActivity.recipeDirections"
        const val RECIPE_IMAGE = "com.keeghan.reciplan2.DirectionsActivity.recipeImage"
        const val RECIPE_ID = "com.keeghan.reciplan2.DirectionsActivity.recipeId"
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
            this,
            R.string.recipe_unavailable,
            Toast.LENGTH_SHORT
        ).show()
    }

}