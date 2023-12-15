package com.example.todolistinkotlin.database.repositories

import com.example.todolistinkotlin.database.TodoDatabase
import com.example.todolistinkotlin.model.ToDoListDataEntity
import javax.inject.Inject

class TodoRepositoryImpl @Inject constructor(private val todoDatabase: TodoDatabase) : TodoRepository {

    override fun getAll(): List<ToDoListDataEntity>{
        return todoDatabase.todoDao().getAll()
    }

    override fun update(title: String, date: String, time: String, id: Long) {
        todoDatabase.todoDao().update(title = title, date = date, time = time, id = id)
    }

    override fun insert(toDoListData: ToDoListDataEntity): Long {
         return todoDatabase.todoDao().insert(toDoListData = toDoListData)
    }

    override fun delete(id: Long) {
        todoDatabase.todoDao().Delete(id = id)
    }

    override fun isShownUpdate(id: Long, isShow: Int) {
         todoDatabase.todoDao().isShownUpdate(id = id, isShow = isShow)
    }

    override fun get(id: Long): ToDoListDataEntity {
         return todoDatabase.todoDao().get(id = id)
    }

}