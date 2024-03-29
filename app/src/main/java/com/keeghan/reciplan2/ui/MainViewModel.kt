package com.keeghan.reciplan2.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.keeghan.reciplan2.database.Recipe
import com.keeghan.reciplan2.database.RecipeDatabase
import com.keeghan.reciplan2.database.RecipeRepository
import kotlinx.coroutines.launch

class MainViewModel(application: Application) :
    AndroidViewModel(
        application
    ) {
    private val repository: RecipeRepository = RecipeRepository(
        RecipeDatabase.getDatabase(application, viewModelScope).recipeDao()
    )

    //getting different recipetype
    val breakfastRecipes = repository.getBreakfastRecipes()
    val lunchRecipes = repository.getLunchRecipes()
    val dinnerRecipes = repository.getDinnerRecipes()
    val snackRecipes = repository.getSnackRecipes()

    //getting different collections and favorites
    val collectionRecipes = repository.getCollectionRecipes()
    val favoriteRecipes = repository.getFavoriteRecipes()


    //update methods
    fun updateRecipe(recipe: Recipe) {
        viewModelScope.launch { repository.update(recipe) }
    }

    //delete methods
    fun deleteRecipe(recipe: Recipe) {
        viewModelScope.launch { repository.deleteRecipe(recipe) }
    }

    //update methods
//    fun insertRecipe(recipe: Recipe) {
//        viewModelScope.launch {
//            repository.insert(recipe)
//        }
//    }

    //Menu commands
    fun clearCollection() {
        viewModelScope.launch {
            repository.clearCollection()
        }
    }

    fun clearFavorite() {
        viewModelScope.launch {
            repository.clearFavorite()
        }
    }

    fun getAllUserCreatedRecipes(): List<Recipe> {
        return repository.getAllUserCreatedRecipes()
    }
}