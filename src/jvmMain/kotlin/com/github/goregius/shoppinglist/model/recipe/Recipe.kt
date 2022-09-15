package com.github.goregius.shoppinglist.model.recipe

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Recipe(
    @SerialName("recipe_name")
    val recipeName: String,
    @SerialName("ingredients")
    val ingredients: List<Ingredient>,
    @SerialName("method")
    val method: List<String>
)

@Serializable
data class Ingredient(
    @SerialName("unit_size")
    val unitSize: String,
    @SerialName("unit_type")
    val unitType: String,
    @SerialName("ingredient_name")
    val ingredientName: String
)