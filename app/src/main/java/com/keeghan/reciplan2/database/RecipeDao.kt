package com.keeghan.reciplan2.database

import androidx.lifecycle.LiveData
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface RecipeDao {

    @Insert
    suspend fun insert(recipe: Recipe)

    @Update
    suspend fun update(recipe: Recipe)

    @Delete
    suspend fun delete(recipe: Recipe)

    //getting Recipe data in a list form
    @Query("SELECT * FROM recipe_table ORDER By _id DESC")
    fun getAllRecipes(): Flow<List<Recipe>>

    @Query("SELECT * FROM recipe_table WHERE collection = 1 ORDER By _id DESC")
    fun getCollectionsRecipes(): LiveData<List<Recipe>>

    @Query("SELECT * FROM recipe_table WHERE favorite = 1 ORDER By _id DESC")
    fun getFavoriteRecipes(): LiveData<List<Recipe>>


    //Getting types of recipes
    @Query("SELECT * FROM recipe_table WHERE type = 'snack' ORDER By _id")
    fun getSnackRecipes(): LiveData<List<Recipe>>

    @Query("SELECT * FROM recipe_table WHERE type = 'breakfast' ORDER BY _id ")
    fun getBreakfastRecipes(): LiveData<List<Recipe>>

    @Query("SELECT * FROM recipe_table WHERE type = 'dinner' ")
    fun getDinnerRecipes(): LiveData<List<Recipe>>

    @Query("SELECT * FROM recipe_table WHERE type = 'lunch' ")
    fun getLunchRecipes(): LiveData<List<Recipe>>

    //Appbar Menu Commands
    @Query("UPDATE recipe_table SET collection = 'false', favorite = 'false' ")
    suspend fun clearCollection()

    @Query("UPDATE recipe_table SET favorite = 'false' ")
    suspend fun clearFavorite()

    @Query("UPDATE day_table SET breakfast  = '0', lunch = '1', dinner = '2' ")
    suspend fun clearPlans()

    /*Get recipes only in collection*/

    /*Get recipes only in collection*/
    @Query("SELECT * FROM recipe_table WHERE type = 'breakfast' AND collection = 1 ")
    fun getBreakfastCollectionRecipes(): LiveData<List<Recipe>>

    @Query("SELECT * FROM recipe_table WHERE type = 'dinner' AND collection = 1 ")
    fun getDinnerCollectionRecipes(): LiveData<List<Recipe>>

    @Query("SELECT * FROM recipe_table WHERE type = 'lunch' AND collection = 1 ")
    fun getLunchCollectionRecipes(): LiveData<List<Recipe>>


    /*Redundant get each day recipes*/
    @Query("SELECT * FROM recipe_table WHERE _id IN(:dayIDs) ")
    fun getActiveDayRecipes(dayIDs: IntArray): LiveData<List<Recipe>>


    /*Methods for Day_Table*/
    @Insert
    suspend fun insert(day: Day)

    @Update
    suspend fun update(day: Day)


    //Redundant day Tables
    @Query("SELECT * from  day_table WHERE _id = :dayID")
    fun getActiveDay(dayID: Int): LiveData<Day>

    @Query("SELECT * from  day_table WHERE _id = 1")
    fun getSunday(): LiveData<Day>

    @Query("SELECT * from  day_table WHERE _id = 2")
    fun getMonday(): LiveData<Day>

    @Query("SELECT * from  day_table WHERE _id = 3")
    fun getTuesday(): LiveData<Day>

    @Query("SELECT * from  day_table WHERE _id = 4")
    fun getWednesday(): LiveData<Day>

    @Query("SELECT * from  day_table WHERE _id = 5")
    fun getThursday(): LiveData<Day>

    @Query("SELECT * from  day_table WHERE _id = 6")
    fun getFriday(): LiveData<Day>

    @Query("SELECT * from  day_table WHERE _id = 7")
    fun getSaturday(): LiveData<Day>


    @Query("SELECT * from  day_table")
    fun getAllDays(): LiveData<List<Day>>

    @Query(
        """ SELECT * FROM recipe_table   WHERE _id IN (
        SELECT breakfast FROM day_table WHERE _id = :dayId
        UNION ALL
        SELECT lunch FROM day_table WHERE _id = :dayId
        UNION ALL
        SELECT dinner FROM day_table WHERE _id = :dayId ) """
    )
    fun getRecipesForDay(dayId: Int): LiveData<List<Recipe>>

    @Query("SELECT * from  day_table WHERE _id = :dayID")
    fun getDay(dayID: Int): Day
}