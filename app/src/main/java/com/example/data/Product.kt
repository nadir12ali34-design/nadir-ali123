package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class Product(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String,
    val price: Double,
    val category: String, // "Tech", "Fashion", "Home", "Books", "Sports", "Other"
    val condition: String, // "New", "Like New", "Good", "Fair"
    val status: String = "Active", // "Active", "Sold"
    val contactInfo: String,
    val timestamp: Long = System.currentTimeMillis()
)
