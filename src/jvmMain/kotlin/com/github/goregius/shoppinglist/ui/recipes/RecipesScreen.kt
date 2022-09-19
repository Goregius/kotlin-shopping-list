package com.github.goregius.shoppinglist.ui.recipes

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.updateTransition
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import arrow.core.Either
import arrow.core.right
import com.github.goregius.shoppinglist.extension.toDisplayString
import com.github.goregius.shoppinglist.model.RecipeRepositoryError
import com.github.goregius.shoppinglist.model.TodoistRepositoryError
import com.github.goregius.shoppinglist.model.recipe.Ingredient
import com.github.goregius.shoppinglist.model.recipe.Recipe
import com.github.goregius.shoppinglist.model.todoist.AddItemsResponse
import com.github.goregius.shoppinglist.model.todoist.ItemAddCommand
import com.github.goregius.shoppinglist.repository.RecipeRepository
import com.github.goregius.shoppinglist.repository.TodoistSyncRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

@Composable
fun RecipesScreen(viewModel: RecipesViewModel) {
    val recipeOptions = viewModel.recipeOptions

    Column(Modifier.fillMaxSize().padding(8.dp)) {
        Card(Modifier.weight(1f).fillMaxWidth()) {
            Column {
                Box(
                    modifier = Modifier.fillMaxSize().padding(4.dp)
                ) {
                    val stateVertical = rememberScrollState(0)
                    // LazyColumn would be preferable here but the expanding lists cause the scrollbar to bug out
                    Column(Modifier.verticalScroll(stateVertical)) {
                        viewModel.userErrorMessage?.let { message ->
                            Text(
                                text = message,
                                modifier = Modifier.clickable { viewModel.dismissErrorMessage() },
                                color = animateFunkyColor().value
                            )
                        }
                        recipeOptions.forEachIndexed { index, recipeOption ->
                            RecipeOptionCard(
                                recipeOption = recipeOption,
                                onRecipeSelect = {
                                    recipeOptions[index] = recipeOption.copy(selected = !recipeOption.selected)
                                },
                                onRecipeExpand = {
                                    recipeOptions[index] =
                                        recipeOption.copy(expanded = !recipeOption.expanded)
                                }
                            )
                        }
                    }
                    VerticalScrollbar(
                        modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
                        adapter = rememberScrollbarAdapter(
                            scrollState = stateVertical
                        ),
                    )
                }
            }
        }

        if (viewModel.isAddingToShoppingList) {
            Spacer(Modifier.height(8.dp))
            LinearProgressIndicator(Modifier.fillMaxWidth().height(10.dp))
        }

        Spacer(Modifier.height(8.dp))

        Button(
            onClick = {
                viewModel.addSelectionToShoppingList()
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !viewModel.isAddingToShoppingList && viewModel.recipeOptions.any { it.selected }
        ) {
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                if (viewModel.ingredientsCount > 0) {
                    val s = if (viewModel.ingredientsCount > 1) "s" else ""
                    Text("Add ${viewModel.ingredientsCount} ingredient$s to the shopping list")
                } else {
                    Text("Select a recipe")
                }
            }
        }
    }
}

@Composable
private fun RecipeOptionCard(
    recipeOption: RecipeOption,
    onRecipeSelect: () -> Unit,
    onRecipeExpand: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(modifier.fillMaxWidth().padding(2.dp)) {
        Column(Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(recipeOption.selected, {
                        onRecipeSelect()
                    })
                    Text(recipeOption.recipe.recipeName)
                }
                IconButton(onClick = {
                    onRecipeExpand()
                }, Modifier.padding(end = 8.dp)) {
                    if (recipeOption.expanded) {
                        Icon(Icons.Default.KeyboardArrowUp, "Save")
                    } else {
                        Icon(Icons.Default.KeyboardArrowDown, "Save")
                    }
                }
            }
            AnimatedVisibility(recipeOption.expanded) {
                Card(Modifier.fillMaxWidth().padding(8.dp)) {
                    IngredientsList(recipeOption.recipe.ingredients)
                }
            }
        }
    }
}

@Composable
private fun IngredientsList(ingredients: Iterable<Ingredient>) {
    Column(Modifier.fillMaxSize()) {
        for (ingredient in ingredients) {
            Text(ingredient.toDisplayString(), Modifier.padding(8.dp))
        }
    }
}

@Composable
private fun animateFunkyColor(): State<Color> {
    var currentState by remember { mutableStateOf(false) }
    val transition = updateTransition(currentState)

    LaunchedEffect(Unit) {
        while (true) {
            delay(200)
            currentState = !currentState
        }
    }

    return transition.animateColor() { state ->
        if (state) Color.Red else Color.Blue
    }
}

@Preview
@Composable
fun RecipeScreenSimple() {
    MaterialTheme(darkColors()) {
        Surface(Modifier.fillMaxSize()) {
            RecipesScreen(
                RecipesViewModel(
                    recipeRepository = object : RecipeRepository {
                        override fun findAll(): Either<RecipeRepositoryError, Flow<Recipe>> {
                            return flowOf(
                                Recipe("Chicken", emptyList(), emptyList()),
                                Recipe("Pork", emptyList(), emptyList()),
                                Recipe("Beef", emptyList(), emptyList()),
                            ).right()
                        }
                    },
                    todoistRepository = object : TodoistSyncRepository {
                        override suspend fun addItems(commands: List<ItemAddCommand>): Either<TodoistRepositoryError, AddItemsResponse> {
                            return AddItemsResponse().right()
                        }
                    },
                    rememberCoroutineScope()
                )
            )
        }
    }
}

@Preview
@Composable
fun RecipeScreenOpened() {
    MaterialTheme(darkColors()) {
        Surface(Modifier.fillMaxSize()) {
            RecipesScreen(
                RecipesViewModel(
                    recipeRepository = object : RecipeRepository {
                        override fun findAll(): Either<RecipeRepositoryError, Flow<Recipe>> {
                            return flowOf(
                                Recipe("Chicken", emptyList(), emptyList()),
                                Recipe(
                                    "Pork",
                                    listOf(
                                        Ingredient("1", "kg", "pork"),
                                        Ingredient("15", "g", "thyme"),
                                        Ingredient("25", "g", "butter"),
                                    ),
                                    emptyList()
                                ),
                                Recipe("Beef", emptyList(), emptyList()),
                            ).right()
                        }
                    },
                    todoistRepository = object : TodoistSyncRepository {
                        override suspend fun addItems(commands: List<ItemAddCommand>): Either<TodoistRepositoryError, AddItemsResponse> {
                            return AddItemsResponse().right()
                        }
                    },
                    rememberCoroutineScope()
                ).also { recipesViewModel ->
                    recipesViewModel.recipeOptions[1] =
                        recipesViewModel.recipeOptions[1].copy(selected = true, expanded = true)
                }
            )
        }
    }
}