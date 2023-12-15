package com.example.todolistinkotlin.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "analytics")
data class AnalyticsData(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val deviceId: String, // Unique identifier for the device
    val timestamp: Long, // Timestamp of when the analytic event occurred
    var eventType: String, // Type of analytic event (e.g., "add", "edit", "delete", etc.)
    var eventDescription: String, // Description or details of the analytic event
    var sessionDuration: Long?, // App used duration time
    val deviceType: String, // Type of device (e.g., "phone", "tablet", etc.)
    val androidVersion: Int, // Version of the Android operating system
    val isOnline: Boolean, // Online status of the user during the analytic event
)