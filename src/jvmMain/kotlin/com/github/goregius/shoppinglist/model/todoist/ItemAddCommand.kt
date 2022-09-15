package com.github.goregius.shoppinglist.model.todoist

import kotlinx.serialization.Required
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class ItemAddCommand(val args: ItemAddArgs) {
    @Required
    val type = "item_add"

    @Required
    @SerialName("temp_id")
    val tempId: String = UUID.randomUUID().toString()

    @Required
    val uuid: String = UUID.randomUUID().toString()
}

fun typeOfIt(): String {
    return "project_id"
}

@Serializable
data class ItemAddArgs(
    val content: String,
    val description: String? = null,
    @SerialName("project_id")
    val projectId: String? = null,
)
