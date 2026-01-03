package com.example.semestralapp.data

import com.google.firebase.firestore.DocumentId

/**
 * Datová třída reprezentující jeden úkol v databázi Firestore
 * Firestore automaticky mapuje dokumenty na objekty této třídy, pokud se názvy polí shodují
 */
data class Task(
    // @DocumentId enšuruje, že se do týhle proměnný uloží ID dokumentu z dbs (neukládá se jako pole uvnitř dokumentu)
    @DocumentId
    val id: String = "",
    
    // Title úkolu
    val title: String = "",
    
    // ID kategorie, ke které úkol patří (výchozí je "default", neboli "Vše")
    val categoryId: String = "default",
    
    // Podrobnější popis úkolu
    val note: String = "",
    
    // Fotka uložená jako Base64 řetězec (text), aby se nemusela řešit externí Storage
    val photoUrl: String? = null,
    
    // Stav splnění úkolu
    val completed: Boolean = false,
    
    // Čas vytvoření pro řazení (timestamp)
    val timestamp: Long = System.currentTimeMillis()
)