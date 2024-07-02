package com.keeghan.reciplan2.ui.plan

import android.app.Application
import androidx.lifecycle.*
import com.keeghan.reciplan2.database.Day
import com.keeghan.reciplan2.database.Recipe
import com.keeghan.reciplan2.database.RecipeDatabase
import com.keeghan.reciplan2.database.RecipeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PlanViewModel(application: Application) : AndroidViewModel(
    application
) {
    private val repository: RecipeRepository = RecipeRepository(
        RecipeDatabase.getDatabase(application, viewModelScope).recipeDao()
    )

//    val recipesForSunday: LiveData<List<Recipe>> = repository.getRecipesForDay(1)
//    val recipesForMonday: LiveData<List<Recipe>> = repository.getRecipesForDay(2)
//    val recipesForTuesday: LiveData<List<Recipe>> = repository.getRecipesForDay(3)
//    val recipesForWednesday: LiveData<List<Recipe>> = repository.getRecipesForDay(4)
//    val recipesForThursday: LiveData<List<Recipe>> = repository.getRecipesForDay(5)
//    val recipesForFriday: LiveData<List<Recipe>> = repository.getRecipesForDay(6)
//    val recipesForSaturday: LiveData<List<Recipe>> = repository.getRecipesForDay(7)

    val recipesForWeek: LiveData<Map<Int, List<Recipe>>> = MediatorLiveData<Map<Int, List<Recipe>>>().apply {
        for (day in 1..7) {
            addSource(repository.getRecipesForDay(day)) { recipes ->
                val currentMap = value ?: emptyMap()
                value = currentMap + (day to recipes)
            }
        }
    }

    //SetPlanActivity variables
    val breakfastCollection = repository.getBreakfastCollectionRecipes()
    val lunchCollection = repository.getLunchCollectionRecipes()
    val dinnerCollection = repository.getDinnerCollectionRecipes()


    //Menu Command
    fun clearPlans() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.clearPlans()
        }
    }

    fun updateDay(day: Day) {
        viewModelScope.launch(Dispatchers.IO)  {
            repository.update(day)
        }
    }

    fun getDay(dayId: Int): Day {
        return repository.getDay(dayId)
    }
}