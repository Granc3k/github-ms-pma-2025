package com.example.myapp011todolist.data

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.FileNotFoundException

class TodoStore(private val context: Context) {
    private val fileName = "todos.json"
    private val json = Json { prettyPrint = true; ignoreUnknownKeys = true }

    suspend fun load(): List<Todo> = withContext(Dispatchers.IO) {
        try {
            context.openFileInput(fileName).use { fis ->
                val text = fis.bufferedReader().readText()
                if (text.isBlank()) emptyList() else json.decodeFromString(text)
            }
        } catch (_: FileNotFoundException) {
            emptyList()
        }
    }

    suspend fun save(items: List<Todo>) = withContext(Dispatchers.IO) {
        val text = json.encodeToString(items)
        context.openFileOutput(fileName, Context.MODE_PRIVATE).use {
            it.write(text.toByteArray())
        }
    }
}
