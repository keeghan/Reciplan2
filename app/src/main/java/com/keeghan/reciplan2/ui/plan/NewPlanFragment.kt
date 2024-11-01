package com.keeghan.reciplan2.ui.plan
//
//import android.net.Uri
//import android.os.Bundle
//import android.util.Log
//import android.view.LayoutInflater
//import android.view.Menu
//import android.view.MenuInflater
//import android.view.MenuItem
//import android.view.View
//import android.view.ViewGroup
//import android.widget.TextView
//import android.widget.Toast
//import androidx.appcompat.app.AppCompatActivity
//import androidx.appcompat.widget.Toolbar
//import androidx.core.view.MenuProvider
//import androidx.fragment.app.Fragment
//import androidx.lifecycle.ViewModelProvider
//import androidx.navigation.Navigation
//import androidx.navigation.fragment.findNavController
//import androidx.navigation.ui.AppBarConfiguration
//import androidx.navigation.ui.NavigationUI
//import androidx.preference.PreferenceManager
//import androidx.recyclerview.widget.LinearLayoutManager
//import com.google.android.material.dialog.MaterialAlertDialogBuilder
//import com.keeghan.reciplan2.R
//import com.keeghan.reciplan2.database.Recipe
//import com.keeghan.reciplan2.databinding.NewFragmentPlanBinding
//import com.keeghan.reciplan2.ui.adapters.DayPlanAdapter
//import com.keeghan.reciplan2.utils.Constants
//import kotlinx.serialization.encodeToString
//import kotlinx.serialization.json.Json
//import java.nio.charset.StandardCharsets
//
//class NewPlanFragment : Fragment(), MenuProvider {
//    private var _binding: NewFragmentPlanBinding? = null
//    private val binding get() = _binding!!
//    private lateinit var viewModel: PlanViewModel
//    private lateinit var daysAdapter: DayPlanAdapter
//
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        daysAdapter = DayPlanAdapter()
//    }
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
//    ): View {
//        _binding = NewFragmentPlanBinding.inflate(layoutInflater, container, false)
//        viewModel = ViewModelProvider(this)[PlanViewModel::class.java]
//
//        requireActivity().addMenuProvider(this, viewLifecycleOwner)
//        return binding.root
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        val toolbar: Toolbar = view.findViewById(R.id.toolbar_plan)
//        val navController = Navigation.findNavController(view)
//        val appBarConfiguration = AppBarConfiguration.Builder(
//            R.id.navigation_recipe, R.id.navigation_plan
//        ).build()
//
//        NavigationUI.setupWithNavController(toolbar, navController, appBarConfiguration)
//        (activity as AppCompatActivity?)!!.setSupportActionBar(toolbar)
//
//        val dayRecycler = binding.dayPlanRecycler
//        dayRecycler.setHasFixedSize(true)
//        dayRecycler.adapter = daysAdapter
//        dayRecycler.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
//        //  attachItemTouchHelper(adapter, dayId, recyclerView)
//
//        //inflate recyclerView
//        viewModel.allDays.observe(viewLifecycleOwner) { daysMap ->
//            daysAdapter.updateData(daysMap);
//        }
//
//        //Todo: implement onTouch Events
//        //setEditButtonOnclickListener()
//
//        showWelcomeToast()
//        //implement buttonClick interface for all clicks on the recyclerview items
//        // +1 to offset recyclerAdapter absolutePosition
//        daysAdapter.setButtonClickListener(
//            object : DayPlanAdapter.DayPlanOnClickListener {
//                override fun onEditClick(position: Int) {
//                    val direction =
//                        NewPlanFragmentDirections.actionNewPlanFragmentToSetPlanFragment(position + 1)
//                    findNavController().navigate(direction)
//                }
//
//                //get recipe converted to spring format and pass to directions fragment
//                //don't do anything when recipe is missing
//                override fun onRecipeClick(recipe: Recipe) {
//                    if (recipe.name != "missing0" && recipe.name != "missing1" && recipe.name != "missing2") {
//                        val sRecipe = Uri.encode(Json.encodeToString(recipe), StandardCharsets.UTF_8.toString())
//                        val directionsAction =
//                            NewPlanFragmentDirections.actionNavigationNewPlanToDirectionsFragment(sRecipe)
//                        findNavController().navigate(directionsAction)
//                    }else{
//                        Toast.makeText(context, getString(R.string.recipe_not_set), Toast.LENGTH_SHORT).show()
//                    }
//                }
//            }
//        )
//
//    }//end of onViewCreated
//
//
//    // ToolBar options Implementations
//    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
//        menuInflater.inflate(R.menu.plan_menu, menu)
//    }
//
//    //Open settings or clear all plans
//    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
//        if (menuItem.itemId == R.id.action_open_plan_settings) {
//            val directionsAction = NewPlanFragmentDirections.actionGlobalSettingsFragment()
//            findNavController().navigate(directionsAction)
//            return true
//        } else if (menuItem.itemId == R.id.action_plan_clear) {
//            onActionClearPlans()
//            return true
//        }
//        return false
//    }
//
//    //ToolBar menu option to clear all items
//    private fun onActionClearPlans() {
//        val builder = MaterialAlertDialogBuilder(requireContext())
//
//        val v: View = LayoutInflater.from(context).inflate(R.layout.clear_confirmation_dialog, null, false)
//        val textView = v.findViewById<TextView>(R.id.txt_clear_confirmation)
//        textView.text = getString(R.string.clear_all_plans)
//        builder.setView(v)
//        builder.setPositiveButton(R.string.ok) { _, _ ->
//            viewModel.clearPlans()
//            Toast.makeText(context, getString(R.string.all_plans_cleared), Toast.LENGTH_SHORT).show()
//        }
//
//        builder.setNegativeButton(R.string.cancel) { dialog, _ ->
//            Toast.makeText(context, getString(R.string.operation_cancelled), Toast.LENGTH_SHORT).show()
//            dialog.dismiss()
//        }
//        builder.show()
//    }
//
//    //First Time user welcome message
//    private fun showWelcomeToast() {
//        val prefs = PreferenceManager.getDefaultSharedPreferences(requireContext())
//        if (prefs.getBoolean(Constants.IS_FIRST_RUN_PLAN, true)) {
//            prefs.edit().putBoolean(Constants.IS_FIRST_RUN_PLAN, false).apply()
//            Toast.makeText(requireContext(), R.string.str_swipe_delete, Toast.LENGTH_LONG).show()
//        }
//    }
//
//}