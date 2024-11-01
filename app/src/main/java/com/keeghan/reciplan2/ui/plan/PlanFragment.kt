/*
* Fragment that represents the main plan page
* It includes seven recyclers grouped under days
* */

package com.keeghan.reciplan2.ui.plan

import android.graphics.Canvas
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.keeghan.reciplan2.R
import com.keeghan.reciplan2.database.Recipe
import com.keeghan.reciplan2.databinding.FragmentPlanBinding
import com.keeghan.reciplan2.ui.adapters.PlanRecyclerAdapter
import com.keeghan.reciplan2.utils.Constants
import com.keeghan.reciplan2.utils.Constants.BREAKFAST
import com.keeghan.reciplan2.utils.Constants.DINNER
import com.keeghan.reciplan2.utils.Constants.FRIDAY_ID
import com.keeghan.reciplan2.utils.Constants.LUNCH
import com.keeghan.reciplan2.utils.Constants.MISSING_MEAL_PLAN
import com.keeghan.reciplan2.utils.Constants.MONDAY_ID
import com.keeghan.reciplan2.utils.Constants.NO_BREAKFAST
import com.keeghan.reciplan2.utils.Constants.NO_DINNER
import com.keeghan.reciplan2.utils.Constants.NO_LUNCH
import com.keeghan.reciplan2.utils.Constants.SATURDAY_ID
import com.keeghan.reciplan2.utils.Constants.SUNDAY_ID
import com.keeghan.reciplan2.utils.Constants.THURSDAY_ID
import com.keeghan.reciplan2.utils.Constants.TUESDAY_ID
import com.keeghan.reciplan2.utils.Constants.WEDNESDAY_ID
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.nio.charset.StandardCharsets
import kotlinx.serialization.encodeToString


class PlanFragment : Fragment(), View.OnClickListener, MenuProvider {

    private var _binding: FragmentPlanBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: PlanViewModel

    private val days = arrayOf(SUNDAY_ID, MONDAY_ID, TUESDAY_ID, WEDNESDAY_ID, THURSDAY_ID, FRIDAY_ID, SATURDAY_ID)
    var adapters: Array<PlanRecyclerAdapter>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlanBinding.inflate(layoutInflater, container, false)
        viewModel = ViewModelProvider(this)[PlanViewModel::class.java]

        requireActivity().addMenuProvider(this, viewLifecycleOwner)
        adapters = Array(7) { PlanRecyclerAdapter(requireContext()) }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val toolbar: Toolbar = view.findViewById(R.id.toolbar_plan)
        val navController = Navigation.findNavController(view)
        val appBarConfiguration = AppBarConfiguration.Builder(
            R.id.navigation_recipe, R.id.navigation_plan
        ).build()

        NavigationUI.setupWithNavController(toolbar, navController, appBarConfiguration)
        (activity as AppCompatActivity?)!!.setSupportActionBar(toolbar)

        val recyclers = arrayOf(
            binding.sundayRecycler,
            binding.mondayRecycler,
            binding.tuesdayRecycler,
            binding.wednesdayRecycler,
            binding.thursdayRecycler,
            binding.fridayRecycler,
            binding.saturdayRecycler
        )

        //Todo: set adapter breakfast, lunch , dinner separately
        viewModel.allDays.observe(viewLifecycleOwner) { daysMap ->
            for (i in days.indices) {
                setRecycler(days[i], recyclers[i], adapters!![i])
                val recipes = listOf(daysMap[i].breakfastRecipe, daysMap[i].lunchRecipe, daysMap[i].dinnerRecipe)
                adapters!![i].setRecipes(recipes)
                adapters!![i].setPlanItemClickListener(
                    object : PlanRecyclerAdapter.PlanItemOnClickListener {
                        override fun onRecipeClick(recipe: Recipe) {
                            val sRecipe = Uri.encode(Json.encodeToString(recipe), StandardCharsets.UTF_8.toString())
                            val direction = PlanFragmentDirections.actionNavigationPlanToDirectionsFragment(sRecipe)
                            findNavController().navigate(direction)
                        }
                    }
                )
            }
        }

