package com.idz.travelconnect

import android.app.Application
import com.google.firebase.FirebaseApp

class TravelConnectApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize Firebase to prevent "Default FirebaseApp is not initialized" crash
        FirebaseApp.initializeApp(this)
    }
}
