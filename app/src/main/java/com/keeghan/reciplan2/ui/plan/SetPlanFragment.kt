package com.keeghan.reciplan2.ui.plan

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.keeghan.reciplan2.database.Day
import com.keeghan.reciplan2.databinding.FragmentSetPlanBinding
import com.keeghan.reciplan2.ui.adapters.SetPlanRecyclerAdapter
import com.keeghan.reciplan2.utils.Constants.BREAKFAST
import com.keeghan.reciplan2.utils.Constants.DINNER
import com.keeghan.reciplan2.utils.Constants.LUNCH
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class SetPlanFragment : Fragment(), SetPlanRecyclerAdapter.ButtonClickListener {
    private val binding by lazy { FragmentSetPlanBinding.inflate(layoutInflater) }
    private val args: SetPlanFragmentArgs by navArgs()
    private lateinit var viewModel: PlanViewModel
    private var recyclerAdapter: SetPlanRecyclerAdapter? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[PlanViewModel::class.java]
        recyclerAdapter = SetPlanRecyclerAdapter(requireContext(), this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val recyclerView = binding.setPlanRecycler
        recyclerView.adapter = recyclerAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        recyclerView.setHasFixedSize(true)

        //Select whether to display breakfast, lunch or dinner lists
        binding.radioGroup.setOnCheckedChangeListener { _, editId ->
            when (editId) {
                binding.optionBreakfast.id -> viewModel.breakfastCollection.observe(viewLifecycleOwner) {
                    recyclerAdapter?.setRecipes(it)
                }

                binding.optionLunch.id -> viewModel.lunchCollection.observe(viewLifecycleOwner) {
                    recyclerAdapter?.setRecipes(it)
                }

                binding.optionDinner.id -> viewModel.dinnerCollection.observe(viewLifecycleOwner) {
                    recyclerAdapter?.setRecipes(it)
                }
            }
        }
        binding.radioGroup.check(binding.optionBreakfast.id)  //set breakfast as default option

        binding.backBtn.setOnClickListener {
            findNavController().popBackStack()
        }

        return binding.root
    }

    //Check which kind of recipe was clicked and use it day id in updating it
    override fun onAssignClick(position: Int) {
        val temp = recyclerAdapter?.getRecipeAt(position)
        viewLifecycleOwner.lifecycleScope.launch {
            val day: Day = withContext(Dispatchers.IO) { viewModel.getDay(args.dayId) }
            val updatedDay = when (temp?.type) {
                BREAKFAST -> day.copy(breakfast = temp._id)
                LUNCH -> day.copy(lunch = temp._id)
                DINNER -> day.copy(dinner = temp._id)
                else -> throw Exception("Recipe clicked has illegal type")
            }
            viewModel.updateDay(updatedDay)
            Toast.makeText(context, "Plan updated", Toast.LENGTH_SHORT).show()
        }

    }


}