package com.keeghan.reciplan2.ui.plan

import android.app.Application
import androidx.lifecycle.*
import com.keeghan.reciplan2.database.Day
import com.keeghan.reciplan2.database.Recipe
import com.keeghan.reciplan2.database.RecipeDatabase
import com.keeghan.reciplan2.database.RecipeRepository
import kotlinx.coroutines.launch

class PlanViewModel(application: Application) : AndroidViewModel(
    application
) {
    private var repository: RecipeRepository

    init {
        val recipeDao = RecipeDatabase.getDatabase(application, viewModelScope).recipeDao()
        repository = RecipeRepository(recipeDao)
    }

    val allDays: LiveData<List<Day>> = repository.getAllDays()

    //temporaryLiveData for recipe transformation
    private val tempRecipeArray1 = MutableLiveData<IntArray>()
    private val tempRecipeArray2 = MutableLiveData<IntArray>()
    private val tempRecipeArray3 = MutableLiveData<IntArray>()
    private val tempRecipeArray4 = MutableLiveData<IntArray>()
    private val tempRecipeArray5 = MutableLiveData<IntArray>()
    private val tempRecipeArray6 = MutableLiveData<IntArray>()
    private val tempRecipeArray7 = MutableLiveData<IntArray>()


    //LiveData is observed after Transformation.SwitchMap
    private var sundayRecipes = tempRecipeArray1.switchMap { repository.getActiveDayRecipes(it) }
    private var mondayRecipes = tempRecipeArray2.switchMap { repository.getActiveDayRecipes(it) }
    private var tuesdayRecipes = tempRecipeArray3.switchMap { repository.getActiveDayRecipes(it) }
    private var wednesdayRecipes = tempRecipeArray4.switchMap { repository.getActiveDayRecipes(it) }
    private var thursdayRecipes = tempRecipeArray5.switchMap { repository.getActiveDayRecipes(it) }
    private var fridayRecipes = tempRecipeArray6.switchMap { repository.getActiveDayRecipes(it) }
    private var saturdayRecipes = tempRecipeArray7.switchMap { repository.getActiveDayRecipes(it) }


    //SetPlanActivity variables
    val breakfastCollection = repository.getBreakfastCollectionRecipes()
    val lunchCollection = repository.getLunchCollectionRecipes()
    val dinnerCollection = repository.getDinnerCollectionRecipes()


    fun setRecipeArray(recipeArray: IntArray, day: Int) {
        when (day) {
            1 -> tempRecipeArray1.value = recipeArray
            2 -> tempRecipeArray2.value = recipeArray
            3 -> tempRecipeArray3.value = recipeArray
            4 -> tempRecipeArray4.value = recipeArray
            5 -> tempRecipeArray5.value = recipeArray
            6 -> tempRecipeArray6.value = recipeArray
            7 -> tempRecipeArray7.value = recipeArray
        }
    }

    fun getActiveDayRecipes(day: Int): LiveData<List<Recipe>>? {
        return when (day) {
            1 -> sundayRecipes
            2 -> mondayRecipes
            3 -> tuesdayRecipes
            4 -> wednesdayRecipes
            5 -> thursdayRecipes
            6 -> fridayRecipes
            7 -> saturdayRecipes
            else -> null
        }
    }

    //Menu Command
    fun clearPlans() {
        viewModelScope.launch {
            repository.clearPlans()
        }
    }

    fun updateDay(day: Day) {
        viewModelScope.launch {
            repository.update(day)
        }
    }
}