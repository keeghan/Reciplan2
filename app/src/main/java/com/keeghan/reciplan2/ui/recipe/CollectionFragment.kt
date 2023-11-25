package com.keeghan.reciplan2.ui.recipe

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
import androidx.appcompat.app.AlertDialog
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.keeghan.reciplan2.R
import com.keeghan.reciplan2.databinding.FragmentCollectionBinding
import com.keeghan.reciplan2.ui.MainViewModel
import com.keeghan.reciplan2.ui.adapters.CollectionAdapter
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.nio.charset.StandardCharsets

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
        val isSw600dp = requireContext().resources.configuration.smallestScreenWidthDp >= 600
        if (isSw600dp) recyclerView.layoutManager = GridLayoutManager(context, 2) else
            recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        recyclerView.setHasFixedSize(true)

        adapter.setButtonClickListener(object : CollectionAdapter.ButtonClickListener {
            override fun onDirectionsClick(position: Int) {
                val clickedRecipe = adapter.getRecipeAt(position)
                val sRecipe = Uri.encode(Json.encodeToString(clickedRecipe), StandardCharsets.UTF_8.toString())
                val directionsAction =
                    DirectionsFragmentDirections.actionGlobalDirectionsFragment(sRecipe)
                findNavController().navigate(directionsAction)
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