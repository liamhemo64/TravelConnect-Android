package com.idz.travelconnect

import android.app.Application
import android.content.Context
import com.google.firebase.FirebaseApp

class TravelConnectApplication : Application() {

    companion object Globals {
        var appContext: Context? = null
    }

    override fun onCreate() {
        super.onCreate()
        // Initialize Firebase to prevent "Default FirebaseApp is not initialized" crash
        FirebaseApp.initializeApp(this)
        appContext = applicationContext
    }
}
