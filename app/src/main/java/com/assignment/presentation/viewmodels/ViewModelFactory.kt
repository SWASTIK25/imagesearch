package com.assignment.presentation.viewmodels

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.assignment.presentation.helpers.IInternetStatus
import com.assignment.presentation.viewmodels.common.BaseViewModel
import com.assignment.presentation.viewmodels.common.SingleLiveEvent
import com.assignment.usecase.Intractors
import com.model.StatusData
import javax.inject.Inject

class ViewModelFactory @Inject constructor(val application: Application,val intractors: Intractors,val singleLiveEvent: SingleLiveEvent<StatusData>,
                                           val internetStatus:IInternetStatus):ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (BaseViewModel::class.java.isAssignableFrom(modelClass)) {
            return modelClass.getConstructor(Application::class.java,Intractors::class.java,SingleLiveEvent::class.java,IInternetStatus::class.java)
                    .newInstance(application,intractors,singleLiveEvent,internetStatus)
        }else {
            throw IllegalArgumentException("unexpected viewModel class $modelClass")
        }
    }
}