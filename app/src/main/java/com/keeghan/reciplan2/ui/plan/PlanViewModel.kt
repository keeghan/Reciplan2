package com.keeghan.reciplan2.ui.plan

import android.app.Application
import androidx.lifecycle.*
import com.keeghan.reciplan2.database.Day
import com.keeghan.reciplan2.database.DayRepository
import com.keeghan.reciplan2.database.DayWithRecipes
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
    private val dayRepository: DayRepository = DayRepository(
        RecipeDatabase.getDatabase(application, viewModelScope).dayDao()
    )

    val allDays : LiveData<List<DayWithRecipes>> = dayRepository.getAllDays()

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
        viewModelScope.launch(Dispatchers.IO) {
            repository.update(day)
        }
    }

    fun getDay(dayId: Int): Day {
        return repository.getDay(dayId)
    }
}