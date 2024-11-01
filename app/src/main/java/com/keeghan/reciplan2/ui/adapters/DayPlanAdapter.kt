package com.keeghan.reciplan2.ui.adapters
//
//import android.content.Context
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import androidx.recyclerview.widget.RecyclerView
//import com.bumptech.glide.Glide
//import com.keeghan.reciplan2.R
//import com.keeghan.reciplan2.database.DayWithRecipes
//import com.keeghan.reciplan2.database.Recipe
//import com.keeghan.reciplan2.databinding.PlanDayLayoutBinding
//
//class DayPlanAdapter : RecyclerView.Adapter<DayPlanAdapter.DayViewHolder>() {
//    private var data: List<DayWithRecipes> = emptyList()
//    private var clickListener: DayPlanOnClickListener? = null
//
//    interface DayPlanOnClickListener {
//        fun onEditClick(position: Int)
//        fun onRecipeClick(recipe: Recipe)
//    }
//
//    fun setButtonClickListener(listener: DayPlanOnClickListener?) {
//        clickListener = listener
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayViewHolder {
//        val binding = PlanDayLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
//        return DayViewHolder(binding, clickListener)
//    }
//
//    override fun onBindViewHolder(holder: DayViewHolder, position: Int) {
//        val currentDay = data[position]
//        val binding = holder.binding
//        binding.DayName.text = currentDay.day.name
//
//        if (currentDay.breakfastRecipe.name == "missing0") {
//            binding.breakfastMealType.text = binding.root.context.getText(R.string.not_set_breakfast)
//            binding.breakFastName.visibility = View.GONE
//        } else {
//            binding.breakFastName.text = currentDay.breakfastRecipe.name
//        }
//        if (currentDay.lunchRecipe.name == "missing1") {
//            binding.lunchMealType.text = binding.root.context.getText(R.string.not_set_lunch)
//            binding.lunchName.visibility = View.GONE
//        } else {
//            binding.lunchName.text = currentDay.lunchRecipe.name
//        }
//        if (currentDay.dinnerRecipe.name == "missing2") {
//            binding.dinnerMealType.text = binding.root.context.getText(R.string.not_set_dinner)
//            binding.dinnerName.visibility = View.GONE
//        } else {
//            binding.dinnerName.text = currentDay.dinnerRecipe.name
//        }
//
//
//        //Recipe Images Implementation
//        Glide.with(holder.context).load(currentDay.breakfastRecipe.imageUrl).into(binding.imgBreakfast)
//        Glide.with(holder.context).load(currentDay.lunchRecipe.imageUrl).into(binding.imgLunch)
//        Glide.with(holder.context).load(currentDay.dinnerRecipe.imageUrl).into(binding.imgDinner)
//    }
//
//
//    //ViewHolder representing each day Card
//    inner class DayViewHolder(
//        val binding: PlanDayLayoutBinding, listener: DayPlanOnClickListener?
//    ) : RecyclerView.ViewHolder(binding.root) {
//        val context: Context = binding.root.context
//        private val dayEditBtn = binding.btnDayEdit
//
//        //for accepting clicks on items and directing to DirectionsPage and Editing mealPlan
//        init {
//            dayEditBtn.setOnClickListener {
//                listener?.onEditClick(absoluteAdapterPosition)
//            }
//            binding.layoutBreakfast.setOnClickListener {
//                listener?.onRecipeClick(data[absoluteAdapterPosition].breakfastRecipe)
//            }
//            binding.layoutLunch.setOnClickListener {
//                listener?.onRecipeClick(data[absoluteAdapterPosition].lunchRecipe)
//            }
//            binding.layoutDinner.setOnClickListener {
//                listener?.onRecipeClick(data[absoluteAdapterPosition].dinnerRecipe)
//            }
//        }
//    }
//    //end of viewHolder
//
//
//    override fun getItemCount(): Int = data.size
//
//    fun updateData(newData: List<DayWithRecipes>) {
//        data = newData
//        notifyDataSetChanged()
//    }
//
//}
