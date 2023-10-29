package com.keeghan.reciplan2.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "recipe_table")
data class Recipe(
    @PrimaryKey(autoGenerate = true)  //"autoGenerate = true , = 0" migration 1_2
    val _id: Int = 0,

    val name: String,
    val direction: Int = 0,
    val ingredients: Int = 0,
    val mins: Int,
    val imageUrl: String,
    var collection: Boolean,
    var favorite: Boolean,
    val type: String,
    //new column migration 1_2
    val userCreated: Boolean = false,
    val userDirection: String = "",
    val userIngredient: String = "",


    ) {
    override fun toString(): String {
        return "Recipe(name='$name', type='$type')\n"
    }
}


