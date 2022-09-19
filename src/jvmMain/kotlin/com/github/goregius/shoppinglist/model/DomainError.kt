package com.github.goregius.shoppinglist.model

sealed interface DomainError

sealed interface RecipeRepositoryError : DomainError
data class RecipesNotFound(val description: String) : RecipeRepositoryError
data class UnexpectedRecipesError(val description: String, val error: Throwable) : RecipeRepositoryError

sealed interface TodoistRepositoryError : DomainError

data class TodoistResponseError(val error: String) : TodoistRepositoryError
data class UnexpectedTodoistError(val description: String, val error: Throwable) : TodoistRepositoryError

data class SerializationError(val description: String, val error: Throwable) : DomainError, RecipeRepositoryError