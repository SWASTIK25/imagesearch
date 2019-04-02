package com.assignment.presentation.viewmodels.common;

import android.arch.lifecycle.ViewModel;

import com.assignment.data.StatusData;


public class BaseViewModel extends ViewModel {

    private SingleLiveEvent<StatusData> mSingleLiveEvent;


    protected BaseViewModel(SingleLiveEvent<StatusData> singleLiveEvent) {
        this.mSingleLiveEvent = new SingleLiveEvent<>();
    }


    public SingleLiveEvent<StatusData> getSingleLiveEvent() {
        return mSingleLiveEvent;
    }
}
