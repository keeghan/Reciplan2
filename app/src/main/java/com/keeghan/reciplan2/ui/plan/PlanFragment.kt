/*
* Fragment that represents the main plan page
* It includes seven recyclers grouped under days
* */

package com.keeghan.reciplan2.ui.plan

import android.content.Intent
import android.content.res.Configuration
import android.graphics.Canvas
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.keeghan.reciplan2.R
import com.keeghan.reciplan2.SettingsActivity
import com.keeghan.reciplan2.database.Day
import com.keeghan.reciplan2.database.Recipe
import com.keeghan.reciplan2.databinding.FragmentPlanBinding
import com.keeghan.reciplan2.ui.adapters.PlanRecyclerAdapter
import com.keeghan.reciplan2.utils.Constants
import com.keeghan.reciplan2.utils.Constants.DAY
import com.keeghan.reciplan2.utils.Constants.FRIDAY_ID
import com.keeghan.reciplan2.utils.Constants.MONDAY_ID
import com.keeghan.reciplan2.utils.Constants.SATURDAY_ID
import com.keeghan.reciplan2.utils.Constants.SUNDAY_ID
import com.keeghan.reciplan2.utils.Constants.THURSDAY_ID
import com.keeghan.reciplan2.utils.Constants.TUESDAY_ID
import com.keeghan.reciplan2.utils.Constants.WEDNESDAY_ID
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator


class PlanFragment : Fragment(), View.OnClickListener, MenuProvider {

    private var _binding: FragmentPlanBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: PlanViewModel

    private lateinit var mondayAdapter: PlanRecyclerAdapter
    private lateinit var tuesdayAdapter: PlanRecyclerAdapter
    private lateinit var wednesdayAdapter: PlanRecyclerAdapter
    private lateinit var thursdayAdapter: PlanRecyclerAdapter
    private lateinit var fridayAdapter: PlanRecyclerAdapter
    private lateinit var saturdayAdapter: PlanRecyclerAdapter
    private lateinit var sundayAdapter: PlanRecyclerAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlanBinding.inflate(layoutInflater, container, false)
        val view = binding.root
        viewModel = ViewModelProvider(this)[PlanViewModel::class.java]


        //initialize all adapters with context
        setAdapters()

        setRecycler(SUNDAY_ID, binding.sundayRecycler, sundayAdapter)
        setRecycler(MONDAY_ID, binding.mondayRecycler, mondayAdapter)
        setRecycler(TUESDAY_ID, binding.tuesdayRecycler, tuesdayAdapter)
        setRecycler(WEDNESDAY_ID, binding.wednesdayRecycler, wednesdayAdapter)
        setRecycler(THURSDAY_ID, binding.thursdayRecycler, thursdayAdapter)
        setRecycler(FRIDAY_ID, binding.fridayRecycler, fridayAdapter)
        setRecycler(SATURDAY_ID, binding.saturdayRecycler, saturdayAdapter)

        setEditButtonOnclickListener()
        showWelcomeToast()

        requireActivity().addMenuProvider(this, viewLifecycleOwner)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //initialize all adapter with context
        setAdapters()

        val toolbar: Toolbar = view.findViewById(R.id.toolbar_plan)
        val navController = Navigation.findNavController(view)
        val appBarConfiguration = AppBarConfiguration.Builder(
            R.id.navigation_recipe, R.id.navigation_plan
        ).build()

