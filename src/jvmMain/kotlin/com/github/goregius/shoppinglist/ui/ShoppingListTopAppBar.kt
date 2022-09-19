package com.github.goregius.shoppinglist.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun ShoppingListTopAppBar(
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

@Preview
@Composable
fun ShoppingListTopAppBarRecipesPreview() {
    MaterialTheme(darkColors()) {
        ShoppingListTopAppBar(Route.Recipes, {}, false, {})
    }
}
@Preview
@Composable
fun ShoppingListTopAppBarSettingsPreview() {
    MaterialTheme(darkColors()) {
        ShoppingListTopAppBar(Route.Settings, {}, true, {})
    }
}