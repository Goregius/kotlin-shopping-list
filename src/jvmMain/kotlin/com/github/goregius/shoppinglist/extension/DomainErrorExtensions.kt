package com.github.goregius.shoppinglist.extension

import com.github.goregius.shoppinglist.model.*

/**
 * Converts a [DomainError] to a user suitable message.
 */
fun DomainError.toUserMessage(): String = when (this) {
    is RecipesNotFound -> "Your recipes could not be found"
    is SerializationError -> "Ooopsie we couldn't serialize something"
    is UnexpectedRecipesError -> "There was an unexpected error with our recipe department"
    is UnexpectedTodoistError -> "There was an unexpected error with our todoist department"
    is TodoistResponseError -> "There was an error with a todoist request: $error"
}