package com.keeghan.reciplan2.ui.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.keeghan.reciplan2.R
import com.keeghan.reciplan2.database.Recipe
import java.util.*

class CollectionAdapter(var context: Context?) :
    RecyclerView.Adapter<CollectionAdapter.RecipeHolder>() {

    private var recipes: List<Recipe> = ArrayList<Recipe>()
    private var bListener: ButtonClickListener? = null

    //getAdapter position passed to implementations
    interface ButtonClickListener {
        fun onDirectionsClick(position: Int)
        fun doFavoriteOperation(position: Int)
    }

    fun setButtonClickListener(listener: ButtonClickListener?) {
        bListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeHolder {
        val itemView: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.collection_explorer_item, parent, false)
        return RecipeHolder(itemView, bListener)
    }

    override fun onBindViewHolder(holder: RecipeHolder, position: Int) {
        val currentRecipe: Recipe = recipes[position]
        if (!currentRecipe.favorite) {
            holder.btnFavorite.setBackgroundResource(R.drawable.ic_favorite_border)
        } else {
            holder.btnFavorite.setBackgroundResource(R.drawable.ic_favorite)
        }
        holder.textName.text = currentRecipe.name
        holder.textIngredients.text = currentRecipe.ingredients.toString()
        holder.textMin.text = currentRecipe.mins.toString()

        //Glide Implementation
        Glide.with(context!!)
            .load(currentRecipe.imageUrl)
            .placeholder(R.drawable.ic_launcher_background)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .centerCrop()
            .into(holder.recipeImg)


        if(context != null){
            Log.e("context test", "context provided")
        }
    }

    //The separate ViewHolders
    class RecipeHolder(itemView: View, listener: ButtonClickListener?) :
        RecyclerView.ViewHolder(itemView) {
        val textName: TextView = itemView.findViewById(R.id.collection_recipe_name)
        val textMin: TextView = itemView.findViewById(R.id.collection_recipe_min)
        val textIngredients: TextView = itemView.findViewById(R.id.collection_recipe_ingredients)
        val recipeImg: ImageView = itemView.findViewById(R.id.collection_recipe_image)
        var btnFavorite: Button = itemView.findViewById(R.id.btn_favorite_collection)

        init {
            val directions = itemView.findViewById<TextView>(R.id.collection_view_directions)
            btnFavorite.setOnClickListener {
                if (listener != null) {
                    val position = adapterPosition
                    listener.doFavoriteOperation(position)
                }
            }
            directions.setOnClickListener {
                if (listener != null) {
                    val position = adapterPosition
                    listener.onDirectionsClick(position)
                }
            }
        }
    }

    fun getRecipeAt(position: Int): Recipe {
        return recipes[position]
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setRecipes(recipes: List<Recipe>) {
        this.recipes = recipes
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return recipes.size
    }
}
