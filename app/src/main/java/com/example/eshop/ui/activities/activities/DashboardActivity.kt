package com.example.eshop.ui.activities.activities

import android.content.Intent
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.eshop.R
import com.example.eshop.utils.Constants
import timber.log.Timber

class DashboardActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashborad)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        supportActionBar?.setBackgroundDrawable(ContextCompat.getDrawable(
            this@DashboardActivity,R.drawable.app_gradient_color_background
        ))



        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(setOf(
                R.id.navigation_products, R.id.navigation_dashboard, R.id.navigation_orders, R.id.navigation_sold_product))
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onBackPressed() {
        doubleBackToExit()
    }

}