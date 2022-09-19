package com.github.goregius.shoppinglist.repository

import arrow.core.Either
import arrow.core.continuations.either
import arrow.core.leftIfNull
import arrow.core.right
import com.github.goregius.shoppinglist.model.RecipeRepositoryError
import com.github.goregius.shoppinglist.model.RecipesNotFound
import com.github.goregius.shoppinglist.model.SerializationError
import com.github.goregius.shoppinglist.model.UnexpectedRecipesError
import com.github.goregius.shoppinglist.model.recipe.Recipe
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.net.URI
import java.net.URL
import java.nio.file.FileSystemNotFoundException
import java.nio.file.FileSystems
import java.nio.file.NotDirectoryException
import java.nio.file.Path
import kotlin.io.path.listDirectoryEntries
import kotlin.io.path.readText
import kotlin.io.path.toPath

class RecipeFileRepository(private val json: Json) : RecipeRepository {
    override fun findAll(): Either<RecipeRepositoryError, Flow<Recipe>> = either.eager {
        val recipesUrl = findRecipesUrl().bind()
        val dirPath = findPathFromUri(recipesUrl.toURI()).bind()
        val recipePaths = findRecipePaths(dirPath).bind()
        decodeRecipePaths(recipePaths).bind().asFlow()
    }

    private fun findRecipesUrl(): Either<RecipesNotFound, URL> =
        RecipeFileRepository::class.java.getResource("/recipes/").right().leftIfNull {
            RecipesNotFound("The recipes could not be found in the '/recipes' resource folder")
        }

    private fun findPathFromUri(uri: URI) = Either.catch {
        try {
            uri.toPath()
        } catch (e: FileSystemNotFoundException) {
            // If this is thrown, then it means that we are running the JAR directly (example: not from an IDE)
            val env = mutableMapOf<String, String>()
            FileSystems.newFileSystem(uri, env).getPath("/recipes/")
        }
    }.mapLeft { ex ->
        UnexpectedRecipesError("There was an error while trying to find recipe path", ex)
    }

    private fun findRecipePaths(dirPath: Path) = Either.catch {
        dirPath.listDirectoryEntries("*.json")
    }.mapLeft { ex ->
        if (ex is NotDirectoryException) {
            RecipesNotFound("The recipe folder could not be found ($dirPath)")
        } else {
            UnexpectedRecipesError(
                "There was an error while finding the recipes json files in $dirPath",
                ex
            )
        }
    }

    private fun decodeRecipePaths(recipePaths: List<Path>): Either<RecipeRepositoryError, List<Recipe>> = either.eager {
        recipePaths.map { path ->
            Either.catch {
                json.decodeFromString<Recipe>(path.readText()).let { it.copy(ingredients = it.ingredients.distinct()) }
            }.mapLeft { ex ->
                SerializationError("The recipe could not be serialized", ex)
            }.bind()
        }
    }
}