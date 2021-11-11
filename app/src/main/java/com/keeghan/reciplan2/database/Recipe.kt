package com.keeghan.reciplan2.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recipe_table")
data class Recipe(
    @PrimaryKey(autoGenerate = false)
    val _id: Int,

    val name: String,
    val direction: Int,
    val ingredients: Int,
    val mins: Int,
    val imageUrl: String,
    var collection: Boolean,
    var favorite: Boolean,
    val type: String
) {
    override fun toString(): String {
        return "Recipe(name='$name', type='$type')\n"
    }
}


