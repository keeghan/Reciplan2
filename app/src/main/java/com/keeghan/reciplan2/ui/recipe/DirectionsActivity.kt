package com.keeghan.reciplan2.ui.recipe

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.appbar.AppBarLayout
import com.keeghan.reciplan2.R
import com.keeghan.reciplan2.databinding.ActivityDirectionsBinding
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
        supportActionBar!!.setDisplayShowTitleEnabled(false);

        // val title = intent.getStringExtra(RECIPE_NAME)
        val directionAddress = intent.getIntExtra(RECIPE_DIRECTION, R.string.default_recipe_txt)
        val imageUrl = intent.getStringExtra(RECIPE_IMAGE)

        binding.btnYoutube.setMagicButtonClickListener {
            Toast.makeText(this, "button clicked", Toast.LENGTH_SHORT).show()
        }

        binding.directionsAppBar.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
            val percentage = abs(verticalOffset).toFloat() / appBarLayout!!.totalScrollRange
            if (abs(verticalOffset) == appBarLayout.totalScrollRange) {
                binding.btnYoutube.visibility = View.GONE
            } else if (verticalOffset == 0) {
                binding.btnYoutube.visibility = View.VISIBLE
            } else {
              TODO("Implement Transparency")
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


    companion object {
        const val RECIPE_NAME = "com.keeghan.reciplan2.DirectionsActivity.recipeName"
        const val RECIPE_DIRECTION = "com.keeghan.reciplan2.DirectionsActivity.recipeDirections"
        const val RECIPE_IMAGE = "com.keeghan.reciplan2.DirectionsActivity.recipeImage"
    }
}