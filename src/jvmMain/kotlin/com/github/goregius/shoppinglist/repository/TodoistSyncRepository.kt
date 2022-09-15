package com.github.goregius.shoppinglist.repository

import arrow.core.Either
import com.github.goregius.shoppinglist.model.TodoistRepositoryError
import com.github.goregius.shoppinglist.model.todoist.ItemAddCommand

interface TodoistSyncRepository {
    suspend fun addItems(commands: List<ItemAddCommand>): Either<TodoistRepositoryError, String>
}