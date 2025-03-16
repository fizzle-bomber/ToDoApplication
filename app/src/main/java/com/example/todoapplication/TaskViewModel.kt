package com.example.todoapplication

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class TaskViewModel(private val taskRepository: TaskRepository) : ViewModel() {

    val allTasks: LiveData<List<Task>> = taskRepository.allTasks

    fun insert(task: Task) = viewModelScope.launch {
        taskRepository.insert(task)
    }

    fun update(task: Task) = viewModelScope.launch {
        taskRepository.update(task)
    }

    fun delete(taskId: Int) = viewModelScope.launch {
        taskRepository.delete(taskId)
    }
}