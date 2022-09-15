package com.github.goregius.shoppinglist

import com.github.goregius.shoppinglist.model.*

fun DomainError.toUserMessage(): String = when (this) {
    is RecipesNotFound -> "Your recipes could not be found"
    is SerializationError -> "Ooopsie we couldn't serialize something"
    is UnexpectedRecipesError -> "There was an unexpected error with our recipe department"
    is UnexpectedTodoistError -> "There was an unexpected error with our todoist department"
}