        setEditButtonOnclickListener()
        showWelcomeToast()
    }


    private fun setRecycler(
        dayId: Int, recyclerView: RecyclerView, adapter: PlanRecyclerAdapter
    ) {
        //observe combined liveData
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        attachItemTouchHelper(adapter, dayId, recyclerView)
    }

    private fun attachItemTouchHelper(adapter: PlanRecyclerAdapter, dayId: Int, recyclerView: RecyclerView) {
        //        //Implementation of swiping to delete
        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            0, ItemTouchHelper.RIGHT or ItemTouchHelper.LEFT
        ) {
            override fun onMove(
                recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                when (direction) {
                    ItemTouchHelper.RIGHT, ItemTouchHelper.LEFT -> {
                        //Update swipe day with swipe recipe item
                        val temp = adapter.getRecipeAt(viewHolder.absoluteAdapterPosition)
                        viewLifecycleOwner.lifecycleScope.launch {
                            val day = withContext(Dispatchers.IO) { viewModel.getDay(dayId) }
                            val updatedDay = when (temp.type) {
                                MISSING_MEAL_PLAN -> day
                                BREAKFAST -> day.copy(breakfast = NO_BREAKFAST)
                                LUNCH -> day.copy(lunch = NO_LUNCH)
                                DINNER -> day.copy(dinner = NO_DINNER)
                                else -> throw Exception("day type doesn't exist")
                            }
                            viewModel.updateDay(updatedDay)  //deletion by replacement
                            adapter.notifyItemChanged(viewHolder.absoluteAdapterPosition)
                        }
                    }
                }
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                RecyclerViewSwipeDecorator.Builder(
                    c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive
                ).addBackgroundColor(
                    ContextCompat.getColor(context!!, R.color.colorAccent)
                ).addActionIcon(R.drawable.ic_baseline_delete_outline_24)
                    .addSwipeLeftLabel(resources.getString(R.string.swipeRemove))
                    .addSwipeRightLabel(resources.getString(R.string.swipeRemove)).setSwipeLeftLabelColor(
                        ContextCompat.getColor(context!!, R.color.textSecondaryLight)
                    ).setSwipeRightLabelColor(
                        ContextCompat.getColor(context!!, R.color.textSecondaryLight)
                    ).create().decorate()
                super.onChildDraw(
                    c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive
                )
            }
        })
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }


    private fun setEditButtonOnclickListener() {
        binding.btnSundayEdit.setOnClickListener(this)
        binding.btnMondayEdit.setOnClickListener(this)
        binding.btnTuesdayEdit.setOnClickListener(this)
        binding.btnWednesdayEdit.setOnClickListener(this)
        binding.btnThursdayEdit.setOnClickListener(this)
        binding.btnFridayEdit.setOnClickListener(this)
        binding.btnSaturdayEdit.setOnClickListener(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    //btn implementations
    override fun onClick(v: View?) {
        val dayId = when (v?.id) {
            R.id.btn_sunday_edit -> SUNDAY_ID
            R.id.btn_monday_edit -> MONDAY_ID
            R.id.btn_tuesday_edit -> TUESDAY_ID
            R.id.btn_wednesday_edit -> WEDNESDAY_ID
            R.id.btn_thursday_edit -> THURSDAY_ID
            R.id.btn_friday_edit -> FRIDAY_ID
            R.id.btn_saturday_edit -> SATURDAY_ID
            else -> throw Exception("Button clicked not part of editGroup")
        }
        val direction =
            PlanFragmentDirections.actionNavigationPlanToSetPlanFragment(dayId)
        findNavController().navigate(direction)
    }


    private fun showWelcomeToast() {
        val prefs = PreferenceManager.getDefaultSharedPreferences(requireContext())
        if (prefs.getBoolean(Constants.IS_FIRST_RUN_PLAN, true)) {
            prefs.edit().putBoolean(Constants.IS_FIRST_RUN_PLAN, false).apply()
            Toast.makeText(requireContext(), R.string.str_swipe_delete, Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.plan_menu, menu)
    }

    //Open settings or clear all plans
    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        if (menuItem.itemId == R.id.action_open_plan_settings) {
            val directionsAction = PlanFragmentDirections.actionGlobalSettingsFragment()
            findNavController().navigate(directionsAction)
            return true
        } else if (menuItem.itemId == R.id.action_plan_clear) {
            onActionClearPlans()
            return true
        }
        return false
    }


    private fun onActionClearPlans() {
        val builder = MaterialAlertDialogBuilder(requireContext())

        val v: View = LayoutInflater.from(context).inflate(R.layout.clear_confirmation_dialog, null, false)
        val textView = v.findViewById<TextView>(R.id.txt_clear_confirmation)
        textView.text = getString(R.string.clear_all_plans)
        builder.setView(v)
        builder.setPositiveButton(R.string.ok) { _, _ ->
            viewModel.clearPlans()
            Toast.makeText(context, "All Plans cleared", Toast.LENGTH_SHORT).show()
        }

        builder.setNegativeButton(R.string.cancel) { dialog, _ ->
            Toast.makeText(context, "operation cancelled", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }
        builder.show()
    }
}

//Sort List to make sure the order of breakfast, lunch and dinner is correct
//    private fun sortRecipes(recipes: List<Recipe>): List<Recipe> {
//        val breakfast = recipes.filter { it.type == BREAKFAST }
//        val missing0 = recipes.filter { it.type == MISSING_MEAL_PLAN && it._id == NO_BREAKFAST }
//        val lunch = recipes.filter { it.type == LUNCH }
//        val missing1 = recipes.filter { it.type == MISSING_MEAL_PLAN && it._id == NO_LUNCH }
//        val dinner = recipes.filter { it.type == DINNER }
//        val missing2 = recipes.filter { it.type == MISSING_MEAL_PLAN && it._id == NO_DINNER }
//        return breakfast.asSequence().plus(missing0).plus(lunch).plus(missing1).plus(dinner).plus(missing2).toList()
//    }