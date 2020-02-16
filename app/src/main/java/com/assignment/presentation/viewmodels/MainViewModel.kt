package com.assignment.presentation.viewmodels

import android.app.Application
import android.util.Log
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.assignment.domain.model.SearchPhotoRequest
import com.assignment.exceptions.ApiException
import com.assignment.presentation.helpers.IInternetStatus
import com.assignment.presentation.viewmodels.common.BaseViewModel
import com.assignment.presentation.viewmodels.common.SingleLiveEvent
import com.assignment.usecase.Intractors
import com.model.Photos
import com.model.SearchPhotoDataModel
import com.model.StatusData
import io.reactivex.Observer
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

class MainViewModel constructor(application: Application,
                                private var intractors: Intractors, private val mSingleLiveEvent: SingleLiveEvent<StatusData>, private val mInternetStatus: IInternetStatus) : BaseViewModel(application,intractors,mSingleLiveEvent,mInternetStatus) {
    private val mCompositeDisposable: CompositeDisposable?
    val photoSearchResponseData = MutableLiveData<Photos>()
    val searchTxt = ObservableField<String>()
    val isVisible = ObservableField<Boolean>()

    init {
        this.mCompositeDisposable = CompositeDisposable()
    }

    fun search(searchPhotoRequest: SearchPhotoRequest) {
        intractors.searchPhotos(searchPhotoRequest)
                .subscribe(object : Observer<SearchPhotoDataModel> {
                    override fun onSubscribe(d: Disposable) {
                        mCompositeDisposable!!.add(d)
                    }

                    override fun onNext(searchPhotoDataModel: SearchPhotoDataModel) {

                        if (searchPhotoDataModel.stat == "ok") {
                            photoSearchResponseData.postValue(searchPhotoDataModel.photos)
                            if (mInternetStatus.isConnected) {
                                intractors.storeDataLocally(searchPhotoRequest.text?:"", searchPhotoDataModel).subscribe()
                            }
                        } else {
                            val status = StatusData()
                            status.status = StatusData.Status.FAIL
                            //TODO declare exception here
                            status.throwable = ApiException("test")
                            mSingleLiveEvent.setValue(status)
                        }
                        Log.i(javaClass.name, searchPhotoDataModel.toString())
                    }

                    override fun onError(e: Throwable) {
                        e.printStackTrace()
                        val status = StatusData()
                        status.status = StatusData.Status.FAIL
                        status.throwable = e
                        mSingleLiveEvent.value = status
                    }

                    override fun onComplete() {

                    }
                })
    }


    override fun onCleared() {
        mCompositeDisposable?.dispose()
        super.onCleared()
    }


}