        NavigationUI.setupWithNavController(toolbar, navController, appBarConfiguration)
        (activity as AppCompatActivity?)!!.setSupportActionBar(toolbar)
    }

    private fun setAdapters() {
        mondayAdapter = PlanRecyclerAdapter(context)
        tuesdayAdapter = PlanRecyclerAdapter(context)
        wednesdayAdapter = PlanRecyclerAdapter(context)
        thursdayAdapter = PlanRecyclerAdapter(context)
        fridayAdapter = PlanRecyclerAdapter(context)
        saturdayAdapter = PlanRecyclerAdapter(context)
        sundayAdapter = PlanRecyclerAdapter(context)
    }

    /*Function to set each Day's Recycler
    * and attach the adapters to it*/
    private fun setRecycler(
        dayId: Int, recyclerView: RecyclerView, adapter: PlanRecyclerAdapter
    ) {
        val tempRecipeArray = IntArray(3)
        viewModel.allDays.observe(viewLifecycleOwner) { days ->
            for (day in days) {
                if (day._id == dayId) {
                    tempRecipeArray[0] = day.breakfast
                    tempRecipeArray[1] = day.lunch
                    tempRecipeArray[2] = day.dinner
                    viewModel.setRecipeArray(tempRecipeArray, dayId)

                    //Retrieve recipes for day after setting them above
                    viewModel.getActiveDayRecipes(dayId)?.observe(
                        viewLifecycleOwner
                    ) { recipes ->
                        val sortedList = sortRecipes(recipes)
                        adapter.setRecipes(sortedList)
                    }
                }
            }
        }

        //pass array holding id to each viewModels switchMap
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = adapter
        //functions need to execute  inside main onViewCreated
        recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        recyclerView.setHasFixedSize(true)


        //Implementation of swiping to delete
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
                        val temp: Recipe =
                            adapter.getRecipeAt(viewHolder.absoluteAdapterPosition) //shouldn't have called this inside an observer
                        val tempDay: Array<Day?> = arrayOfNulls(1)
                        viewModel.allDays.observe(
                            viewLifecycleOwner
                        ) { days ->
                            for (day in days) if (day._id == dayId) {
                                tempDay[0] = day
                                when (temp._id) {
                                    day.breakfast -> {
                                        tempDay[0]?.breakfast = 0
                                    }

                                    day.lunch -> {
                                        tempDay[0]?.lunch = 1
                                    }

                                    day.dinner -> {
                                        tempDay[0]?.dinner = 2
                                    }
                                }
                            }
                        }
                        viewModel.updateDay(tempDay[0]!!)
                        adapter.notifyItemChanged(viewHolder.absoluteAdapterPosition)
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
        }) //end of item touch helper
        itemTouchHelper.attachToRecyclerView(recyclerView)
        //end of setRecycler
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
        val intent = Intent(context, SetPlanActivity::class.java)
        var value: Int? = null
        when (v!!.id) {
            R.id.btn_sunday_edit -> value = SUNDAY_ID
            R.id.btn_monday_edit -> value = MONDAY_ID
            R.id.btn_tuesday_edit -> value = TUESDAY_ID
            R.id.btn_wednesday_edit -> value = WEDNESDAY_ID
            R.id.btn_thursday_edit -> value = THURSDAY_ID
            R.id.btn_friday_edit -> value = FRIDAY_ID
            R.id.btn_saturday_edit -> value = SATURDAY_ID
        }
        intent.putExtra(DAY, value)
        startActivity(intent)
    }


    private fun showWelcomeToast() {
        val prefs = PreferenceManager.getDefaultSharedPreferences(requireContext())
        if (prefs.getBoolean(Constants.IS_FIRST_RUN_PLAN, true)) {
            prefs.edit().putBoolean(Constants.IS_FIRST_RUN_PLAN, false).apply()
            Toast.makeText(requireContext(), R.string.str_swipe_delete, Toast.LENGTH_LONG).show()
        }
    }

    //Sort List to make sure the order of breakfast, lunch and dinner is correct
    private fun sortRecipes(recipes: List<Recipe>): List<Recipe> {
        val breakfast = recipes.filter { it.type == "breakfast" }
        val missing0 = recipes.filter { it.type == "Missing Meal Plan" && it._id == 0 }
        val lunch = recipes.filter { it.type == "lunch" }
        val missing1 = recipes.filter { it.type == "Missing Meal Plan" && it._id == 1 }
        val dinner = recipes.filter { it.type == "dinner" }
        val missing2 = recipes.filter { it.type == "Missing Meal Plan" && it._id == 2 }
        return breakfast.asSequence().plus(missing0).plus(lunch).plus(missing1).plus(dinner).plus(missing2).toList()
    }


    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        //change menu color based on theme
        menuInflater.inflate(R.menu.plan_menu, menu)
        when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_NO -> menu.findItem(R.id.action_open_plan_settings)
                .setIcon(R.drawable.ic_baseline_settings_black_24)

            Configuration.UI_MODE_NIGHT_YES -> menu.findItem(R.id.action_open_plan_settings)
                .setIcon(R.drawable.ic_baseline_settings_white_24)
        }
    }

    //Open settings or clear all plans
    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        if (menuItem.itemId == R.id.action_open_plan_settings) {
            val intent = Intent(context, SettingsActivity::class.java)
            startActivity(intent)
            return true
        } else if (menuItem.itemId == R.id.action_plan_clear) {
            onActionClearPlans()
            return true
        }
        return false
    }


    private fun onActionClearPlans() {
        val builder = AlertDialog.Builder(requireContext())

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