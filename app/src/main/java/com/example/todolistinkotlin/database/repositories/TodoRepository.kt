package com.example.todolistinkotlin.database.repositories

import com.example.todolistinkotlin.model.ToDoListDataEntity

interface TodoRepository {

    fun getAll(): List<ToDoListDataEntity>

    fun update(title: String, date: String, time: String, id: Long)

    fun insert(toDoListData: ToDoListDataEntity) : Long

    fun delete(id : Long)

    fun isShownUpdate(id:Long , isShow : Int)

    fun get(id : Long): ToDoListDataEntity


}


