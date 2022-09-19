package com.github.goregius.shoppinglist.ui

import androidx.compose.material.*
import androidx.compose.runtime.*
import com.github.goregius.shoppinglist.env.Dependencies
import com.github.goregius.shoppinglist.ui.recipes.RecipesScreen
import com.github.goregius.shoppinglist.ui.recipes.RecipesViewModel
import com.github.goregius.shoppinglist.ui.settings.SettingsScreen
import com.github.goregius.shoppinglist.ui.settings.SettingsViewModel

@Composable
fun App(dependencies: Dependencies) {
    val coroutineScope = rememberCoroutineScope()
    val recipesViewModel = remember {
        RecipesViewModel(
            recipeRepository = dependencies.recipeRepository,
            todoistRepository = dependencies.todoistRepository,
            coroutineScope = coroutineScope
        )
    }
    val settingsViewModel = remember {
        SettingsViewModel(preferencesRepository = dependencies.preferencesRepository)
    }

    val navigation = remember { Navigation(Route.Recipes) }

    MaterialTheme(darkColors()) {
        val scaffoldState: ScaffoldState = rememberScaffoldState()

        Scaffold(
            scaffoldState = scaffoldState,
            topBar = {
                ShoppingListTopAppBar(
                    route = navigation.currentRoute,
                    onSettingsClick = {
                        navigation.navigateTo(Route.Settings)
                    },
                    hasBackNavigation = navigation.hasBackNavigation,
                    onBackClicked = { navigation.navigateBack() }
                )
            }
        ) {
            Surface {
                when (navigation.currentRoute) {
                    Route.Recipes -> {
                        RecipesScreen(recipesViewModel)
                    }

                    Route.Settings -> {
                        SettingsScreen(settingsViewModel)
                    }
                }
            }
        }
    }
}