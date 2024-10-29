package com.keeghan.reciplan2.database

import androidx.lifecycle.LiveData

class DayRepository(private val dayDao: DayDao) {

    suspend fun updateBreakFast(dayId: Int, recipeId: Int) {
        dayDao.updateBreakfast(dayId, recipeId)
    }

    suspend fun updateDinner(dayId: Int, recipeId: Int) {
        dayDao.updateDinner(dayId, recipeId)
    }

    suspend fun updateLunch(dayId: Int, recipeId: Int) {
        dayDao.updateLunch(dayId, recipeId)
    }

    fun getDayWithRecipes(dayId: Int): DayWithRecipes {
        return dayDao.getDayWithRecipes(dayId)
    }

    fun getAllDays(): LiveData<List<DayWithRecipes>> {
        return dayDao.getDaysWithRecipes()
    }

}