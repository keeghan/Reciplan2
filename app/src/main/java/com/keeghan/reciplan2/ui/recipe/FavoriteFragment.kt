package com.keeghan.reciplan2.ui.recipe

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
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.keeghan.reciplan2.R
import com.keeghan.reciplan2.database.Recipe
import com.keeghan.reciplan2.databinding.FragmentFavoriteBinding
import com.keeghan.reciplan2.ui.MainViewModel
import com.keeghan.reciplan2.ui.adapters.FavoriteAdapter
import com.keeghan.reciplan2.ui.adapters.FavoriteAdapter.ButtonClickListener
import com.keeghan.reciplan2.ui.recipe.ChooseRecipeActivity.Companion.encodeRecipeToDirectionsActivity

class FavoriteFragment : Fragment(), MenuProvider {
    private lateinit var viewModel: MainViewModel
    private var _binding: FragmentFavoriteBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: FavoriteAdapter  //Adapter declared after binding in onCreateView to provide context

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoriteBinding.inflate(layoutInflater, container, false)
        val view = binding.root
        val recyclerView = binding.favoriteRecycler
        val textView = binding.emptyListTxt
        adapter = FavoriteAdapter(context)
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        viewModel.favoriteRecipes.observe(
            viewLifecycleOwner
        ) {
            adapter.setRecipes(it)
            //Set Visibility of empty message
            if (it.isEmpty()) {
                recyclerView.visibility = View.GONE
                textView.visibility = View.VISIBLE
            } else {
                textView.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
            }
        }

        recyclerView.adapter = adapter
        recyclerView.layoutManager = GridLayoutManager(context, 2)
        recyclerView.setHasFixedSize(true)

        adapter.setButtonClickListener(object : ButtonClickListener {
            override fun onDirectionsClick(position: Int) {
                val workingRecipe: Recipe = adapter.getRecipeAt(position)
                encodeRecipeToDirectionsActivity(requireContext(), workingRecipe)
            }

            override fun doFavoriteOperation(position: Int) {
                val workingRecipe = adapter.getRecipeAt(position)
                val favoriteStatus: Boolean = workingRecipe.favorite
                if (!favoriteStatus) {
                    workingRecipe.favorite = true
                    viewModel.updateRecipe(workingRecipe)
                    Toast.makeText(
                        context, workingRecipe.name + " added to favorite",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                workingRecipe.favorite = false
                viewModel.updateRecipe(workingRecipe)
                Toast.makeText(
                    context, workingRecipe.name + " removed from favorite",
                    Toast.LENGTH_SHORT
                ).show()
            }

        })

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    /*method to confirm clearing collections option*/
    private fun onActionClearFavorite() {
        val builder = AlertDialog.Builder(
            requireContext()
        )
        val v =
            LayoutInflater.from(context).inflate(R.layout.clear_confirmation_dialog, null, false)
        val textView = v.findViewById<TextView>(R.id.txt_clear_confirmation)
        textView.setText(R.string.str_clear_favorite)
        builder.setView(v)
        builder.setPositiveButton(
            "OK"
        ) { _, _ ->
            viewModel.clearFavorite()
            Toast.makeText(context, "All favorite Recipes cleared", Toast.LENGTH_SHORT).show()
        }
        builder.setNegativeButton(
            "cancel"
        ) { dialog, _ ->
            Toast.makeText(context, "operation cancelled", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }
        builder.show()
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.collections_menu, menu)
    }

    //clear collections menu
    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        if (menuItem.itemId == R.id.action_clear) {
            onActionClearFavorite()
            return true
        }
        return false
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


