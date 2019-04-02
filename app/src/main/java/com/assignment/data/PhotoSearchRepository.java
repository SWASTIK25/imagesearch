package com.assignment.data;

import com.assignment.data.apis.ApiServices;

import javax.inject.Inject;
import javax.inject.Named;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class PhotoSearchRepository {

    private ApiServices mApiService;

    @Inject
    public PhotoSearchRepository(@Named("RETROFIT_NORMAL") Retrofit retrofit) {
        mApiService = retrofit.create(ApiServices.class);
    }

    public Single<SearchPhotoDataModel> searchPhotos(SearchPhotoRequest request) {
        return mApiService.searchPhotos(request.getMethod()
                , request.getApiKey()
                , request.getText()
                , request.getPageSize()
                , request.getPage()
                , request.getFormat()
                , request.getNoJsonCallback())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
