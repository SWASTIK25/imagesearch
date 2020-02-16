package com.assignment.presentation.viewmodels

import android.app.Application
import androidx.databinding.ObservableField
import com.assignment.presentation.helpers.IInternetStatus
import com.assignment.presentation.viewmodels.common.BaseViewModel
import com.assignment.presentation.viewmodels.common.SingleLiveEvent
import com.assignment.usecase.Intractors
import com.model.StatusData

class PhotoViewerModel constructor(application: Application,
                                   private var intractors: Intractors,
                                   private val mSingleLiveEvent: SingleLiveEvent<StatusData>,
                                   private val mInternetStatus: IInternetStatus) : BaseViewModel(application, intractors, mSingleLiveEvent, mInternetStatus) {
    var currentPage = ObservableField<String>()

    fun printMsg(msg: String) {
        println(msg)
    }

}