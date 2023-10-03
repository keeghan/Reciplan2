package com.keeghan.reciplan2.ui.recipe

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.navigation.Navigation
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.keeghan.reciplan2.R
import com.keeghan.reciplan2.SettingsActivity
import com.keeghan.reciplan2.databinding.FragmentRecipeBinding

class RecipeFragment : Fragment(), MenuProvider {

    private var _binding: FragmentRecipeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecipeBinding.inflate(inflater, container, false)
        val view = binding.root

        val fragments: ArrayList<Fragment> = arrayListOf(
            ExploreFragment(),
            CollectionFragment(),
            FavoriteFragment()
        )

        // Instantiate a ViewPager2 and a PagerAdapter
        val pagerAdapter = RecipePagerAdapter(fragments, this)
        binding.viewpager.adapter = pagerAdapter
        TabLayoutMediator(binding.tabsRecipe, binding.viewpager) { tab, position ->
            when (position) {
                0 -> tab.text = "Explore"
                1 -> tab.text = "Collection"
                2 -> tab.text = "Favorite"
            }
        }.attach()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val toolbar: Toolbar = view.findViewById(R.id.toolbar)
        val navController = Navigation.findNavController(view)
        val appBarConfiguration = AppBarConfiguration.Builder(
            R.id.navigation_recipe, R.id.navigation_plan
        ).build()

        NavigationUI.setupWithNavController(toolbar, navController, appBarConfiguration)
        (activity as AppCompatActivity?)!!.setSupportActionBar(toolbar)

        requireActivity().addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private inner class RecipePagerAdapter(
        val items: ArrayList<Fragment>, activity: Fragment
    ) :
        FragmentStateAdapter(activity) {
        override fun getItemCount(): Int = items.size

        override fun createFragment(position: Int): Fragment {
            return items[position]
        }
    }

    //New Implementation for Menus
    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        //change menu color based on theme
        menuInflater.inflate(R.menu.main_menu, menu)
        when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_NO -> menu.findItem(R.id.action_open_settings)
                .setIcon(R.drawable.ic_baseline_settings_black_24)

            Configuration.UI_MODE_NIGHT_YES -> menu.findItem(R.id.action_open_settings)
                .setIcon(R.drawable.ic_baseline_settings_white_24)
        }
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        if (menuItem.itemId == R.id.action_open_settings) {
            val intent = Intent(context, SettingsActivity::class.java)
            startActivity(intent)
            return true
        }
        return false
    }

}