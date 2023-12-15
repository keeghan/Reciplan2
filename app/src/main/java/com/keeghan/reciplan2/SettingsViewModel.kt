package com.keeghan.reciplan2

import android.app.Application
import android.os.Environment
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.keeghan.reciplan2.database.Recipe
import com.keeghan.reciplan2.database.RecipeDatabase
import com.keeghan.reciplan2.database.RecipeRepository
import com.keeghan.reciplan2.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: RecipeRepository = RecipeRepository(
        RecipeDatabase.getDatabase(application, viewModelScope).recipeDao()
    )

    private var _errorMsg: MutableLiveData<String> = MutableLiveData("")
    var errorMsg: LiveData<String> = _errorMsg

    fun getAllUserCreatedRecipes(): List<Recipe> {
        return repository.getAllUserCreatedRecipes()
    }

    fun writeToFile(fileName: String, jsonString: String) {
        if (fileName.isNotBlank()) {
            val downloadsDir =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val file = File(downloadsDir, "$fileName.json")
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    file.writeText(jsonString)
                    _errorMsg.postValue("Recipes Export successfully")
                } catch (e: Exception) {
                    _errorMsg.postValue("Error: ${e.message}")
                }
            }
        } else _errorMsg.postValue("FileName Invalid")
    }

    fun importRecipes(recipes: List<Recipe>) {
        viewModelScope.launch {
            try {
                for (recipe in recipes) {
                    repository.insert(recipe.copy(_id = Constants.generateUniqueId()))
                    delay(5)
                }
                _errorMsg.postValue("${recipes.size} recipes imported!\n edit recipes to add images")
            } catch (e: Exception) {
                _errorMsg.postValue(e.message)
            }
        }
    }

    fun resetErrorMessage() {
        _errorMsg.value = ""
    }

}