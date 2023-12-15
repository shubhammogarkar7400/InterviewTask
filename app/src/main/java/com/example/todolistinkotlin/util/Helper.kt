package com.example.todolistinkotlin.util

import android.content.Context
import android.net.ConnectivityManager


// Checks the device's network connectivity status.
fun isOnline(context: Context): Boolean {
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val networkInfo = connectivityManager.activeNetworkInfo
    return networkInfo != null && networkInfo.isConnectedOrConnecting
}