package com.assignment.presentation.viewmodels.common

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.assignment.common.AssignmentApp
import com.assignment.presentation.helpers.IInternetStatus
import com.assignment.usecase.Intractors
import com.model.StatusData


open class BaseViewModel protected constructor(application: Application,
                                               intractors: Intractors, singleLiveEvent: SingleLiveEvent<StatusData>, mInternetStatus: IInternetStatus) : AndroidViewModel(application) {
    val singleLiveEvent: SingleLiveEvent<StatusData> =SingleLiveEvent()
    protected val application:AssignmentApp = getApplication()
}
