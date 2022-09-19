package com.github.goregius.shoppinglist.ui.settings

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.github.goregius.shoppinglist.repository.PreferenceKey
import com.github.goregius.shoppinglist.repository.PreferencesRepository

class SettingsViewModel(private val preferencesRepository: PreferencesRepository) {
    private var _todoistToken by mutableStateOf("")
    val todoistToken get() = _todoistToken

    init {
        val todoistToken = preferencesRepository.getPreference(PreferenceKey.TodoistToken).orEmpty()
        _todoistToken = todoistToken
    }

    fun updateTodoistTokenText(text: String) {
        _todoistToken = text
    }

    fun submitTodoistToken() {
        if (todoistToken.isNotBlank()) {
            preferencesRepository.putPreference(PreferenceKey.TodoistToken, todoistToken.trim())
        }
    }
}