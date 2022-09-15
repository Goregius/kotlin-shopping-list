package com.github.goregius.shoppinglist.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.updateTransition
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
import com.github.goregius.shoppinglist.extension.toDisplayString
import kotlinx.coroutines.delay

@Composable
fun funkyColor(): State<Color> {
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

@Composable
fun Recipes(state: RecipesViewModel) {
    val recipeOptions = state.recipeOptions

    Column(Modifier.fillMaxSize().padding(8.dp)) {
        Card(Modifier.weight(1f).fillMaxWidth()) {
            Column {
                Box(
                    modifier = Modifier.fillMaxSize().padding(4.dp)
                ) {
                    val stateVertical = rememberScrollState(0)
                    // LazyColumn would be preferable here but the expanding lists cause the scrollbar to bug out
                    Column(Modifier.verticalScroll(stateVertical)) {
                        state.userErrorMessage?.let {
                            Text(it, color = funkyColor().value)
                        }
                        recipeOptions.forEachIndexed { index, recipeOption ->
                            Card(Modifier.fillMaxWidth().padding(2.dp)) {
                                Column(Modifier.fillMaxSize()) {
                                    Row(
                                        modifier = Modifier.fillMaxSize(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                                            Checkbox(recipeOption.selected, {
                                                recipeOptions[index] = recipeOption.copy(selected = it)
                                            })
                                            Text(recipeOption.recipe.recipeName)
                                        }
                                        IconButton(onClick = {
                                            recipeOptions[index] =
                                                recipeOption.copy(expanded = !recipeOption.expanded)
                                        }, Modifier.padding(end = 8.dp)) {
                                            if (recipeOptions[index].expanded) {
                                                Icon(Icons.Default.KeyboardArrowUp, "Save")
                                            } else {
                                                Icon(Icons.Default.KeyboardArrowDown, "Save")

                                            }
                                        }
                                    }
                                    AnimatedVisibility(recipeOption.expanded) {
                                        Card(Modifier.fillMaxWidth().padding(8.dp)) {
                                            Column(Modifier.fillMaxSize()) {
                                                for (ingredient in recipeOption.recipe.ingredients) {
                                                    Text(ingredient.toDisplayString(), Modifier.padding(8.dp))
                                                }
                                            }
                                        }
                                    }
                                }
                            }
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

        if (state.isAddingToShoppingList.value) {
            Spacer(Modifier.height(8.dp))
            LinearProgressIndicator(Modifier.fillMaxWidth().height(10.dp))
        }

        Spacer(Modifier.height(8.dp))

        Button(
            onClick = {
                state.addSelectionToShoppingList()
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !state.isAddingToShoppingList.value && state.recipeOptions.any { it.selected }
        ) {
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                if (state.ingredientsCount.value > 0) {
                    val s = if (state.ingredientsCount.value > 1) "s" else ""
                    Text("Add ${state.ingredientsCount.value} ingredient$s to the shopping list")
                } else {
                    Text("Select a recipe")
                }
            }
        }
    }

}