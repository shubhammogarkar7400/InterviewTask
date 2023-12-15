package com.example.todolistinkotlin.di

import android.content.Context
import androidx.room.Room
import com.example.todolistinkotlin.database.AnalyticDataDao
import com.example.todolistinkotlin.database.ToDoListDAO
import com.example.todolistinkotlin.database.TodoDatabase
import com.example.todolistinkotlin.database.repositories.AnalyticDataRepository
import com.example.todolistinkotlin.database.repositories.TodoRepository
import com.example.todolistinkotlin.database.repositories.TodoRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    // Provides the application context
    @Provides
    @Singleton
    fun provideContext(@ApplicationContext context: Context): Context {
        return context
    }

    // Provides the Room database instance
    @Provides
    @Singleton
    fun provideDatabase(context: Context): TodoDatabase {
        return Room.databaseBuilder(context, TodoDatabase::class.java, "app-database").build()
    }

    // Provides the DAO (Data Access Object) for Todo entities
    @Provides
    @Singleton
    fun provideTodoDao(database: TodoDatabase): ToDoListDAO {
        return database.todoDao()
    }

    // Provides the DAO (Data Access Object) for AnalyticData entities
    @Provides
    @Singleton
    fun provideAnalyticDataDao(database: TodoDatabase): AnalyticDataDao {
        return database.analyticDataDao()
    }

    // Provides the repository for AnalyticData operations
    @Provides
    @Singleton
    fun provideAnalyticDataRepository(analyticDataDao: AnalyticDataDao): AnalyticDataRepository {
        return AnalyticDataRepository(analyticDataDao)
    }

    // Provides the repository for Todo operations
    @Provides
    @Singleton
    fun providesRepositoryImpl(totoDatabase: TodoDatabase): TodoRepository {
        return TodoRepositoryImpl(totoDatabase)
    }

}
