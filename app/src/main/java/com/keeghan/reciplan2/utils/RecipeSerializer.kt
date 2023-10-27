package com.keeghan.reciplan2.utils

import com.keeghan.reciplan2.database.Recipe
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

class RecipeSerializer : KSerializer<Recipe> {
    override fun serialize(encoder: Encoder, value: Recipe) {
        encoder.encodeInt(value._id)
        encoder.encodeString(value.name)
        encoder.encodeInt(value.direction)
        encoder.encodeInt(value.ingredients)
        encoder.encodeInt(value.mins)
        encoder.encodeString(value.imageUrl)
        encoder.encodeBoolean(value.collection)
        encoder.encodeBoolean(value.favorite)
        encoder.encodeString(value.type)
        encoder.encodeBoolean(value.userCreated)
        encoder.encodeString(value.userDirection)
    }

    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("Recipe") {
        element<Int>("_id")
        element<String>("name")
        element<Int>("direction")
        element<Int>("ingredients")
        element<Int>("mins")
        element<String>("imageUrl")
        element<Boolean>("collection")
        element<Boolean>("favorite")
        element<String>("type")
        element<Boolean>("userCreated")
        element<String>("userDirection")
    }

    override fun deserialize(decoder: Decoder): Recipe {
        val _id = decoder.decodeInt()
        val name = decoder.decodeString()
        val direction = decoder.decodeInt()
        val ingredients = decoder.decodeInt()
        val mins = decoder.decodeInt()
        val imageUrl = decoder.decodeString()
        val collection = decoder.decodeBoolean()
        val favorite = decoder.decodeBoolean()
        val type = decoder.decodeString()
        val userCreated = decoder.decodeBoolean()
        val userDirection = decoder.decodeString()

        return Recipe(
            _id, name,
            direction = direction,
            ingredients = ingredients,
            mins = mins,
            imageUrl = imageUrl,
            collection = collection,
            favorite = favorite,
            type = type,
            userCreated = userCreated,
            userDirection = userDirection
        )
    }
}