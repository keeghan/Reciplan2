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
import com.keeghan.reciplan2.R
import com.keeghan.reciplan2.database.Recipe
import java.util.*

class PlanRecyclerAdapter(var context: Context?) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var recipes: List<Recipe> = ArrayList<Recipe>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == 1) {
            val itemView: View = LayoutInflater.from(parent.context).inflate(R.layout.recipe_plan_item, parent, false)
            PlanItemHolder(itemView)
        } else {
            val itemView: View =
                LayoutInflater.from(parent.context).inflate(R.layout.recipe_plan_default_item, parent, false)
            PlanDefaultHolder(itemView)
        }
    }

    //Multiple layouts to show default recipe
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentRecipe: Recipe = recipes[position]
        if (recipes[position]._id == 0 || recipes[position]._id == 1 || recipes[position]._id == 2) {
            val holderDefault = holder as PlanDefaultHolder
            when (currentRecipe.name) {
                "missing0" -> holderDefault.defaultText.text = context?.getString(R.string.not_set_breakfast)
                "missing1" -> holderDefault.defaultText.text = context?.getString(R.string.not_set_lunch)
                "missing2" -> holderDefault.defaultText.text = context?.getString(R.string.not_set_dinner)
                else -> holderDefault.defaultText.text = context?.getString(R.string.not_set_snack)

            }
        } else {
            val holderItem = holder as PlanItemHolder
            holderItem.recipeName.text = currentRecipe.name
            //Convert MealType to Sentence case
            when (currentRecipe.type) {
                "breakfast" -> holderItem.mealType.setText(R.string.str_breakfast)
                "lunch" -> holderItem.mealType.setText(R.string.str_lunch)
                "dinner" -> holderItem.mealType.setText(R.string.str_dinner)
            }

            //Glide Implementation
            if (context != null) {
                Glide.with(context!!)
                    .load(currentRecipe.imageUrl)
                    .into(holderItem.planImage)
            }
        }
    }


    internal class PlanItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mealType: TextView = itemView.findViewById(R.id.plan_item_meal_type)
        val recipeName: TextView = itemView.findViewById(R.id.plan_item_recipe_name)
        val planImage: ImageView = itemView.findViewById(R.id.plan_item_image)
    }

    internal class PlanDefaultHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val defaultText: TextView = itemView.findViewById(R.id.default_item_text)
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

    override fun getItemViewType(position: Int): Int {
        return if (recipes[position]._id == 0 || recipes[position]._id == 1 || recipes[position]._id == 2) {
            0
        } else {
            1
        }
    }
}
