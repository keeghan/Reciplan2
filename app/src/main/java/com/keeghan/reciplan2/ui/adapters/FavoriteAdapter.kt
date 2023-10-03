package com.keeghan.reciplan2.ui.adapters

import android.content.Context
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

class FavoriteAdapter(var context: Context?) :
    RecyclerView.Adapter<FavoriteAdapter.RecipeHolder>() {
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
            .inflate(R.layout.favorite_explorer_item, parent, false)
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

        //Glide Implementation
        Glide.with(context!!)
            .load(currentRecipe.imageUrl)
            .placeholder(R.drawable.ic_launcher_background)
            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
            .centerCrop()
            .into(holder.recipeImg)
    }

    //The separate ViewHolders
    class RecipeHolder(itemView: View, listener: ButtonClickListener?) :
        RecyclerView.ViewHolder(itemView) {
        val btnFavorite: Button = itemView.findViewById(R.id.btn_favorite_favorites)
        val textName: TextView = itemView.findViewById(R.id.collection_recipe_name)
        val recipeImg: ImageView = itemView.findViewById(R.id.collection_recipe_image)
       // var directions: TextView = itemView.findViewById(R.id.collection_view_directions)

        init {
            val directions =
                itemView.findViewById<View>(R.id.collection_view_directions) as TextView
            btnFavorite.setOnClickListener {
                if (listener != null) {
                    val position = absoluteAdapterPosition
                    listener.doFavoriteOperation(position)
                }
            }
            directions.setOnClickListener {
                if (listener != null) {
                    val position = absoluteAdapterPosition
                    listener.onDirectionsClick(position)
                }
            }
        }
    }

    fun getRecipeAt(position: Int): Recipe {
        return recipes[position]
    }

    fun setRecipes(recipes: List<Recipe>) {
        this.recipes = recipes
        notifyDataSetChanged()
    }

    fun getRecipes(): List<Recipe> {
        return recipes
    }

    override fun getItemCount(): Int {
        return recipes.size
    }
}
