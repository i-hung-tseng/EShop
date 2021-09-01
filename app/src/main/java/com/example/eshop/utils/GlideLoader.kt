package com.example.eshop.utils

import android.content.Context
import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.example.eshop.R
import java.io.IOException

class GlideLoader(val context: Context) {

    fun loadUserPicture(imageUri: Any?, imageView: ImageView){
        try {
            Glide
                .with(context)
                .load(imageUri)
                .centerCrop()
                .placeholder(R.drawable.ic_user_placeholder)
                .into(imageView)
        }catch (e: IOException){
            e.printStackTrace()
        }
    }


    fun loadProductPicture(imageUri: Any?, imageView: ImageView){
        try {
            Glide
                    .with(context)
                    .load(imageUri)
                    .centerCrop()
                    .into(imageView)
        }catch (e: IOException){
            e.printStackTrace()
        }
    }

}