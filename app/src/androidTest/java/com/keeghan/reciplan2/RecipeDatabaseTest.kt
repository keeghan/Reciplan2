package com.keeghan.reciplan2

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.keeghan.reciplan2.database.Recipe
import com.keeghan.reciplan2.database.RecipeDao
import com.keeghan.reciplan2.database.RecipeDatabase
import com.keeghan.reciplan2.database.RecipeUrls.Companion.hausa_Kooko
import com.keeghan.reciplan2.database.RecipeUrls.Companion.missing0
import com.keeghan.reciplan2.utils.Constants.BREAKFAST
import com.keeghan.reciplan2.utils.Constants.MISSING_MEAL_PLAN
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith


@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@SmallTest
class RecipeDatabaseTest {


    private lateinit var database: RecipeDatabase
    private lateinit var dao: RecipeDao

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RecipeDatabase::class.java
        ).allowMainThreadQueries().build()
        dao = database.recipeDao()
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun insertRecipe() = runTest {
        val recipe = Recipe(
            3,
            "Hausa Kooko",
            R.array.hausa_kooko_array,
            6,
            15,
            hausa_Kooko,
            collection = false,
            favorite = false,
            type = BREAKFAST,
        )

        dao.insert(recipe)

        val allRecipes = dao.getAllRecipes().first()
        assert(allRecipes.contains(recipe))
    }

    @Test
    fun deleteRecipe() = runTest {
        val recipe = Recipe(
            3,
            "Hausa Kooko",
            R.array.hausa_kooko_array,
            6,
            15,
            hausa_Kooko,
            collection = false,
            favorite = false,
            type = BREAKFAST,
        )

        dao.insert(recipe)
        dao.deleteRecipe(recipe)

        val allRecipes = dao.getAllRecipes().first()
        assertFalse(allRecipes.contains(recipe))
    }

    @Test
    fun updateRecipe() = runTest {
        val recipe = Recipe(
            3,
            "Hausa Kooko",
            R.array.hausa_kooko_array,
            6,
            15,
            hausa_Kooko,
            collection = false,
            favorite = false,
            type = BREAKFAST,
        )

        dao.insert(recipe)

        val updatedRecipe = recipe.copy(
            name = "Updated Carbonara",
            mins = 35
        )

        dao.update(updatedRecipe)

        val allRecipes = dao.getAllRecipes().first()
        assertTrue(allRecipes.contains(updatedRecipe))
        assertFalse(allRecipes.contains(recipe))
    }

    @Test
    fun testThatDefaultRecipeCannotBeDeleted() = runTest {
        val recipe = Recipe(
            0,
            "missing0",
            R.string.no_directions,
            0,
            0,
            missing0,
            collection = false,
            favorite = false,
            type = MISSING_MEAL_PLAN,
        )
        dao.insert(recipe)
        try {
            dao.deleteRecipe(recipe)
        } catch (e: IllegalStateException) {
            assert(e.message == "default recipe being deleted")
        }
    }
}