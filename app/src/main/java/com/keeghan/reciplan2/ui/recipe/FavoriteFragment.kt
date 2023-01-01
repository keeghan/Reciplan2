package com.keeghan.reciplan2.ui.recipe

import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.keeghan.reciplan2.R
import com.keeghan.reciplan2.database.Recipe
import com.keeghan.reciplan2.databinding.FragmentFavoriteBinding
import com.keeghan.reciplan2.ui.MainViewModel
import com.keeghan.reciplan2.ui.adapters.FavoriteAdapter
import com.keeghan.reciplan2.ui.adapters.FavoriteAdapter.ButtonClickListener

class FavoriteFragment : Fragment() {
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
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
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
                val intent = Intent(context, DirectionsActivity::class.java)
                intent.putExtra(DirectionsActivity.RECIPE_NAME, workingRecipe.name)
                intent.putExtra(DirectionsActivity.RECIPE_DIRECTION, workingRecipe.direction)
                intent.putExtra(DirectionsActivity.RECIPE_IMAGE, workingRecipe.imageUrl)
                startActivity(intent)
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

        setHasOptionsMenu(true)
        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.collections_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
        when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_NO -> menu.findItem(R.id.action_clear)
                .setIcon(R.drawable.ic_action_clear_black)
            Configuration.UI_MODE_NIGHT_YES -> menu.findItem(R.id.action_clear)
                .setIcon(R.drawable.ic_action_clear)
        }
    }

    //clear collections menu
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_clear) {
            onActionClearFavorite()
            return true
        }
        return super.onOptionsItemSelected(item)
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
}
