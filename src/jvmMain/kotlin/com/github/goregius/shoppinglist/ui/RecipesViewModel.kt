package com.github.goregius.shoppinglist.ui

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import arrow.core.Either
import com.github.goregius.shoppinglist.extension.toDisplayString
import com.github.goregius.shoppinglist.model.DomainError
import com.github.goregius.shoppinglist.model.recipe.Recipe
import com.github.goregius.shoppinglist.model.todoist.ItemAddArgs
import com.github.goregius.shoppinglist.model.todoist.ItemAddCommand
import com.github.goregius.shoppinglist.repository.RecipeRepository
import com.github.goregius.shoppinglist.repository.TodoistSyncRepository
import com.github.goregius.shoppinglist.toUserMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch

class RecipesViewModel(
    private val recipeRepository: RecipeRepository,
    private val todoistRepository: TodoistSyncRepository,
    private val coroutineScope: CoroutineScope,
) {
    // Intellij doesn't like using the 'by' keyword currently in classes, so I'm avoiding them for now
    private var error = mutableStateOf<DomainError?>(null)
    val userErrorMessage get() = error.value?.toUserMessage()

    val recipeOptions = mutableStateListOf<RecipeOption>()

    var isAddingToShoppingList = mutableStateOf(false)

    val ingredientsCount =
        derivedStateOf { recipeOptions.filter { it.selected }.sumOf { it.recipe.ingredients.count() } }

    init {
        coroutineScope.launch {
            when (val recipesResult = recipeRepository.findAll()) {
                is Either.Left -> {
                    error.value = recipesResult.value
                }

                is Either.Right -> {
                    recipeOptions.addAll(
                        recipesResult.value.toList()
                            .map { recipe -> RecipeOption(recipe = recipe, selected = false, expanded = false) })
                }
            }
        }
    }

    fun addSelectionToShoppingList() = coroutineScope.launch {
        val recipes = recipeOptions
            .filter { it.selected }
            .map { it.recipe }

        addRecipesToShoppingList(recipes)
    }

    private suspend fun addRecipesToShoppingList(recipes: Iterable<Recipe>) {
        if (isAddingToShoppingList.value) return
        isAddingToShoppingList.value = true
        val ingredients = recipes
            .flatMap {
                it.ingredients.map { ingredient -> ingredient.toDisplayString() }
            }

        val chunkedCommands = ingredients.map { ingredient ->
            ItemAddCommand(
                args = ItemAddArgs(
                    content = ingredient,
                    projectId = "2288325058"
                )
            )
        }.chunked(100)

        for (commands in chunkedCommands) {
            when (val result = todoistRepository.addItems(commands)) {
                is Either.Left -> {
                    error.value = result.value
                    break
                }

                is Either.Right -> {

                }
            }
        }

        isAddingToShoppingList.value = false
        unselectAll()
    }

    fun unselectAll() {
        recipeOptions.replaceAll {
            it.copy(selected = false)
        }
    }
}