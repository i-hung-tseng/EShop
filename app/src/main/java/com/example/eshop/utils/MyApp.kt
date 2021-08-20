package com.example.eshop.utils

import android.app.Application
import com.example.eshop.BuildConfig
import timber.log.Timber

class MyApp:Application() {

    override fun onCreate() {
        super.onCreate()
        if(BuildConfig.DEBUG){
            Timber.plant(Timber.DebugTree())
        }
    }
}