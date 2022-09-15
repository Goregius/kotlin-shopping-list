package com.github.goregius.shoppinglist.repository

import arrow.core.Either
import com.github.goregius.shoppinglist.model.recipe.Recipe
import com.github.goregius.shoppinglist.model.RecipeRepositoryError
import kotlinx.coroutines.flow.Flow

interface RecipeRepository {
    fun findAll(): Either<RecipeRepositoryError, Flow<Recipe>>
}