package com.example.todolistinkotlin

 import android.app.DatePickerDialog
 import android.app.TimePickerDialog
 import android.content.pm.PackageManager
 import android.os.Build
 import android.os.Bundle
 import android.provider.Settings
 import android.view.View
 import androidx.activity.result.contract.ActivityResultContracts
 import androidx.activity.viewModels
 import androidx.appcompat.app.AppCompatActivity
 import androidx.core.content.ContextCompat
 import com.example.todolistinkotlin.databinding.ActivityMainBinding
 import com.example.todolistinkotlin.enums.DeviceType
 import com.example.todolistinkotlin.enums.EventType
 import com.example.todolistinkotlin.model.AnalyticsData
 import com.example.todolistinkotlin.preferences.AppPreferences
 import com.example.todolistinkotlin.util.isOnline
 import dagger.hilt.android.AndroidEntryPoint
 import org.jetbrains.anko.alert
 import org.jetbrains.anko.toast
 import java.text.SimpleDateFormat
 import java.util.Calendar


@AndroidEntryPoint
class MainActivity : AppCompatActivity(), OnItemClick {
    private val binding: ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val list = mutableListOf<ToDoListData>()

    private val c = Calendar.getInstance()

    private val month: Int = c.get(Calendar.MONTH)
    private val year: Int = c.get(Calendar.YEAR)
    private val day: Int = c.get(Calendar.DAY_OF_MONTH)

    private var cal = Calendar.getInstance()

    private val listAdapter = ListAdapter(list, this)

    private val viewModel: ToDoListViewModel by viewModels()

    // Initialize the AppPreferences singleton
    val appPreferences = AppPreferences.getInstance(this)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.lifecycleOwner = this

        binding.rvTodoList.adapter = listAdapter
        binding.vieModel = viewModel


        viewModel.getPreviousList()

        viewModel.toDoList.observe(this) { toDoList ->
            if (toDoList == null)
                return@observe

            list.clear()
            val tempList = toDoList.map { toDo ->
                ToDoListData(  title = toDo.title,
                date = toDo.date,
                time = toDo.time,
                indexDb = toDo.id,
                isShow = toDo.isShow)
            }

            list.addAll(tempList)
            listAdapter.notifyDataSetChanged()
            viewModel.position = -1;

            viewModel.toDoList.value = null
        }

        binding.etdate.setOnClickListener {

            val dpd = DatePickerDialog(this, { view, year, monthOfYear, dayOfMonth ->

                // Display Selected date in textbox
                binding.etdate.setText("" + dayOfMonth + "/" + (monthOfYear + 1) + "/" + year)
                viewModel.month = monthOfYear
                viewModel.year = year
                viewModel.day = dayOfMonth
            }, year, month, day)

            dpd.datePicker.minDate = System.currentTimeMillis() - 1000
            dpd.show()

        }
        binding.etTime.setOnClickListener {
            val cal = Calendar.getInstance()
            val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
                cal.set(Calendar.HOUR_OF_DAY, hour)
                cal.set(Calendar.MINUTE, minute)
                this.cal.set(Calendar.HOUR_OF_DAY, hour)
                this.cal.set(Calendar.MINUTE, minute)

                viewModel.hour = hour
                viewModel.minute = minute

                binding.etTime.setText(SimpleDateFormat("HH:mm").format(cal.time))
            }

            this.cal = cal
            TimePickerDialog(
                this,
                timeSetListener,
                cal.get(Calendar.HOUR_OF_DAY),
                cal.get(Calendar.MINUTE),
                true
            ).show()
        }


        binding.bAddList.setOnClickListener {

            val analyticsData = getAnalyticsData()

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                // Code to be executed on Android 13 and above
                when (PackageManager.PERMISSION_GRANTED) {
                    ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) -> {
                        // You can use the API that requires the permission.
                        viewModel.addToDoListDataEntity(analyticsData)
                    }
                    else -> {
                        // You can directly ask for the permission.
                        // The registered ActivityResultCallback gets the result of this request.
                        requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                    }
                }
            } else {
                // Code for versions below Android 13
                viewModel.addToDoListDataEntity(analyticsData)
            }


        }

    }

    private fun getAnalyticsData(): AnalyticsData {
        val deviceId = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
        val isTablet = resources.getBoolean(R.bool.isTablet)
        val androidVersion = Build.VERSION.RELEASE
        val isOnline = isOnline(this)

        return AnalyticsData(
            deviceId = deviceId,
            timestamp = System.currentTimeMillis(),
            eventType = if (viewModel.position != -1) EventType.EDIT_TODO.value else EventType.ADD_TODO.value,
            eventDescription = if (viewModel.position != -1) getString(R.string.updated_old_todo) else getString(
                R.string.new_todo_added
            ),
            sessionDuration = null,
            deviceType = if (isTablet) DeviceType.TABLET.value else DeviceType.PHONE.value,
            androidVersion = androidVersion.toInt(),
            isOnline = isOnline
        )
    }


    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
        if (isGranted) {
            binding.bAddList.performClick()
        } else {
           toast(getString(R.string.please_allow_the_alarm_permission))
        }
    }


    override fun onItemClick(v: View, position: Int) {

        alert {
            message = list[position].title
            positiveButton(getString(R.string.edit)) {
                viewModel.title.set(list[position].title)
                viewModel.date.set(list[position].date)
                viewModel.time.set(list[position].time)
                viewModel.position = position
                viewModel.index = list[position].indexDb
                binding.etTitle.isFocusable = true
            }
            negativeButton(getString(R.string.delete)) {
                val analyticsData = getAnalyticsData()
                analyticsData.eventType = EventType.DELETE_TODO.value
                analyticsData.eventDescription = getString(R.string.todo_deleted)
                viewModel.delete(list[position].indexDb, analyticsData)
            }

        }.show()

    }

    override fun onDestroy() {
        super.onDestroy()

        if (appPreferences.getAppStartTimestamp() != 0.toLong()){
            val sessionDuration = System.currentTimeMillis() - appPreferences.getAppStartTimestamp()
            val analyticsData = getAnalyticsData()
            analyticsData.eventType = EventType.SESSION_TIMESTAMP.value
            analyticsData.sessionDuration = sessionDuration
            viewModel.addAnalyticData(analyticsData = analyticsData)
        }
    }


    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

}
