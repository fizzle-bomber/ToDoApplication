package com.example.todoapplication

import android.app.Application

class TodoApplication : Application() {

    val database by lazy { TaskDatabase.getDatabase(this) }
    val repository by lazy { TaskRepository(database.taskDao()) }
}