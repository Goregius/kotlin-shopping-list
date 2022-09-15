package com.github.goregius.shoppinglist.repository

import arrow.core.Either
import arrow.core.continuations.either
import arrow.core.leftIfNull
import arrow.core.right
import com.github.goregius.shoppinglist.model.*
import com.github.goregius.shoppinglist.model.recipe.Recipe
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.nio.file.NotDirectoryException
import kotlin.io.path.listDirectoryEntries
import kotlin.io.path.readText
import kotlin.io.path.toPath

class RecipeFileRepository(private val json: Json) : RecipeRepository {
    override fun findAll(): Either<RecipeRepositoryError, Flow<Recipe>> = either.eager {
        val resource = object {}.javaClass.getResource("/recipes").right().leftIfNull {
            RecipesNotFound("The recipes could not be found in the '/recipes' resource folder")
        }.bind()

        val recipeFolder = resource.toURI().toPath()

        val recipePaths = Either.catch {
            recipeFolder.listDirectoryEntries("*.json")
        }.mapLeft { ex ->
            if (ex is NotDirectoryException) {
                RecipesNotFound("The recipe folder could not be found ($recipeFolder)")
            } else {
                UnexpectedRecipesError(
                    "There was an error while finding the recipes json files in ${resource.path}",
                    ex
                )
            }
        }.bind()

        recipePaths.map { path ->
            Either.catch {
                json.decodeFromString<Recipe>(path.readText()).let { it.copy(ingredients = it.ingredients.distinct()) }
            }.mapLeft { ex ->
                SerializationError("The recipe could not be serialized", ex)
            }.bind()
        }.asFlow()
    }
}