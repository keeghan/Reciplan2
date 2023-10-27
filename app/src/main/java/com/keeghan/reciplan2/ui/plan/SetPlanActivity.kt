package com.keeghan.reciplan2.ui.plan

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.keeghan.reciplan2.utils.Constants.DAY
import com.keeghan.reciplan2.R
import com.keeghan.reciplan2.database.Recipe
import com.keeghan.reciplan2.databinding.ActivitySetPlanBinding
import com.keeghan.reciplan2.ui.adapters.SetPlanRecyclerAdapter

class SetPlanActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener,
    SetPlanRecyclerAdapter.ButtonClickListener {
    private lateinit var binding: ActivitySetPlanBinding
    private lateinit var viewModel: PlanViewModel
    private lateinit var recyclerView: RecyclerView
    private var recyclerAdapter = SetPlanRecyclerAdapter(this, this)

    /*flag to determine which spinner value is currently in use
    to determining which day value to update*/
    private var spinnerId: Int? = null
    private var intentDay: Int? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySetPlanBinding.inflate(layoutInflater)
        val view = binding.root
        val toolbar = view.findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
       // supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeButtonEnabled(true)



        intentDay = intent.getIntExtra(DAY, 7)
        viewModel = ViewModelProvider(this)[PlanViewModel::class.java]
        val spinner = binding.daySpinner
        val spinnerAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.days_array,
            android.R.layout.simple_spinner_item
        )
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = spinnerAdapter
        spinner.onItemSelectedListener = this //handle clicks

        recyclerView = binding.setPlanRecycler
        recyclerView.adapter = recyclerAdapter
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerView.setHasFixedSize(true)
        setContentView(view)
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        when (parent!!.getItemAtPosition(position) as String) {
            "Breakfast" -> {
                viewModel.breakfastCollection.observe(this) {
                    recyclerAdapter.setRecipes(it)
                    spinnerId = 1
                }
            }
            "Lunch" -> {
                viewModel.lunchCollection.observe(this) {
                    recyclerAdapter.setRecipes(it)
                    spinnerId = 2
                }
            }
            "Dinner" -> {
                viewModel.dinnerCollection.observe(this) {
                    recyclerAdapter.setRecipes(it)
                    spinnerId = 3
                }
            }
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        //Do nothing
    }


    override fun onAssignClick(position: Int) {
        val workingRecipe: Recipe = recyclerAdapter.getRecipeAt(position)
        viewModel.allDays.observe(this) { days ->
            for (day in days) {
                if (day._id == intentDay!!) {
                    when (spinnerId) {
                        1 -> day.breakfast = workingRecipe._id
                        2 -> day.lunch = workingRecipe._id
                        3 -> day.dinner = workingRecipe._id
                        else -> Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
                    }
                    viewModel.updateDay(day)
                    recyclerAdapter.selectedPosition = position
                }
            }
        }
        recyclerAdapter.notifyItemChanged(position)
        Toast.makeText(
            this,
            workingRecipe.type + " changed to " + workingRecipe.name,
            Toast.LENGTH_SHORT
        ).show()
    }
}