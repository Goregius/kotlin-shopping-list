package com.github.goregius.shoppinglist.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.goregius.shoppinglist.PreferencesReference
import com.github.goregius.shoppinglist.repository.PreferenceKey
import com.github.goregius.shoppinglist.repository.PreferencesRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.prefs.Preferences

@Composable
fun RecipesTopAppBar(
    scaffoldState: ScaffoldState,
    preferencesRepository: PreferencesRepository,
    modifier: Modifier = Modifier,
) {
    var initialToken = remember { preferencesRepository.getPreference(PreferenceKey.TodoistToken).orEmpty() }
    var currentToken by remember { mutableStateOf(initialToken) }

    val coroutineScope = rememberCoroutineScope()

    TopAppBar(modifier = modifier, title = { Text(text = "Recipes") }, actions = {
        var isDropdownVisible by remember { mutableStateOf(false) }
        var isSettingsIconEnabled by remember { mutableStateOf(true) }

        IconButton(onClick = {
            if (isSettingsIconEnabled) {
                isDropdownVisible = true
            }
        }) {
            Icon(Icons.Default.Settings, "Settings")
        }

        TodoistDropdown(
            expanded = isDropdownVisible,
            token = currentToken,
            onUpdateTodoistToken = {
                currentToken = it
            },
            onDismissRequest = {
                isDropdownVisible = !isDropdownVisible

                coroutineScope.launch {
                    isSettingsIconEnabled = false
                    delay(200)

                    // Placed after delay to avoid text from visibly changing before dropdown is not visible
                    currentToken = initialToken

                    isSettingsIconEnabled = true
                }
            }, onSubmit = {
                isDropdownVisible = false

                if (initialToken == currentToken) return@TodoistDropdown

                preferencesRepository.putPreference(PreferenceKey.TodoistToken, currentToken)

                coroutineScope.launch {
                    val result = scaffoldState.snackbarHostState.showSnackbar(
                        message = "Todoist token saved",
                        actionLabel = "Undo"
                    )

                    when (result) {
                        SnackbarResult.Dismissed -> {
                            initialToken = currentToken
                        }

                        SnackbarResult.ActionPerformed -> {
                            Preferences.userNodeForPackage(PreferencesReference::class.java)
                                .put("todoist_token", initialToken)
                            currentToken = initialToken
                        }
                    }
                }
            }
        )
    })
}

@Composable
fun TodoistDropdown(
    expanded: Boolean,
    token: String,
    onUpdateTodoistToken: (String) -> Unit,
    onDismissRequest: () -> Unit,
    onSubmit: () -> Unit,
    modifier: Modifier = Modifier,
) {
    DropdownMenu(modifier = modifier, expanded = expanded, onDismissRequest = onDismissRequest) {
        Text("Todoist token:", Modifier.padding(horizontal = 16.dp))

        Spacer(Modifier.height(8.dp))

        Row(Modifier.fillMaxWidth()) {
            OutlinedTextField(token, {
                onUpdateTodoistToken(it)
            }, Modifier.padding(horizontal = 16.dp))

            IconButton(onClick = {
                onSubmit()
            }) {
                Icon(Icons.Default.Done, "Save")
            }
        }
        Spacer(Modifier.height(4.dp))
    }
}

@Preview
@Composable
private fun RecipesTopAppBar() {
    MaterialTheme(darkColors()) {
        RecipesTopAppBar(rememberScaffoldState(), object : PreferencesRepository {
            override fun getPreference(key: PreferenceKey): String = "token"
            override fun putPreference(key: PreferenceKey, value: String) {}
        })
    }
}