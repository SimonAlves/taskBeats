package com.example.taskbeats

import android.app.Application
import androidx.room.Room
import com.example.taskbeats.data.AppDataBase

class taskbeatsApplication: Application() {
   lateinit var database : AppDataBase

    override fun onCreate() {
        super.onCreate()
        database = Room.databaseBuilder(
            applicationContext,
            AppDataBase::class.java, "taskbeats-database"
        ).build()
    }
    fun getAppDataBase():AppDataBase{
        return database
    }

    }