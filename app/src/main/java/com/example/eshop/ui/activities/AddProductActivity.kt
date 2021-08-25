package com.example.eshop.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.example.eshop.R

class AddProductActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_product)

        supportActionBar?.setBackgroundDrawable(ContextCompat.getDrawable(
                this@AddProductActivity,R.drawable.app_gradient_color_background
        ))

    }
}