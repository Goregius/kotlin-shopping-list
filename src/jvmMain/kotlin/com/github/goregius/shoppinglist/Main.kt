package com.github.goregius.shoppinglist

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.github.goregius.shoppinglist.repository.RecipeFileRepository
import com.github.goregius.shoppinglist.repository.RecipeRepository
import com.github.goregius.shoppinglist.repository.TodoistSyncKtorRepository
import com.github.goregius.shoppinglist.repository.TodoistSyncRepository
import com.github.goregius.shoppinglist.ui.App
import kotlinx.serialization.json.Json
import java.awt.Color
import java.util.prefs.Preferences
import javax.swing.JFrame
import javax.swing.SwingUtilities
import javax.swing.UIManager
import javax.swing.plaf.ColorUIResource


class Dependencies(
    val recipeRepository: RecipeRepository,
    val todoistRepository: TodoistSyncRepository,
    val env: Env,
)

class Env {
    fun getTodoistToken(): String = Preferences.userNodeForPackage(Env::class.java).get("todoist_token", "")
}

fun main() {
    val env = Env()
    val recipeRepository = RecipeFileRepository(Json {
        ignoreUnknownKeys = true
    })
    val todoistRepository = TodoistSyncKtorRepository(Json {
        ignoreUnknownKeys = true
    }, env)
    val dependencies = Dependencies(recipeRepository, todoistRepository, env)

    application {
        val state = rememberWindowState(size = DpSize(800.dp, 1000.dp))
        var isOpen by remember { mutableStateOf(true) }

        if (isOpen) {
            Window(
                title = "Shopping List",
                state = state,
                onCloseRequest = ::exitApplication,
                undecorated = true,
                transparent = true
            ) {
                App(dependencies, { isOpen = false }, state.placement, { state.placement = it })
            }
        }
    }
}