package com.example.semestralapp.data

import com.google.firebase.firestore.DocumentId

data class Task(
    @DocumentId
    val id: String = "",
    val title: String = "",
    val categoryId: String = "default", // "default" will be "Vše" or unassigned
    val note: String = "",
    val photoUrl: String? = null,
    val completed: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)