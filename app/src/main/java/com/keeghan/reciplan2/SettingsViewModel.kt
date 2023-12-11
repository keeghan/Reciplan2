package com.keeghan.reciplan2

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.keeghan.reciplan2.database.Recipe
import com.keeghan.reciplan2.database.RecipeDatabase
import com.keeghan.reciplan2.database.RecipeRepository

class SettingsViewModel(application: Application) :
    AndroidViewModel(application) {

    private val repository: RecipeRepository = RecipeRepository(
        RecipeDatabase.getDatabase(application, viewModelScope).recipeDao()
    )

    fun getAllUserCreatedRecipes(): List<Recipe> {
        return repository.getAllUserCreatedRecipes()
    }
}