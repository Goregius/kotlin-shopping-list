package com.github.goregius.shoppinglist.env

import com.github.goregius.shoppinglist.repository.PreferencesRepository
import com.github.goregius.shoppinglist.repository.RecipeRepository
import com.github.goregius.shoppinglist.repository.TodoistSyncRepository

class Dependencies(
    val recipeRepository: RecipeRepository,
    val todoistRepository: TodoistSyncRepository,
    val preferencesRepository: PreferencesRepository,
)