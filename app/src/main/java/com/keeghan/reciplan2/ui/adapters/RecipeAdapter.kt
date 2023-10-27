package com.keeghan.reciplan2.ui.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.keeghan.reciplan2.R
import com.keeghan.reciplan2.database.Recipe
import java.util.*

class RecipeAdapter(
    var context: Context?
) :
    RecyclerView.Adapter<RecipeAdapter.RecipeHolder>() {
    private var recipes: List<Recipe> = ArrayList<Recipe>()
    private var bListener: ButtonClickListener? = null

    //getAdapter position passed to implementations
    interface ButtonClickListener {
        fun onDirectionsClick(position: Int)
        fun removeFromCollection(position: Int)
        fun addToCollection(position: Int)
    }

    fun setButtonClickListener(listener: ButtonClickListener?) {
        bListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.recipe_explorer_item, parent, false)
        return RecipeHolder(itemView, bListener)
    }

    override fun onBindViewHolder(holder: RecipeHolder, position: Int) {
        val currentRecipe: Recipe = recipes[position]
        holder.textName.text = currentRecipe.name
        holder.textIngredients.text = currentRecipe.ingredients.toString()
        holder.textMin.text = currentRecipe.mins.toString()

        //Glide Implementation
        Glide.with(context!!)
            .load(currentRecipe.imageUrl)
            .into(holder.recipeImg)

    }

    override fun getItemCount(): Int {
        return recipes.size
    }

    fun getRecipeAt(position: Int): Recipe {
        return recipes[position]
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setRecipes(recipes: List<Recipe>) {
        this.recipes = recipes
        notifyDataSetChanged()
    }

    class RecipeHolder(itemView: View, listener: ButtonClickListener?) :
        RecyclerView.ViewHolder(itemView) {
        val textName: TextView = itemView.findViewById(R.id.recipe_name)
        val textMin: TextView = itemView.findViewById(R.id.collection_recipe_min)
        val textIngredients: TextView = itemView.findViewById(R.id.collection_recipe_ingredients)
        val recipeImg: ImageView = itemView.findViewById(R.id.collection_recipe_image)
        private val directions: TextView = itemView.findViewById(R.id.view_directions)

        private val addToCollection: FloatingActionButton =
            itemView.findViewById(R.id.btn_add_to_collection)
        private val removeFromCollection: FloatingActionButton =
            itemView.findViewById(R.id.btn_remove_from_collection)

        init {
            directions.setOnClickListener {
                if (listener != null) {
                    val position = absoluteAdapterPosition
                    listener.onDirectionsClick(position)
                }
            }
            addToCollection.setOnClickListener {
                if (listener != null) {
                    val position = absoluteAdapterPosition
                    listener.addToCollection(position)
                }
            }
            removeFromCollection.setOnClickListener {
                if (listener != null) {
                    val position = absoluteAdapterPosition
                    listener.removeFromCollection(position)
                }
            }
        }
    }
}
