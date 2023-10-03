package com.keeghan.reciplan2.ui.recipe

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.keeghan.reciplan2.R
import com.keeghan.reciplan2.database.Recipe
import com.keeghan.reciplan2.databinding.FragmentCollectionBinding
import com.keeghan.reciplan2.ui.adapters.CollectionAdapter
import com.keeghan.reciplan2.ui.MainViewModel

class CollectionFragment : Fragment(), MenuProvider {
    private var _binding: FragmentCollectionBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: CollectionAdapter  //Adapter declared after binding in onCreateView to provide context
    private lateinit var viewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCollectionBinding.inflate(inflater, container, false)
        val view = binding.root
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        val recyclerView = binding.collectionRecycler
        adapter = CollectionAdapter(activity)
        viewModel.collectionRecipes.observe(viewLifecycleOwner) {
            //Set Visibility of empty message
            if (it.isEmpty()) {
                recyclerView.visibility = View.GONE
                binding.emptyListTxt.visibility = View.VISIBLE
            } else {
                binding.emptyListTxt.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
                adapter.setRecipes(it)
            }
        }

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(
            context, LinearLayoutManager.VERTICAL, false
        )
        recyclerView.setHasFixedSize(true)

        adapter.setButtonClickListener(object : CollectionAdapter.ButtonClickListener {
            override fun onDirectionsClick(position: Int) {
                val workingRecipe: Recipe = adapter.getRecipeAt(position)
                val intent = Intent(context, DirectionsActivity::class.java)
                intent.putExtra(DirectionsActivity.RECIPE_NAME, workingRecipe.name)
                intent.putExtra(DirectionsActivity.RECIPE_DIRECTION, workingRecipe.direction)
                intent.putExtra(DirectionsActivity.RECIPE_IMAGE, workingRecipe.imageUrl)
                intent.putExtra(DirectionsActivity.RECIPE_ID, workingRecipe._id)
                startActivity(intent)
            }

            override fun doFavoriteOperation(position: Int) {
                val workingRecipe = adapter.getRecipeAt(position)
                val favoriteStatus: Boolean = workingRecipe.favorite
                if (!favoriteStatus) {
                    workingRecipe.favorite = true
                    viewModel.updateRecipe(workingRecipe)
                    Toast.makeText(context, workingRecipe.name + " added to favorite", Toast.LENGTH_SHORT).show()
                    return
                }
                workingRecipe.favorite = false
                viewModel.updateRecipe(workingRecipe)
                Toast.makeText(context, workingRecipe.name + " removed from favorite", Toast.LENGTH_SHORT).show()
            }
        })
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun onActionClearCollection() {
        val builder = AlertDialog.Builder(requireContext())

        val v: View = LayoutInflater.from(context).inflate(R.layout.clear_confirmation_dialog, null, false)
        val textView = v.findViewById<TextView>(R.id.txt_clear_confirmation)
        textView.setText(R.string.str_clear_collection)
        builder.setView(v)
        builder.setPositiveButton(R.string.ok) { _, _ ->
            viewModel.clearCollection()
            Toast.makeText(context, "All Recipes cleared", Toast.LENGTH_SHORT).show()
        }

        builder.setNegativeButton(R.string.cancel) { dialog, _ ->
            Toast.makeText(context, "operation cancelled", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }
        builder.show()
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.collections_menu, menu)
        when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_NO -> menu.findItem(R.id.action_clear).setIcon(R.drawable.ic_action_clear_black)
            Configuration.UI_MODE_NIGHT_YES -> menu.findItem(R.id.action_clear).setIcon(R.drawable.ic_action_clear)
        }
    }

    //clear collections menu
    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        if (menuItem.itemId == R.id.action_clear) {
            onActionClearCollection()
            return true
        }
        return false
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}