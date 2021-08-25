package com.example.eshop.ui.activities

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowInsets
import android.view.WindowManager
import android.graphics.Typeface
import android.widget.TextView
import com.example.eshop.R

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
        val controller = window.insetsController
            controller?.hide(WindowInsets.Type.statusBars())
        }else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }

        Handler(Looper.getMainLooper()).postDelayed(
            {
                startActivity(Intent(this@SplashActivity, DashboardActivity::class.java))
                //finish 結束當前 Activity
             finish()
            },
        1500
        )
         val typeface: Typeface = Typeface.createFromAsset(assets,"Montserrat-Bold.ttf")
        findViewById<TextView>(R.id.tv_app_name).typeface = typeface
    }
}