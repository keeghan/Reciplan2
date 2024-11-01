package com.keeghan.reciplan2.ui.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.keeghan.reciplan2.R
import com.keeghan.reciplan2.database.Recipe
import com.keeghan.reciplan2.databinding.RecipePlanDefaultItemBinding
import com.keeghan.reciplan2.databinding.RecipePlanItemBinding
import com.keeghan.reciplan2.utils.Constants.BREAKFAST
import com.keeghan.reciplan2.utils.Constants.DINNER
import com.keeghan.reciplan2.utils.Constants.LUNCH
import com.keeghan.reciplan2.utils.Constants.MISSING_MEAL_PLAN

class PlanRecyclerAdapter(var context: Context?) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var recipes: List<Recipe> = ArrayList()

    //Set onClickListener for when a user clicks on an recipe in plan
    private var planItemClickListener: PlanItemOnClickListener? = null

    interface PlanItemOnClickListener {
        fun onRecipeClick(recipe: Recipe)
    }

    fun setPlanItemClickListener(listener: PlanItemOnClickListener?) {
        planItemClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == 1) {
            val binding = RecipePlanItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            PlanItemHolder(binding, planItemClickListener)
        } else {
            val binding = RecipePlanDefaultItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            PlanDefaultHolder(binding)
        }
    }

    //Multiple layouts to show default recipe
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentRecipe: Recipe = recipes[position]
        if (recipes[position].type == MISSING_MEAL_PLAN) {
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
                BREAKFAST -> holderItem.mealType.setText(R.string.str_breakfast)
                LUNCH -> holderItem.mealType.setText(R.string.str_lunch)
                DINNER -> holderItem.mealType.setText(R.string.str_dinner)
            }

            //Glide Implementation
            if (context != null) {
                Glide.with(context!!)
                    .load(currentRecipe.imageUrl)
                    .into(holderItem.planImage)
            }
        }
    }


    inner class PlanItemHolder(
        val binding: RecipePlanItemBinding,
        planItemOnClickListener: PlanItemOnClickListener?
    ) : RecyclerView.ViewHolder(binding.root) {
        val mealType = binding.planItemMealType
        val recipeName = binding.planItemRecipeName
        val planImage = binding.planItemImage
        //Set onClickListener for planItem
        init {
            binding.planItemContainer.setOnClickListener {
                planItemOnClickListener?.onRecipeClick(recipes[absoluteAdapterPosition]);
            }
        }
    }

    internal class PlanDefaultHolder(val binding: RecipePlanDefaultItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val defaultText = binding.defaultItemText
    }

    //Use function to perform operations on the recipe selected from the planFragment
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

    //check if recipe type is mean not set
    override fun getItemViewType(position: Int): Int {
        return if (recipes[position].type == MISSING_MEAL_PLAN) {
            0
        } else {
            1
        }
    }
}
