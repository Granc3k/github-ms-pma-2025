package com.example.myapp014asharedtasklist

data class Task(
    val id: String = "", // Firestore document id
    val title: String = "",
    val completed: Boolean = false
)
