package com.example.todoapplication

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface TaskDao {
    @Insert
    suspend fun insert(task: Task)

    @Update
    suspend fun update(task: Task)

    @Query("Delete from tasks where id = :taskId")
    suspend fun delete(taskId: Int)

    @Query("Select * from tasks order by id Desc")
    fun getAllTask(): LiveData<List<Task>>
}