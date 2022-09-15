package com.github.goregius.shoppinglist.extension

import com.github.goregius.shoppinglist.model.recipe.Ingredient

private val pattern = "\\s+".toRegex()

fun Ingredient.toDisplayString() = "$unitSize $unitType $ingredientName".replace(pattern) { it.value[0].toString() }