package com.github.goregius.shoppinglist.ui.recipes

import androidx.compose.runtime.*
import arrow.core.Either
import com.github.goregius.shoppinglist.extension.toDisplayString
import com.github.goregius.shoppinglist.model.DomainError
import com.github.goregius.shoppinglist.model.recipe.Recipe
import com.github.goregius.shoppinglist.model.todoist.ItemAddArgs
import com.github.goregius.shoppinglist.model.todoist.ItemAddCommand
import com.github.goregius.shoppinglist.repository.RecipeRepository
import com.github.goregius.shoppinglist.repository.TodoistSyncRepository
import com.github.goregius.shoppinglist.extension.toUserMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch

data class RecipeOption(val recipe: Recipe, val selected: Boolean, val expanded: Boolean)

class RecipesViewModel(
    private val recipeRepository: RecipeRepository,
    private val todoistRepository: TodoistSyncRepository,
    private val coroutineScope: CoroutineScope,
) {
    private var error by mutableStateOf<DomainError?>(null)
    val userErrorMessage get() = error?.toUserMessage()

    val recipeOptions = mutableStateListOf<RecipeOption>()

    var isAddingToShoppingList by mutableStateOf(false)

    val ingredientsCount by
        derivedStateOf { recipeOptions.filter { it.selected }.sumOf { it.recipe.ingredients.count() } }

    val isAddIngredientsButtonEnabled by derivedStateOf { !isAddingToShoppingList && recipeOptions.any { it.selected } }

    init {
        coroutineScope.launch {
            when (val recipesResult = recipeRepository.findAll()) {
                is Either.Left -> {
                    error = recipesResult.value
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

    fun selectRecipe(index: Int) {
        val recipeOption = recipeOptions[index]
        recipeOptions[index] = recipeOption.copy(selected = !recipeOption.selected)
    }

    fun expandRecipe(index: Int) {
        val recipeOption = recipeOptions[index]
        recipeOptions[index] = recipeOption.copy(expanded = !recipeOption.expanded)
    }

    private suspend fun addRecipesToShoppingList(recipes: Iterable<Recipe>) {
        if (isAddingToShoppingList) return
        isAddingToShoppingList = true
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
                    error = result.value
                    break
                }
                is Either.Right -> {
                    error = null
                    recipeOptions.replaceAll {
                        it.copy(selected = false)
                    }
                }
            }
        }

        isAddingToShoppingList = false
    }

    fun dismissErrorMessage() {
        error = null
    }
}