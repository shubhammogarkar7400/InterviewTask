package com.example.todolistinkotlin.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.todolistinkotlin.database.repositories.AnalyticDataRepository
import com.example.todolistinkotlin.model.AnalyticsData
import com.example.todolistinkotlin.util.Constants.ANALYTIC_DATA_ARG
import com.google.gson.Gson

class AnalyticDataWorker(
    private val context: Context,
    private val workerParameters: WorkerParameters,
    private val repository: AnalyticDataRepository
) : CoroutineWorker(context, workerParameters) {

    // Perform the analytic data insertion here
    override suspend fun doWork(): Result {

        // Retrieve the serialized analytics data from input data
        val analyticsDataString = workerParameters.inputData.getString(ANALYTIC_DATA_ARG)

        // Check if the analytics data is not null
        return if (analyticsDataString != null){
            val analyticsData = Gson().fromJson(analyticsDataString, AnalyticsData::class.java)

            // Check if deserialization was successful
            if (analyticsData != null){
                try {
                    /*
                     Here you can Implement secure data transmission using the HTTPS protocol for enhanced security.
                     I suggest the use of Retrofit, a powerful library for simplifying API communication in Android.
                     because the actual API endpoint is not provided, so I stored the analytic data to room database instead of actual server.
                     */
                    repository.insertAnalyticData(analyticData = analyticsData)
                    Result.success()
                }catch (e: Exception){
                    // Handle any exceptions that occur during analytic data upload
                    Log.e("apiError", e.message.toString())
                    Result.failure()
                }
            }else{
                // Deserialization failed, return failure
                Result.failure()
            }
        }else{
            // Analytics data not provided, return failure
            Result.failure()
        }
    }
}