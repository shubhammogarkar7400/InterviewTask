package com.example.todolistinkotlin

import android.app.Application
import android.content.Context
import androidx.work.Configuration
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
 import com.example.todolistinkotlin.database.repositories.AnalyticDataRepository
import com.example.todolistinkotlin.preferences.AppPreferences
import com.example.todolistinkotlin.worker.AnalyticDataWorker
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject


@HiltAndroidApp
class ToDoApp : Application() , Configuration.Provider{

    @Inject
    lateinit var workerFactory: UploadAnalyticDataWorkerFactory



    override fun onCreate() {
        super.onCreate()

        // Initialize the AppPreferences singleton
        val appPreferences = AppPreferences.getInstance(this)

        // Record the app start timestamp
        appPreferences.setAppStartTimestamp(System.currentTimeMillis())
    }

    override fun getWorkManagerConfiguration(): Configuration {
        return Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
    }
}

class UploadAnalyticDataWorkerFactory @Inject constructor(
    private val analyticDataRepository: AnalyticDataRepository
) : WorkerFactory() {
    override fun createWorker(appContext: Context, workerClassName: String, workerParameters: WorkerParameters): ListenableWorker = AnalyticDataWorker(appContext, workerParameters = workerParameters, repository = analyticDataRepository)

}
