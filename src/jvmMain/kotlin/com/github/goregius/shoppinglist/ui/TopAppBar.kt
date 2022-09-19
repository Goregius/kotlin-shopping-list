package com.github.goregius.shoppinglist.ui

import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun TopAppBar(
    route: Route,
    onSettingsClick: () -> Unit,
    hasBackNavigation: Boolean,
    onBackClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    androidx.compose.material.TopAppBar(
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