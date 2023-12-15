package com.example.todolistinkotlin.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.example.todolistinkotlin.model.AnalyticsData

@Dao
interface AnalyticDataDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnalyticData(analyticData: AnalyticsData)
}
