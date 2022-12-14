package com.github.goregius.shoppinglist.ui.recipes

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.updateTransition
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.github.goregius.shoppinglist.extension.toDisplayString
import com.github.goregius.shoppinglist.model.recipe.Ingredient
import com.github.goregius.shoppinglist.model.recipe.Recipe
import kotlinx.coroutines.delay

@Composable
fun RecipesScreen(viewModel: RecipesViewModel) {
    Recipes(
        recipeOptions = viewModel.recipeOptions,
        onSelectRecipe = viewModel::selectRecipe,
        onExpandRecipe = viewModel::expandRecipe,
        errorMessage = viewModel.userErrorMessage,
        onDismissErrorMessage = viewModel::dismissErrorMessage,
        isAddingToShoppingList = viewModel.isAddingToShoppingList,
        addSelectionToShoppingList = viewModel::addSelectionToShoppingList,
        ingredientsCount = viewModel.ingredientsCount,
        isAddIngredientsButtonEnabled = viewModel.isAddIngredientsButtonEnabled
    )
}

@Composable
private fun Recipes(
    recipeOptions: List<RecipeOption>,
    onSelectRecipe: (index: Int) -> Unit,
    onExpandRecipe: (index: Int) -> Unit,
    errorMessage: String?,
    onDismissErrorMessage: () -> Unit,
    isAddingToShoppingList: Boolean,
    addSelectionToShoppingList: () -> Unit,
    ingredientsCount: Int,
    isAddIngredientsButtonEnabled: Boolean,
) {
    Column(Modifier.fillMaxSize().padding(8.dp)) {
        Card(Modifier.weight(1f).fillMaxWidth()) {
            RecipesList(
                recipeOptions = recipeOptions,
                onSelectRecipe = onSelectRecipe,
                onExpandRecipe = onExpandRecipe,
                errorMessage = errorMessage,
                onDismissErrorMessage = onDismissErrorMessage,
                modifier = Modifier.fillMaxSize().padding(4.dp)
            )
        }

        if (isAddingToShoppingList) {
            Spacer(Modifier.height(8.dp))
            LinearProgressIndicator(Modifier.fillMaxWidth().height(10.dp))
        }

        Spacer(Modifier.height(8.dp))

        AddIngredientsButton(
            onClick = addSelectionToShoppingList,
            modifier = Modifier.fillMaxWidth(),
            ingredientsCount = ingredientsCount,
            enabled = isAddIngredientsButtonEnabled
        )
    }
}

@Composable
private fun RecipesList(
    recipeOptions: List<RecipeOption>,
    onSelectRecipe: (index: Int) -> Unit,
    onExpandRecipe: (index: Int) -> Unit,
    errorMessage: String? = null,
    onDismissErrorMessage: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
    ) {
        val stateVertical = rememberScrollState(0)
        // LazyColumn would be preferable here but the expanding lists cause the scrollbar to bug out
        Column(Modifier.verticalScroll(stateVertical)) {
            errorMessage?.let { message ->
                Text(
                    text = message,
                    modifier = Modifier.clickable { onDismissErrorMessage() },
                    color = animateFunkyColor().value
                )
            }
            recipeOptions.forEachIndexed { index, recipeOption ->
                RecipeOptionCard(
                    recipeOption = recipeOption,
                    onSelectRecipe = {
                        onSelectRecipe(index)
                    },
                    onExpandRecipe = {
                        onExpandRecipe(index)
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

@Composable
private fun RecipeOptionCard(
    recipeOption: RecipeOption,
    onSelectRecipe: () -> Unit,
    onExpandRecipe: () -> Unit,
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
                    Checkbox(
                        checked = recipeOption.selected,
                        onCheckedChange = { onSelectRecipe() }
                    )
                    Text(recipeOption.recipe.recipeName)
                }
                IconButton(onClick = onExpandRecipe, Modifier.padding(end = 8.dp)) {
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
private fun AddIngredientsButton(
    onClick: () -> Unit,
    ingredientsCount: Int,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled
    ) {
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (ingredientsCount > 0) {
                val s = if (ingredientsCount > 1) "s" else ""
                Text("Add $ingredientsCount ingredient$s to the shopping list")
            } else {
                Text("Select a recipe")
            }
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

    return transition.animateColor { state ->
        if (state) Color.Red else Color.Blue
    }
}

@Preview
@Composable
private fun RecipesSimplePreview() {
    MaterialTheme(darkColors()) {
        Surface(Modifier.fillMaxSize()) {
            Recipes(
                recipeOptions = listOf(
                    RecipeOption(Recipe("Chicken", emptyList(), emptyList()), selected = false, expanded = false),
                    RecipeOption(Recipe("Pork", emptyList(), emptyList()), selected = true, expanded = false),
                    RecipeOption(Recipe("Beef", emptyList(), emptyList()), selected = false, expanded = false)
                ),
                onSelectRecipe = {},
                onExpandRecipe = {},
                errorMessage = null,
                onDismissErrorMessage = {},
                isAddingToShoppingList = false,
                addSelectionToShoppingList = {},
                ingredientsCount = 0,
                isAddIngredientsButtonEnabled = false
            )
        }
    }
}

@Preview
@Composable
private fun RecipeScreenOpenedPreview() {
    MaterialTheme(darkColors()) {
        Surface(Modifier.fillMaxSize()) {
            Recipes(
                recipeOptions = listOf(
                    RecipeOption(Recipe("Chicken", emptyList(), emptyList()), selected = false, expanded = false),
                    RecipeOption(
                        Recipe(
                            "Pork", listOf(
                                Ingredient("1", "kg", "pork"),
                                Ingredient("15", "g", "thyme"),
                                Ingredient("25", "g", "butter"),
                            ), emptyList()
                        ), selected = true, expanded = true
                    ),
                    RecipeOption(Recipe("Beef", emptyList(), emptyList()), selected = false, expanded = false)
                ),
                onSelectRecipe = {},
                onExpandRecipe = {},
                errorMessage = null,
                onDismissErrorMessage = {},
                isAddingToShoppingList = false,
                addSelectionToShoppingList = {},
                ingredientsCount = 3,
                isAddIngredientsButtonEnabled = true
            )
        }
    }
}

@Preview
@Composable
private fun RecipesErrorMessagePreview() {
    MaterialTheme(darkColors()) {
        Surface(Modifier.fillMaxSize()) {
            Recipes(
                recipeOptions = listOf(
                    RecipeOption(Recipe("Chicken", emptyList(), emptyList()), selected = false, expanded = false),
                    RecipeOption(Recipe("Pork", emptyList(), emptyList()), selected = true, expanded = false),
                    RecipeOption(Recipe("Beef", emptyList(), emptyList()), selected = false, expanded = false)
                ),
                onSelectRecipe = {},
                onExpandRecipe = {},
                errorMessage = "Error message",
                onDismissErrorMessage = {},
                isAddingToShoppingList = false,
                addSelectionToShoppingList = {},
                ingredientsCount = 0,
                isAddIngredientsButtonEnabled = true
            )
        }
    }
}