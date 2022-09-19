package com.github.goregius.shoppinglist.ui

sealed interface Route {
    object Recipes : Route
    object Settings : Route
}