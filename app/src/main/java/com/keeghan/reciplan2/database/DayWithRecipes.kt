package com.keeghan.reciplan2.database

import androidx.room.Embedded
import androidx.room.Relation

data class DayWithRecipes(
    @Embedded val day: Day,

    @Relation(
        parentColumn = "breakfast",
        entityColumn = "_id"
    )
    val breakfastRecipe: Recipe,

    @Relation(
        parentColumn = "lunch",
        entityColumn = "_id"
    )
    val lunchRecipe: Recipe,

    @Relation(
        parentColumn = "dinner",
        entityColumn = "_id"
    )
    val dinnerRecipe: Recipe,

)