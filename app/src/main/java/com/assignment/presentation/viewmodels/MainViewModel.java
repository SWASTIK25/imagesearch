package com.assignment.presentation.viewmodels;

import android.arch.lifecycle.MutableLiveData;

import com.assignment.data.SearchPhotoDataModel;
import com.assignment.data.SearchPhotoRequest;
import com.assignment.data.StatusData;
import com.assignment.domain.MainInteractors;
import com.assignment.exceptions.ApiException;
import com.assignment.presentation.helpers.IInternetStatus;
import com.assignment.presentation.helpers.LogUtil;
import com.assignment.presentation.viewmodels.common.BaseViewModel;
import com.assignment.presentation.viewmodels.common.SingleLiveEvent;

import javax.inject.Inject;

import io.reactivex.Observer;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class MainViewModel extends BaseViewModel {

    private final SingleLiveEvent<StatusData> mSingleLiveEvent;
    private MainInteractors.PhotoSearch mPhotoSearch;
    private CompositeDisposable mCompositeDisposable;
    private MutableLiveData<SearchPhotoDataModel.Photos> mPhotoSearchResponseLiveData =new MutableLiveData<>();
    private IInternetStatus mInternetStatus;

    @Inject
    protected MainViewModel(SingleLiveEvent<StatusData> singleLiveEvent, MainInteractors.PhotoSearch photoSearch, IInternetStatus internetStatus) {
        super(singleLiveEvent);
        mSingleLiveEvent = singleLiveEvent;
        this.mCompositeDisposable = new CompositeDisposable();
        this.mPhotoSearch = photoSearch;
        this.mInternetStatus = internetStatus;
    }

    public void search(SearchPhotoRequest searchPhotoRequest) {
        mPhotoSearch
                .getBehaviorStream(searchPhotoRequest)
                .subscribe(new Observer<SearchPhotoDataModel>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mCompositeDisposable.add(d);
                    }

                    @Override
                    public void onNext(SearchPhotoDataModel searchPhotoDataModel) {

                        if (searchPhotoDataModel.getStat().equals("ok")) {
                            mPhotoSearchResponseLiveData.postValue(searchPhotoDataModel.getPhotos());
                            if (mInternetStatus.isConnected()) {
                                mPhotoSearch.storeDataLocally(searchPhotoRequest.getText(), searchPhotoDataModel).subscribe();
                            }
                        } else {
                            StatusData status = new StatusData();
                            status.setStatus(StatusData.Status.FAIL);
                            status.setThrowable(new ApiException());
                            mSingleLiveEvent.setValue(status);
                        }
                        LogUtil.i(getClass().getName(), searchPhotoDataModel.toString());
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        StatusData status = new StatusData();
                        status.setStatus(StatusData.Status.FAIL);
                        status.setThrowable(e);
                        mSingleLiveEvent.setValue(status);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }


    @Override
    protected void onCleared() {
        if (mCompositeDisposable != null) {
            mCompositeDisposable.dispose();
        }
        super.onCleared();
    }

    public MutableLiveData<SearchPhotoDataModel.Photos> getPhotoSearchResponseData() {
        return mPhotoSearchResponseLiveData;
    }


}
