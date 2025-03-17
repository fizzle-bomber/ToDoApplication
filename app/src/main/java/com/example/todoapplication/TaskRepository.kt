package com.example.todoapplication

import androidx.lifecycle.LiveData

class TaskRepository(private val taskDao: TaskDao,
    private val categoryDao: CategoryDao) {

    val allTasks: LiveData<List<Task>> = taskDao.getAllTask()
    val allCategories: LiveData<List<Category>> = categoryDao.getAllCategories()

    suspend fun insert(task: Task) {
        taskDao.insert(task)
    }

    suspend fun update(task: Task) {
        taskDao.update(task)
    }

    suspend fun delete(taskId: Int) {
        taskDao.delete(taskId)
    }

    fun getTaskById(taskId: Int): LiveData<Task> {
        return taskDao.getTaskById(taskId)
    }

    suspend fun insertCategory(category: Category) {
        categoryDao.insert(category)
    }

    suspend fun deleteCategory(category: Category) {
        categoryDao.delete(category)
    }

}