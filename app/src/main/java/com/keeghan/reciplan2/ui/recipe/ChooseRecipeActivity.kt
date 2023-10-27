package com.keeghan.reciplan2.ui.recipe

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.keeghan.reciplan2.R
import com.keeghan.reciplan2.database.Recipe
import com.keeghan.reciplan2.databinding.ActivityChooseRecipeBinding
import com.keeghan.reciplan2.ui.MainViewModel
import com.keeghan.reciplan2.ui.adapters.RecipeAdapter
import com.keeghan.reciplan2.ui.adapters.RecipeAdapter.ButtonClickListener
import com.keeghan.reciplan2.utils.Constants
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.net.URLEncoder

class ChooseRecipeActivity : AppCompatActivity() {
    private lateinit var recipeType: String
    private lateinit var binding: ActivityChooseRecipeBinding
    private lateinit var viewModel: MainViewModel
    private val adapter = RecipeAdapter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChooseRecipeBinding.inflate(layoutInflater)
        val view = binding.root
        val toolbar = view.findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        binding.exploreRecycler.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.exploreRecycler.setHasFixedSize(true)

        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        recipeType = intent.getStringExtra(RECIPETYPE).toString()
        getIntentList()

        //Get Recipe clicked, serialize it and send it to [DirectionsActivity]
        adapter.setButtonClickListener(object : ButtonClickListener {
            override fun onDirectionsClick(position: Int) {
                val workingRecipe: Recipe = adapter.getRecipeAt(position)
                encodeRecipeToDirectionsActivity(this@ChooseRecipeActivity, workingRecipe)
            }

            override fun removeFromCollection(position: Int) {
                remove4rmCollection(position)
            }

            override fun addToCollection(position: Int) {
                add2Collection(position)
            }
        })
        binding.exploreRecycler.adapter = adapter

        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT or ItemTouchHelper.LEFT) {
            override fun onMove(
                recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                when (direction) {
                    ItemTouchHelper.RIGHT -> add2Collection(viewHolder.absoluteAdapterPosition)
                    ItemTouchHelper.LEFT -> remove4rmCollection(viewHolder.absoluteAdapterPosition)
                }
                //reset position of card to prevent disappearance
                adapter.notifyItemChanged(viewHolder.absoluteAdapterPosition)
            }
        }).attachToRecyclerView(binding.exploreRecycler)
        showWelcomeToast()
        setContentView(view)
    }


    //function to get the correct list to build recycler
    private fun getIntentList() {
        when (recipeType) {
            "snack" -> viewModel.snackRecipes.observe(this@ChooseRecipeActivity) { recipes ->
                adapter.setRecipes(recipes)
            }

            "breakfast" -> viewModel.breakfastRecipes.observe(this@ChooseRecipeActivity) { recipes ->
                adapter.setRecipes(recipes)
            }

            "lunch" -> viewModel.lunchRecipes.observe(this@ChooseRecipeActivity) { recipes ->
                adapter.setRecipes(recipes)
            }

            "dinner" -> viewModel.dinnerRecipes.observe(this@ChooseRecipeActivity) { recipes ->
                adapter.setRecipes(recipes)
            }
        }
    }

    //Collection methods
    fun remove4rmCollection(position: Int) {
        val removeRecipe = adapter.getRecipeAt(position)
        val collectionStatus: Boolean = removeRecipe.collection
        if (collectionStatus) {
            removeRecipe.collection = false
            removeRecipe.favorite = false //You can't be part of favorite but not collection
            viewModel.updateRecipe(removeRecipe)
            Toast.makeText(
                this@ChooseRecipeActivity, removeRecipe.name + " removed from Collection", Toast.LENGTH_SHORT
            ).show()
            return
        }
        Toast.makeText(
            this@ChooseRecipeActivity, removeRecipe.name + " not part of Collection", Toast.LENGTH_SHORT
        ).show()
    }

    fun add2Collection(position: Int) {
        val workingRecipe = adapter.getRecipeAt(position)
        if (workingRecipe.collection) {
            Toast.makeText(
                this@ChooseRecipeActivity, workingRecipe.name + " already part of Collection", Toast.LENGTH_SHORT
            ).show()
            return
        }
        workingRecipe.collection = true
        viewModel.updateRecipe(workingRecipe)
        Toast.makeText(
            this@ChooseRecipeActivity, workingRecipe.name + " added to Collection", Toast.LENGTH_SHORT
        ).show()
    }


    private fun showWelcomeToast() {
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        if (prefs.getBoolean(Constants.IS_FIRST_RUN_EXPLORE, true)) {
            prefs.edit().putBoolean(Constants.IS_FIRST_RUN_EXPLORE, false).apply()
            Toast.makeText(this@ChooseRecipeActivity, R.string.str_need_internet, Toast.LENGTH_LONG).show()
        }
    }


    companion object {
        const val RECIPETYPE = "com.keeghan.reciplan2.recipetype"


        fun encodeRecipeToDirectionsActivity(context: Context, recipe: Recipe) {
            val sRecipe = URLEncoder.encode(Json.encodeToString(recipe))
            val intent = Intent(context, DirectionsActivity::class.java)
            intent.putExtra(DirectionsActivity.RECIPE_JSON, sRecipe)
            context.startActivity(intent)
        }
    }
}