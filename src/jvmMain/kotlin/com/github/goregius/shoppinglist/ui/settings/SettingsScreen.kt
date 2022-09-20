package com.github.goregius.shoppinglist.ui.settings

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SettingsScreen(settingsViewModel: SettingsViewModel) {
    Settings(
        todoistToken = settingsViewModel.todoistToken,
        onUpdateTodoistToken = settingsViewModel::updateTodoistTokenText,
        onSubmitTodoistToken = settingsViewModel::submitTodoistToken,
        enableSubmitToken = settingsViewModel.enableSubmitToken
    )
}

@Composable
private fun Settings(
    todoistToken: String,
    onUpdateTodoistToken: (String) -> Unit,
    onSubmitTodoistToken: () -> Unit,
    enableSubmitToken: Boolean,
) {
    Column(Modifier.fillMaxSize().padding(8.dp).padding(top = 8.dp)) {
        Text("Todoist token:")

        Spacer(Modifier.height(8.dp))

        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = todoistToken,
                onValueChange = onUpdateTodoistToken,
                placeholder = { Text("Please enter a Todoist token") }
            )
            Spacer(Modifier.width(4.dp))

            Button(onClick = onSubmitTodoistToken, enabled = enableSubmitToken) {
                Text("Submit")
            }
        }
    }
}

@Preview
@Composable
private fun SettingsPreview() {
    MaterialTheme(darkColors()) {
        Surface {
            Settings("token text", {}, {}, true)
        }
    }
}

@Preview
@Composable
private fun SettingsEmptyPreview() {
    MaterialTheme(darkColors()) {
        Surface {
            Settings("", {}, {}, true)
        }
    }
}

@Preview
@Composable
private fun SettingsSubmitNotEnabledPreview() {
    MaterialTheme(darkColors()) {
        Surface {
            Settings("token text", {}, {}, false)
        }
    }
}