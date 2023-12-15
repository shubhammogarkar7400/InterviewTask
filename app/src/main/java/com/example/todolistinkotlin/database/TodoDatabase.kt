package com.example.todolistinkotlin.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.todolistinkotlin.model.AnalyticsData
import com.example.todolistinkotlin.model.ToDoListDataEntity


@Database(entities = [ToDoListDataEntity::class, AnalyticsData::class], version = 3)
abstract class TodoDatabase : RoomDatabase() {
    abstract fun todoDao(): ToDoListDAO
    abstract fun analyticDataDao(): AnalyticDataDao

}
