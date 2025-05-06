package com.example.hashbrownzwithsalt

import android.app.Application
import androidx.room.Room

class HashBrownzWithSalt: Application() {
    companion object {
        lateinit var db: AppDatabase
    }

    override fun onCreate() {
        super.onCreate()
        db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "UserDb-4").build()
    }
}