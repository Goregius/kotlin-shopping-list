package com.github.goregius.shoppinglist.model.recipe

import com.github.goregius.shoppinglist.extension.toDisplayString
import kotlin.test.Test
import kotlin.test.assertEquals


internal class IngredientTest {
    @Test
    fun test() {
        assertEquals(
            "1 chicken",
            Ingredient(unitSize = "1", unitType = "", ingredientName = "chicken").toDisplayString()
        )
    }
}