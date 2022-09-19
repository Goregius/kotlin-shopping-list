package com.github.goregius.shoppinglist.repository

import com.github.goregius.shoppinglist.PreferencesReference
import java.util.prefs.Preferences

enum class PreferenceKey(val value: String) {
    TodoistToken("todoist_token")
}

interface PreferencesRepository {
    fun getPreference(key: PreferenceKey): String?
    fun putPreference(key: PreferenceKey, value: String)
}

class PreferencesRepositoryImpl : PreferencesRepository {
    private val preferences = Preferences.userNodeForPackage(PreferencesReference::class.java)

    override fun getPreference(key: PreferenceKey): String? = preferences.get(key.value, null)

    override fun putPreference(key: PreferenceKey, value: String) {
        preferences.put(key.value, value)
    }
}