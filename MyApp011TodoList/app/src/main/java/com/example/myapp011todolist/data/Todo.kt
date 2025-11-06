package com.example.myapp011todolist.data

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class Todo(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val done: Boolean = false,
    val description: String? = null,
    val imageUri: String? = null
)
