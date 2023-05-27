package com.example.taskbeats

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface TaskDao {

     @Insert(onConflict =  OnConflictStrategy.REPLACE)
    fun insert(task: Task)

    @Query("Select * from task")
    fun getAll():List<Task>

    //uPdate  encontrar a tarefa que queremos alterar.

    @Update (onConflict =  OnConflictStrategy.REPLACE)
    fun update (task: Task)
    //DELETE PARA ENCONTAR PRECISAMOS DO ID.
    //deletando todos
    @Query("DELETE from task")
    fun deleteAll()
    //deletando pelo id
    @Query("DELETE from task WHERE id =:id")
    fun deleteById(id:Int)
}