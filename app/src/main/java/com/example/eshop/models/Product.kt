package com.example.eshop.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Product(
        val user_id: String = "",
        val user_name: String = "",
        val title: String = "",
        val price: Int = 0,
        val description: String = "",
        val quantity: Int = 0,
        val photo: String = "",
        var product_id: String = ""
): Parcelable
