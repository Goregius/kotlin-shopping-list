package com.github.goregius.shoppinglist.repository

import arrow.core.Either
import arrow.core.continuations.either
import com.github.goregius.shoppinglist.Env
import com.github.goregius.shoppinglist.model.TodoistRepositoryError
import com.github.goregius.shoppinglist.model.UnexpectedTodoistError
import com.github.goregius.shoppinglist.model.todoist.ItemAddCommand
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

class TodoistSyncKtorRepository(private val json: Json, private val env: Env) : TodoistSyncRepository {
    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(json)
        }
        install(Logging) {
            level = LogLevel.ALL
        }

        defaultRequest {
            url("https://api.todoist.com/sync/v9/")
            header("Authorization", "Bearer " + env.getTodoistToken())
        }
    }

    override suspend fun addItems(commands: List<ItemAddCommand>): Either<TodoistRepositoryError, String> = either {
        Either.catch {
            client.post("sync") {
                contentType(ContentType.Application.Json)
                setBody(SyncRequest(commands))
            }.body<String>()
        }.mapLeft { e -> UnexpectedTodoistError("Failed to create an item", e) }
            .bind()
    }

    @Serializable
    private data class SyncRequest(val commands: List<ItemAddCommand>)
}
