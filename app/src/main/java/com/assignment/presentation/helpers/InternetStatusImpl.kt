package com.assignment.presentation.helpers

import android.content.Context
import android.net.ConnectivityManager
import javax.inject.Inject

class InternetStatusImpl @Inject constructor(val context: Context) : IInternetStatus {
    override val isConnected: Boolean
        get() = verifyAvailableNetwork()

    private fun verifyAvailableNetwork(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }
}