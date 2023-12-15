package com.example.todolistinkotlin.database.repositories

import com.example.todolistinkotlin.database.AnalyticDataDao
import com.example.todolistinkotlin.model.AnalyticsData

class AnalyticDataRepository(private val analyticDataDao: AnalyticDataDao) {
    suspend fun insertAnalyticData(analyticData: AnalyticsData) {
        analyticDataDao.insertAnalyticData(analyticData)
    }
}
