package com.example.todolistinkotlin

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.annotation.WorkerThread
import androidx.databinding.ObservableField
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.todolistinkotlin.database.repositories.TodoRepository
import com.example.todolistinkotlin.enums.EventType
import com.example.todolistinkotlin.model.AnalyticsData
import com.example.todolistinkotlin.model.ToDoListDataEntity
import com.example.todolistinkotlin.notification.AlarmReceiver
import com.example.todolistinkotlin.util.Constants.ANALYTIC_DATA_ARG
import com.example.todolistinkotlin.util.Constants.DATE
import com.example.todolistinkotlin.util.Constants.ID
import com.example.todolistinkotlin.util.Constants.IS_SHOW
import com.example.todolistinkotlin.util.Constants.TITLE
import com.example.todolistinkotlin.worker.AnalyticDataWorker
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone
import javax.inject.Inject


@HiltViewModel
class ToDoListViewModel @Inject constructor(
    private val context: Application,
    private val todoRepository: TodoRepository
) : AndroidViewModel(context) {

    private var getAllData = mutableListOf(ToDoListDataEntity())
    val toDoList = MutableLiveData<List<ToDoListDataEntity>>()

    private val workManager = WorkManager.getInstance(context)

    init {
        CoroutineScope(Dispatchers.Default).launch {
            getAllData = todoRepository.getAll() as MutableList<ToDoListDataEntity>
            getPreviousList()
        }
    }


    var title = ObservableField("")
    var date = ObservableField("")
    var time = ObservableField("")

    var month = 0
    var day = 0
    var year = 0

    var hour = 0
    var minute = 0

    var position: Int = -1
    var index: Long = -1


    fun addToDoListDataEntity(analyticsData: AnalyticsData) {

        Log.d("Click", "click")
        if (title.get().toString().isNotBlank() && date.get().toString().isNotBlank() && time.get().toString().isNotBlank()) {
            addData(title.get().toString(), date.get().toString(), time.get().toString(), id = index)
            title.set("")
            date.set("")
            time.set("")

            addAnalyticData(analyticsData)
        }else{
            Toast.makeText(context,
                context.getString(R.string.enter_all_filed_data),Toast.LENGTH_SHORT).show()
            analyticsData.eventType = EventType.ADD_TODO_FIELD_ERROR.value
            analyticsData.eventDescription =
                context.getString(R.string.form_filled_incomplete_error)
            addAnalyticData(analyticsData)
        }
    }

    fun addAnalyticData(analyticsData: AnalyticsData) {
        val analyticsDataString = Gson().toJson(analyticsData)
        val data: Data = Data.Builder()
            .putString(ANALYTIC_DATA_ARG, analyticsDataString)
            .build()
        val analyticDataWorker =
            OneTimeWorkRequestBuilder<AnalyticDataWorker>().setInputData(inputData = data)
                .build()

        workManager.enqueue(analyticDataWorker)
    }

    @WorkerThread
    fun addData(title: String, date: String, time: String, id: Long) {
        CoroutineScope(Dispatchers.Default).launch {
            if (position != -1) {
                todoRepository.update(title = title, date = date, time = time, id = id)
            } else {

                val newId = todoRepository.insert(ToDoListDataEntity(title = title, date = date, time = time, isShow = 0))

                val cal : Calendar = Calendar.getInstance(TimeZone.getDefault(), Locale.getDefault())

                cal.set(Calendar.MONTH, month)
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.DAY_OF_MONTH, day)

                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.HOUR_OF_DAY, hour)
                cal.set(Calendar.MINUTE, minute)

                Log.d("Alarm Title","$month , $date : ${cal.time}")

                setAlarm(cal, 0, newId, title,hour,minute)
            }

            todoRepository.getAll().let {
                getAllData = it as MutableList<ToDoListDataEntity>
                getPreviousList()
            }

        }

     }


    fun getPreviousList() {
        viewModelScope.launch { toDoList.value = getAllData }
    }

    fun delete(id: Long, analyticsData: AnalyticsData) {
        CoroutineScope(Dispatchers.Default).launch {
            todoRepository.delete(id)
            todoRepository.getAll().let {
                getAllData = it as MutableList<ToDoListDataEntity>
                getPreviousList()
                addAnalyticData(analyticsData)
            }
        }
    }

    private fun setAlarm(calender: Calendar, i: Int, id: Long, title: String, hour:Int, minute:Int) {

        // Generate a unique request code
        val requestCode = System.currentTimeMillis().toInt()

        val alarmManager: AlarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(context, AlarmReceiver::class.java)
        intent.putExtra("INTENT_NOTIFY", true)
        intent.putExtra(IS_SHOW, i)
        intent.putExtra(ID, id)
        intent.putExtra(TITLE, title)
        intent.putExtra(DATE,"Time-> $hour:$minute")
        val pandingIntent: PendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_IMMUTABLE)

        if (i == 0) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,  calender.timeInMillis , pandingIntent)
        } else {
            alarmManager.cancel(pandingIntent)
        }
    }
}