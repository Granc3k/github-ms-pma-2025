package com.example.semestralapp.data

import com.google.firebase.firestore.DocumentId

/**
 * Datová třída reprezentující kategorii úkolů.
 */
data class Category(
    // ID kategorie (data z Firestoru)
    @DocumentId
    val id: String = "",
    
    // Name kategorie (např. "Škola", "Nákup", atd.)
    val name: String = ""
)