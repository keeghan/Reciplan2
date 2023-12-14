package com.keeghan.reciplan2.database

import androidx.lifecycle.LiveData
import java.io.File

class RecipeRepository(private val recipeDao: RecipeDao) {

    suspend fun update(recipe: Recipe) {
        recipeDao.update(recipe)
    }

    suspend fun insert(recipe: Recipe) {
        recipeDao.insert(recipe)
    }

    suspend fun deleteRecipe(recipe: Recipe) {
        val imageUrl = recipe.imageUrl
        recipeDao.deleteRecipe(recipe)
        deleteImageUrl(imageUrl)
    }

    //Getting types of recipes
    fun getSnackRecipes(): LiveData<List<Recipe>> {
        return recipeDao.getSnackRecipes()
    }

    fun getBreakfastRecipes(): LiveData<List<Recipe>> {
        return recipeDao.getBreakfastRecipes()
    }

    fun getDinnerRecipes(): LiveData<List<Recipe>> {
        return recipeDao.getDinnerRecipes()
    }

    fun getLunchRecipes(): LiveData<List<Recipe>> {
        return recipeDao.getLunchRecipes()
    }

    //Getting recipes in collection for SetPlanActivity
    fun getBreakfastCollectionRecipes(): LiveData<List<Recipe>> {
        return recipeDao.getBreakfastCollectionRecipes()
    }

    fun getDinnerCollectionRecipes(): LiveData<List<Recipe>> {
        return recipeDao.getDinnerCollectionRecipes()
    }

    fun getLunchCollectionRecipes(): LiveData<List<Recipe>> {
        return recipeDao.getLunchCollectionRecipes()
    }


    //Collections and favorites
    fun getCollectionRecipes(): LiveData<List<Recipe>> {
        return recipeDao.getCollectionsRecipes()
    }

    fun getFavoriteRecipes(): LiveData<List<Recipe>> {
        return recipeDao.getFavoriteRecipes()
    }


    //Menu Commands
    suspend fun clearCollection() {
        recipeDao.clearCollection()
    }

    suspend fun clearFavorite() {
        recipeDao.clearFavorite()
    }

    suspend fun clearPlans() {
        recipeDao.clearPlans()
    }


    //Day methods
    suspend fun update(day: Day) {
        recipeDao.update(day)
    }

    fun getRecipesForDay(dayId: Int): LiveData<List<Recipe>> {
        return recipeDao.getRecipesForDay(dayId)
    }

    fun getDay(dayId: Int): Day {
        return recipeDao.getDay(dayId)
    }

    private fun deleteImageUrl(imageUrl: String): Boolean {
        val file = File(imageUrl)
        if (file.exists()) {
            return file.delete()
        }
        return false
    }

     fun getAllUserCreatedRecipes(): List<Recipe> {
        return recipeDao.getAllUserCreatedRecipes()
    }
}