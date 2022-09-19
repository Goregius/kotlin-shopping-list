package com.github.goregius.shoppinglist.ui

import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import com.github.goregius.shoppinglist.env.Dependencies
import com.github.goregius.shoppinglist.ui.recipes.RecipesScreen
import com.github.goregius.shoppinglist.ui.recipes.RecipesViewModel

@Composable
fun App(dependencies: Dependencies) {
    val coroutineScope = rememberCoroutineScope()
    val viewModel = remember {
        RecipesViewModel(
            recipeRepository = dependencies.recipeRepository,
            todoistRepository = dependencies.todoistRepository,
            coroutineScope = coroutineScope
        )
    }

    MaterialTheme(darkColors()) {
        val scaffoldState: ScaffoldState = rememberScaffoldState()
        Scaffold(scaffoldState = scaffoldState, topBar = {
            RecipesTopAppBar(
                scaffoldState,
                dependencies.preferencesRepository
            )
        }) {
            Surface {
                RecipesScreen(viewModel)
            }
        }
    }
}