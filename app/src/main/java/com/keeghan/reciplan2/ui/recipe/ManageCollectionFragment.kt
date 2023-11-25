package com.keeghan.reciplan2.ui.recipe

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.keeghan.reciplan2.R
import com.keeghan.reciplan2.database.Recipe
import com.keeghan.reciplan2.databinding.ExplorerBottomMenuBinding
import com.keeghan.reciplan2.databinding.FragmentManageCollectionBinding
import com.keeghan.reciplan2.ui.MainViewModel
import com.keeghan.reciplan2.ui.adapters.RecipeAdapter
import com.keeghan.reciplan2.ui.add.AddDialogFragment
import com.keeghan.reciplan2.utils.Constants.BREAKFAST
import com.keeghan.reciplan2.utils.Constants.DINNER
import com.keeghan.reciplan2.utils.Constants.LUNCH
import com.keeghan.reciplan2.utils.Constants.SNACK
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.nio.charset.StandardCharsets

class ManageCollectionFragment : Fragment() {
    private val binding by lazy { FragmentManageCollectionBinding.inflate(layoutInflater) }
    private val args: ManageCollectionFragmentArgs by navArgs()

    private lateinit var viewModel: MainViewModel
    private lateinit var adapter: RecipeAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        adapter = RecipeAdapter(requireContext())

        //Retrieve mealType from ExploreFragment
        // recipe = args.mealType
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        getIntentList()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.titleText.text = getString(R.string.manage_recipes)

        binding.backBtn.setOnClickListener {
            findNavController().popBackStack()
        }

        //Set onclickListener for All RecyclerItem buttons
        adapter.setButtonClickListener(object : RecipeAdapter.ButtonClickListener {
            //Get Recipe clicked, serialize it and send it to [DirectionsActivity]
            override fun onDirectionsClick(position: Int) {
                val workingRecipe: Recipe = adapter.getRecipeAt(position)
                val sRecipe = Uri.encode(Json.encodeToString(workingRecipe), StandardCharsets.UTF_8.toString())
                val directionsAction = ManageCollectionFragmentDirections.actionGlobalDirectionsFragment(sRecipe)
                findNavController().navigate(directionsAction)
            }

            override fun removeFromCollection(position: Int) {
                remove4rmCollection(position)
            }

            override fun addToCollection(position: Int) {
                add2Collection(position)
            }

            //Open bottom dialog Menu
            override fun onMenuClick(position: Int) {
                openBottomMenu(position)
            }

        })
        val isSw600dp = requireContext().resources.configuration.smallestScreenWidthDp >= 600
        val layoutManager = if (isSw600dp) GridLayoutManager(context, 2) else
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.exploreRecycler.layoutManager = layoutManager

        binding.exploreRecycler.adapter = adapter
    }

    private fun getIntentList() {
        when (args.mealType) {
            SNACK -> viewModel.snackRecipes.observe(viewLifecycleOwner) { recipes ->
                adapter.setRecipes(recipes)
            }

            BREAKFAST -> viewModel.breakfastRecipes.observe(viewLifecycleOwner) { recipes ->
                adapter.setRecipes(recipes)
            }

            LUNCH -> viewModel.lunchRecipes.observe(viewLifecycleOwner) { recipes ->
                adapter.setRecipes(recipes)
            }

            DINNER -> viewModel.dinnerRecipes.observe(viewLifecycleOwner) { recipes ->
                adapter.setRecipes(recipes)
            }
        }
    }


    //Collection methods
    fun remove4rmCollection(position: Int) {
        val removeRecipe = adapter.getRecipeAt(position)
        val collectionStatus: Boolean = removeRecipe.collection
        if (collectionStatus) {
            removeRecipe.collection = false
            removeRecipe.favorite = false //You can't be part of favorite but not collection
            viewModel.updateRecipe(removeRecipe)
            showToast(removeRecipe.name + " removed from Collection")
            return
        }
        showToast(removeRecipe.name + " not part of Collection")
    }


    fun add2Collection(position: Int) {
        val workingRecipe = adapter.getRecipeAt(position)
        if (workingRecipe.collection) {
            showToast(workingRecipe.name + " already part of Collection")
            return
        }
        workingRecipe.collection = true
        viewModel.updateRecipe(workingRecipe)
        showToast(workingRecipe.name + " added to Collection")
    }


    //Delete Recipe created by user from Database [from day_table and recipe_table]
    private fun deleteUserRecipe(position: Int) {
        val workingRecipe = adapter.getRecipeAt(position)

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Confirm Deletion").setIcon(R.drawable.outline_delete_24)
            .setPositiveButton("Yes") { dialog, _ ->
                // Delete the recipe
                viewModel.deleteRecipe(workingRecipe)
                dialog.dismiss()
                showToast("${workingRecipe.name} deleted")
            }.setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }.show()
    }

    private fun openBottomMenu(clickedRecipePosition: Int) {
        val menuRecipe = adapter.getRecipeAt(clickedRecipePosition)

        //Bottom Dialog sheet bindings
        val bottomMenuBinding by lazy { ExplorerBottomMenuBinding.inflate(layoutInflater) }
        val bottomSheet = BottomSheetDialog(requireContext())
        //   bottomMenuBinding = ExplorerBottomMenuBinding.inflate(bottomSheet!!.layoutInflater)
        bottomSheet.setContentView(bottomMenuBinding.root)
        bottomSheet.behavior.peekHeight = -1


        //Populate Bottom Dialog
        bottomMenuBinding.recipeName.text = menuRecipe.name
        bottomMenuBinding.deleteGrp.setOnClickListener {
            bottomSheet.dismiss()
            deleteUserRecipe(clickedRecipePosition)
        }

        //Open editFragment
        bottomMenuBinding.editGrp.setOnClickListener {
            bottomSheet.dismiss()
            val dialogFragment = AddDialogFragment(menuRecipe)
            dialogFragment.show(parentFragmentManager, "my_dialog_tag")
        }

        bottomSheet.show()
    }

    private fun showToast(msg: String) {
        Toast.makeText(
            requireContext(), msg, Toast.LENGTH_SHORT
        ).show()
    }
}