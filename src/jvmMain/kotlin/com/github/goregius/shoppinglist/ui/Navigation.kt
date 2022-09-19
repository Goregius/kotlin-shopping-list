package com.github.goregius.shoppinglist.ui

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf

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