package com.keeghan.reciplan2.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface DayDao {
    @Transaction
    @Query("SELECT * FROM day_table")
    fun getDaysWithRecipes(): LiveData<List<DayWithRecipes>>

    @Transaction
    @Query("SELECT * FROM day_table WHERE _id = :dayId")
    fun getDayWithRecipes(dayId: Int): DayWithRecipes

    // Convenience method to get a specific meal's recipe
    @Query("SELECT * FROM recipe_table WHERE _id = :recipeId")
    fun getRecipe(recipeId: Int): Recipe?

    // Add methods to update meals
    @Query("UPDATE day_table SET breakfast = :recipeId WHERE _id = :dayId")
    suspend fun updateBreakfast(dayId: Int, recipeId: Int)

    @Query("UPDATE day_table SET lunch = :recipeId WHERE _id = :dayId")
    suspend fun updateLunch(dayId: Int, recipeId: Int)

    @Query("UPDATE day_table SET dinner = :recipeId WHERE _id = :dayId")
    suspend fun updateDinner(dayId: Int, recipeId: Int)

}