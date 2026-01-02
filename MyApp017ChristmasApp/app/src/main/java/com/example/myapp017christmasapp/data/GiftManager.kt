package com.example.myapp017christmasapp.data

object GiftManager {
    val gifts = mutableListOf<String>()

    fun addGift(gift: String) {
        gifts.add(gift)
    }

    fun getAllGifts(): List<String> {
        return gifts.toList()
    }
    
    fun clearGifts() {
        gifts.clear()
    }
}