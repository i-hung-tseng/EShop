package com.example.eshop.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CartItem(
        val user_id: String = "",
        val product_id: String = "",
        val title: String = "",
        val price: Int? = null,
        val image: String = "",
        val cart_quantity: Int?  = null,
        var stock_quantity: Int? = null,
        var id: String = ""
): Parcelable
