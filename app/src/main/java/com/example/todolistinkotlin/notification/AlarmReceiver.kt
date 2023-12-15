package com.example.todolistinkotlin.notification

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.util.Log
import androidx.annotation.CallSuper
import androidx.core.app.NotificationCompat
import com.example.todolistinkotlin.R
import com.example.todolistinkotlin.database.repositories.TodoRepository
import com.example.todolistinkotlin.util.Constants.DATE
import com.example.todolistinkotlin.util.Constants.ID
import com.example.todolistinkotlin.util.Constants.IS_SHOW
import com.example.todolistinkotlin.util.Constants.TITLE
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject


@AndroidEntryPoint
class AlarmReceiver : HiltBroadcastReceiver() {

    @Inject
    lateinit var todoRepository: TodoRepository


    private val GROUP_MESSAGE: String = "TODOLIST"

    // Handles the broadcasted alarm intent
     override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        val notificationManager: NotificationManager = context.getSystemService(Service.NOTIFICATION_SERVICE) as NotificationManager

        // Extracting data from the intent
        var isShow = intent.getIntExtra(IS_SHOW, 0)
        val dbId = intent.getLongExtra(ID, -1)
        val title = intent.getStringExtra(TITLE) ?: ""
        val time = intent.getStringExtra(DATE) ?:""
        Log.d("Alarm Title", "title : $title")

        // Create a notification channel for Android Oreo and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(NOTIFICATION_CHANNEL_ID, NOTIFICATION_NAME, NotificationManager.IMPORTANCE_DEFAULT)
            // Configure the notification channel.
            notificationChannel.description = "Sample Channel description"
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(false)
            notificationManager.createNotificationChannel(notificationChannel)
        }

        // Build the notification
        val notification = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentTitle(title)
            .setContentText(time)
            .setPriority(NotificationCompat.VISIBILITY_PUBLIC)
            .setColor(Color.RED)
            .setGroup(GROUP_MESSAGE)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .build()

        // Notify the user with the created notification
        notificationManager.notify(getNumber(), notification)

        // Update the database to mark the alarm as shown
        CoroutineScope(Dispatchers.Default).launch {
            todoRepository.isShownUpdate(id = dbId, isShow = 1)
        }

    }


    // to show multiple number of notification , there is need of unique number
    private fun getNumber(): Int = (Date().time / 1000L % Integer.MAX_VALUE).toInt()

    companion object{
        private const val NOTIFICATION_CHANNEL_ID = "Remainder"
        private const val NOTIFICATION_NAME = "TODO Notifications"

    }

}



abstract class HiltBroadcastReceiver : BroadcastReceiver() {
    @CallSuper
    override fun onReceive(context: Context, intent: Intent) {}
}