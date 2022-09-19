package com.github.goregius.shoppinglist.model.todoist

import kotlinx.serialization.Required
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class ItemAddCommand(val args: ItemAddArgs) {
    @Suppress("unused")
    @Required
    val type = "item_add"

    @Suppress("unused")
    @Required
    @SerialName("temp_id")
    val tempId: String = UUID.randomUUID().toString()

    @Suppress("unused")
    @Required
    val uuid: String = UUID.randomUUID().toString()
}

@Serializable
data class ItemAddArgs(
    val content: String,
    val description: String? = null,
    @SerialName("project_id")
    val projectId: String? = null,
)

@Serializable
data class AddItemsResponse(override val error: String? = null): ErrorResponse