package com.github.goregius.shoppinglist.ui

import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.github.goregius.shoppinglist.env.Dependencies
import com.github.goregius.shoppinglist.ui.recipes.RecipesScreen
import com.github.goregius.shoppinglist.ui.recipes.RecipesViewModel
import com.github.goregius.shoppinglist.ui.settings.SettingsScreen
import com.github.goregius.shoppinglist.ui.settings.SettingsViewModel

sealed interface Route {
    object Recipes : Route
    object Settings : Route
}

class Navigation(homeRoute: Route) {
    private val _routes = mutableStateListOf(homeRoute)

    val currentRoute by derivedStateOf { _routes.last() }

    val hasBackNavigate by derivedStateOf { _routes.count() > 1 }

    fun navigateTo(route: Route) {
        _routes.add(route)
    }

    fun navigateBack() {
        if (_routes.count() > 1) {
            _routes.removeLast()
        }
    }
}

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
                TopAppBar(
                    route = navigation.currentRoute,
                    onSettingsClick = {
                        navigation.navigateTo(Route.Settings)
                    },
                    hasBackNavigation = navigation.hasBackNavigate,
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

@Composable
private fun TopAppBar(
    route: Route,
    onSettingsClick: () -> Unit,
    hasBackNavigation: Boolean,
    onBackClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    TopAppBar(
        modifier = modifier,
        title = {
            val text = when (route) {
                Route.Recipes -> "Recipes"
                Route.Settings -> "Settings"
            }
            Text(text)
        },
        navigationIcon = if (hasBackNavigation) {
            {
                IconButton(onClick = onBackClicked) {
                    Icon(Icons.Default.ArrowBack, "Navigate back")
                }
            }
        } else {
            null
        },
        actions = {
            when (route) {
                Route.Recipes -> {
                    IconButton(onClick = onSettingsClick) {
                        Icon(Icons.Default.Settings, "Settings")
                    }
                }

                else -> {}
            }
        }
    )
}