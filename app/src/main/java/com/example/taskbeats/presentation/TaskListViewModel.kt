package com.example.taskbeats.presentation

import android.app.Application
import androidx.lifecycle.ViewModel
import com.example.taskbeats.data.TaskDao
import com.example.taskbeats.taskbeatsApplication

class TaskListViewModel(private val taskDao: TaskDao) : ViewModel(){


companion object{
    fun create (application: Application): TaskListViewModel{
        val dataBaseInstance = (application as taskbeatsApplication).getAppDataBase()
        val dao = dataBaseInstance.taskDao()

        return TaskListViewModel(dao)
    }


    }
}
