package com.example.eshop.models

class User(
        val id: String = "",
        val firstName: String = "",
        val lastName: String = "",
        val email: String = "",
        val image: String = "",
        val mobile: Long = 0,
        val gender: String = "",
        val profileCompleted: Boolean = false
)