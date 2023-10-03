package com.keeghan.reciplan2.ui.adapters

import android.annotation.SuppressLint
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

class SetPlanRecyclerAdapter(var context: Context?, private val bListener: ButtonClickListener?) :
    RecyclerView.Adapter<SetPlanRecyclerAdapter.VH>() {
    private var recipes: List<Recipe> = ArrayList<Recipe>()
    var selectedPosition = -1 //implement singeChoice

    //getAdapter position passed to implementations
    interface ButtonClickListener {
        fun onAssignClick(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val itemView: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.set_plan_item, parent, false)
        return VH(itemView)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val currentRecipe: Recipe = recipes[position]
        if (selectedPosition == position) {
            holder.btnAssign.setBackgroundResource(R.drawable.ic_baseline_radio_button_checked_24)
        } else {
            holder.btnAssign.setBackgroundResource(R.drawable.ic_twotone_radio_button_unchecked_24)
        }
        holder.recipeName.text = currentRecipe.name

        //Glide Implementation
        if (context != null) {
            Glide.with(context!!)
                .load(currentRecipe.imageUrl)
                .placeholder(R.drawable.ic_launcher_background)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .into(holder.recipeImage)
        }
    }

    /*ViewHolder Class*/
    inner class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val recipeName: TextView = itemView.findViewById(R.id.set_plan_item_recipe_name)
        val recipeImage: ImageView = itemView.findViewById(R.id.set_plan_item_image)
        val btnAssign: Button = itemView.findViewById(R.id.btn_set)

        init {
            //view passed to viewHolder using adapter
            itemView.setOnClickListener { bListener?.onAssignClick(absoluteAdapterPosition) }
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
