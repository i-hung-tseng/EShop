package com.example.eshop.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CartItem(
        val user_id: String = "",
        val product_owner_id: String = "",
        val product_id: String = "",
        val title: String = "",
        val price: Int? = null,
        val image: String = "",
        var cart_quantity: Int  = 0,
        var stock_quantity: Int = 0,
        var id: String = ""
): Parcelable
