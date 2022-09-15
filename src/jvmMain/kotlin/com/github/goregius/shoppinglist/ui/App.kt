package com.github.goregius.shoppinglist.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.window.WindowDraggableArea
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.FrameWindowScope
import androidx.compose.ui.window.WindowPlacement
import com.github.goregius.shoppinglist.Dependencies
import com.github.goregius.shoppinglist.Env
import com.github.goregius.shoppinglist.model.recipe.Recipe
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.prefs.Preferences

data class RecipeOption(val recipe: Recipe, val selected: Boolean, val expanded: Boolean)

@Composable
fun FrameWindowScope.App(
    dependencies: Dependencies,
    onClose: () -> Unit,
    windowPlacement: WindowPlacement,
    onUpdateWindowState: (WindowPlacement) -> Unit,
) {
    val recipeRepository = dependencies.recipeRepository
    val coroutineScope = rememberCoroutineScope()
    val viewModel = remember { RecipesViewModel(recipeRepository, dependencies.todoistRepository, coroutineScope) }
    val scaffoldState: ScaffoldState = rememberScaffoldState()

    MaterialTheme(darkColors()) {
        Scaffold(modifier = Modifier.clip(RoundedCornerShape(8.dp)), scaffoldState = scaffoldState, topBar = {
            WindowDraggableArea {
                Box(Modifier.fillMaxWidth().height(48.dp).background(Color.DarkGray))

                TopAppBar(title = { Text(text = "Recipes") }, actions = {
                    var isDropdownVisible by remember { mutableStateOf(false) }
                    var isSettingsIconEnabled by remember { mutableStateOf(true) }
                    val scope = rememberCoroutineScope()

                    IconButton(onClick = {
                        if (isSettingsIconEnabled) {
                            isDropdownVisible = true
                        }
                    }) {
                        Icon(Icons.Default.Settings, "Settings")
                    }

                    DropdownMenu(isDropdownVisible, onDismissRequest = {
                        isDropdownVisible = !isDropdownVisible
                        scope.launch {
                            isSettingsIconEnabled = false
                            delay(200)
                            isSettingsIconEnabled = true
                        }
                    }) {
                        var token by remember { mutableStateOf(dependencies.env.getTodoistToken()) }


                        Text("Todoist token:", Modifier.padding(horizontal = 16.dp))

                        Spacer(Modifier.height(8.dp))

                        Row(Modifier.fillMaxWidth()) {
                            OutlinedTextField(token, {
                                token = it
                            }, Modifier.padding(horizontal = 16.dp))
                            IconButton(onClick = {
                                isDropdownVisible = false

                                val initialToken = dependencies.env.getTodoistToken()
                                if (initialToken == token) return@IconButton

                                Preferences.userNodeForPackage(Env::class.java).put("todoist_token", token)

                                scope.launch {
                                    val result = scaffoldState.snackbarHostState.showSnackbar(
                                        message = "Todoist token saved",
                                        actionLabel = "Undo"
                                    )

                                    when (result) {
                                        SnackbarResult.Dismissed -> {}
                                        SnackbarResult.ActionPerformed -> {
                                            Preferences.userNodeForPackage(Env::class.java)
                                                .put("todoist_token", initialToken)
                                        }
                                    }
                                }
                            }) {
                                Icon(Icons.Default.Done, "Save")
                            }
                        }
                        Spacer(Modifier.height(4.dp))
                    }
                            IconButton(onClick = {
                                when (windowPlacement) {
                                    WindowPlacement.Floating -> onUpdateWindowState(WindowPlacement.Maximized)
                                    WindowPlacement.Maximized -> onUpdateWindowState(WindowPlacement.Floating)
                                    WindowPlacement.Fullscreen -> onUpdateWindowState(WindowPlacement.Floating)
                                }
                            }) {
                                when (windowPlacement) {
                                    WindowPlacement.Floating -> Icon(Icons.Default.KeyboardArrowUp, "Maximize")
                                    WindowPlacement.Maximized -> Icon(Icons.Default.KeyboardArrowDown, "Float")
                                    WindowPlacement.Fullscreen -> Icon(Icons.Default.KeyboardArrowDown, "Float")
                                }

                            }

                            IconButton(onClick = {
                                onClose()
                            }) {
                                Icon(Icons.Default.Close, "Close")
                            }
                })
            }
        }) {
            Surface {
                Recipes(viewModel)
            }
        }
    }
}