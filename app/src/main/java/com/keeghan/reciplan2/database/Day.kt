package com.keeghan.reciplan2.database

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "day_table",
    foreignKeys = [ForeignKey(
        entity = Recipe::class,
        parentColumns = arrayOf("_id"),
        childColumns = arrayOf("breakfast")
    ), ForeignKey(
        entity = Recipe::class,
        parentColumns = arrayOf("_id"),
        childColumns = arrayOf("lunch")
    ), ForeignKey(
        entity = Recipe::class,
        parentColumns = arrayOf("_id"),
        childColumns = arrayOf("dinner")
    )],
    indices = [Index(value = arrayOf("breakfast")),
        Index(value = arrayOf("lunch")),
        Index(
            value = arrayOf(
                "dinner"
            )
        )]
)
data class Day(
    @PrimaryKey(autoGenerate = false)
    var _id: Int,

    val name: String,
    var breakfast: Int,
    var lunch: Int,
    var dinner :Int
)