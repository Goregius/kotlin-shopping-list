package com.github.goregius.shoppinglist.ui.settings

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.github.goregius.shoppinglist.repository.PreferenceKey
import com.github.goregius.shoppinglist.repository.PreferencesRepository

class SettingsViewModel(private val preferencesRepository: PreferencesRepository) {
    private var _todoistToken by mutableStateOf("")
    val todoistToken get() = _todoistToken

    private var _persistedTodoistToken by mutableStateOf("")

    val enableSubmitToken get() = _persistedTodoistToken != _todoistToken

    init {
        val todoistToken = preferencesRepository.getPreference(PreferenceKey.TodoistToken).orEmpty()
        _persistedTodoistToken = todoistToken
        _todoistToken = todoistToken
    }

    fun updateTodoistTokenText(text: String) {
        _todoistToken = text
    }

    fun submitTodoistToken() {
        val newTodoistToken = todoistToken.trim()

        preferencesRepository.putPreference(PreferenceKey.TodoistToken, newTodoistToken)
        _persistedTodoistToken = newTodoistToken
    }
}