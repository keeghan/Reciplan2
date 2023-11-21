package com.keeghan.reciplan2.ui.add

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.keeghan.reciplan2.database.Recipe
import com.keeghan.reciplan2.database.RecipeDatabase
import com.keeghan.reciplan2.database.RecipeRepository
import com.keeghan.reciplan2.utils.Constants
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

class AddViewModel(application: Application) : AndroidViewModel(application) {

    private var repository: RecipeRepository
    private var _errorMsg: MutableLiveData<String> = MutableLiveData("")
    var errorMsg: LiveData<String> = _errorMsg

    init {
        val recipeDao = RecipeDatabase.getDatabase(application, viewModelScope).recipeDao()
        repository = RecipeRepository(recipeDao)
    }

    //Compress Image then save it into Database
    fun saveRecipe(imageUri: Uri, compressedImage: File, recipe: Recipe) {
        val comp = compressImage(imageUri, compressedImage)
        if (comp) {
            viewModelScope.launch {
                repository.insert(recipe)
                File(imageUri.path!!).delete()
                _errorMsg.postValue(Constants.SUCCESS)
            }
        }
    }

    fun updateRecipe(imageUri: Uri, compressedImage: File, recipe: Recipe, oldUri: String) {
        val comp = compressImage(imageUri, compressedImage)
        if (comp) {
            viewModelScope.launch {
                repository.update(recipe)
                File(imageUri.path!!).delete()
                File(oldUri).delete()
                _errorMsg.postValue(Constants.SUCCESS)
            }
        }
    }



    fun updateRecipe(recipe: Recipe) {
        viewModelScope.launch {
            repository.update(recipe)
            _errorMsg.postValue(Constants.SUCCESS)
        }
    }

    //retrieve tempImage and compress it
    private fun compressImage(imageUri: Uri, compressedImage: File): Boolean {
        return try {
            BitmapFactory.decodeFile(imageUri.encodedPath)
                ?.compress(
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        Bitmap.CompressFormat.WEBP_LOSSLESS
                    } else Bitmap.CompressFormat.WEBP, 90, FileOutputStream(compressedImage)
                )
            true
        } catch (e: Exception) {
            _errorMsg.value = e.message
            false
        }
    }

    fun msgReset(){
        _errorMsg.value = ""
    }
}