package com.github.goregius.shoppinglist

import androidx.compose.ui.awt.ComposePanel
import com.formdev.flatlaf.FlatDarculaLaf
import com.github.goregius.shoppinglist.env.Dependencies
import com.github.goregius.shoppinglist.repository.PreferencesRepositoryImpl
import com.github.goregius.shoppinglist.repository.RecipeFileRepository
import com.github.goregius.shoppinglist.repository.TodoistSyncKtorRepository
import com.github.goregius.shoppinglist.ui.App
import kotlinx.serialization.json.Json
import java.awt.BorderLayout
import java.awt.Color
import java.awt.Point
import javax.swing.JFrame
import javax.swing.SwingUtilities
import javax.swing.WindowConstants

fun main() {
    val preferencesRepository = PreferencesRepositoryImpl()

    val recipeRepository = RecipeFileRepository(Json {
        ignoreUnknownKeys = true
    })
    val todoistRepository = TodoistSyncKtorRepository(Json {
        ignoreUnknownKeys = true
    }, preferencesRepository)

    val dependencies = Dependencies(recipeRepository, todoistRepository, preferencesRepository)

    // Required to change the title bar colours
    FlatDarculaLaf.setup()

    SwingUtilities.invokeLater {
        JFrame.setDefaultLookAndFeelDecorated(true)

        val window = JFrame()
        window.location = Point(200, 200)
        window.rootPane.putClientProperty("JRootPane.titleBarBackground", Color(50, 50, 50))
        window.rootPane.putClientProperty("JRootPane.titleBarForeground", Color.white)
        window.defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE

        val composePanel = ComposePanel()
        window.contentPane.add(composePanel, BorderLayout.CENTER)

        // setting the content
        composePanel.setContent {
            App(dependencies)
        }

        window.setSize(800, 1100)
        window.isVisible = true
    }
}