package com.example.eshop.ui.activities.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.eshop.R
import com.example.eshop.utils.Constants
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val sharedPreferences = getSharedPreferences(Constants.MYSHOPPAL_PREFERENCES, MODE_PRIVATE)
        val userName = sharedPreferences.getString(Constants.LOGGED_IN_USERNAME,"")
        tv_main.text = "Hello $userName"

    }
}