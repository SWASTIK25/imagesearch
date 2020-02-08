package com.assignment.data;

import android.content.Context;
import android.text.TextUtils;
import android.util.Pair;

import com.assignment.common.Constants;
import com.assignment.data.apis.ApiServices;
import com.assignment.data.database.LocalDataHelper;
import com.assignment.exceptions.NoDataException;
import com.assignment.exceptions.NoMoreDataException;
import com.assignment.presentation.helpers.AppFileUtils;
import com.assignment.presentation.helpers.IInternetStatus;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;
import javax.inject.Named;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class PhotoSearchRepository {

    private final AppFileUtils mAppFileUtils;
    private ApiServices mApiService;
    private IInternetStatus mInternetStatus;
    private LocalDataHelper mDataSource;
    private Context mContext;

    @Inject
    public PhotoSearchRepository(Context context, @Named("RETROFIT_NORMAL") Retrofit retrofit
            , IInternetStatus internetStatus
            , LocalDataHelper dataSourceHelper
            , AppFileUtils appFileUtils) {
        mApiService = retrofit.create(ApiServices.class);
        mInternetStatus = internetStatus;
        mDataSource = dataSourceHelper;
        mAppFileUtils = appFileUtils;
        mContext = context;
    }

    public Single<SearchPhotoDataModel> searchPhotos(SearchPhotoRequest request) {


        //> From local store
        if (!mInternetStatus.isConnected()) {
            //> Return all data in single page in case of offline
            if (request.getPage() > 1) {
                request.setPage(1);
            }
            return getLocallySavedDataObservable(request.getText())
                    .subscribeOn(Schedulers.computation())
                    .observeOn(AndroidSchedulers.mainThread());
        }
        return mApiService.searchPhotos(request.getMethod()
                , request.getApiKey()
                , request.getText()
                , request.getPageSize()
                , request.getPage()
                , request.getFormat()
                , request.getNoJsonCallback())
                .flatMap(new Function<SearchPhotoDataModel, SingleSource<? extends SearchPhotoDataModel>>() {
                    @Override
                    public SingleSource<? extends SearchPhotoDataModel> apply(SearchPhotoDataModel flickrObj) throws Exception {

                        if (flickrObj == null) {
                            return Single.error(new NoDataException());
                        }

                        SearchPhotoDataModel.Photos photosWrapper = flickrObj.getPhotos();
                        if (photosWrapper == null) {
                            return Single.error(new NoDataException());
                        }
                        //> If last page reached
                        if (photosWrapper.getPage() == photosWrapper.getPages()) {
                            return Single.error(new NoMoreDataException());
                        }
                        List<SearchPhotoDataModel.PhotoItem> photos = photosWrapper.getPhoto();
                        if (photos != null) {
                            for (SearchPhotoDataModel.PhotoItem photo : photos) {
                                //> Make url for Images and store it in same POJO
                                String url = String.format(Locale.getDefault(), "https://farm%d.staticflickr.com/%s/%s_%s_q.jpg",
                                        photo.getFarm(), photo.getServer(), photo.getId(), photo.getSecret());
                                photo.setPhotoUrl(url);
                            }

                            flickrObj.getPhotos().setPhoto(photos);
                            return Single.just(flickrObj);
                        }
                        return Single.error(new NoDataException());
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    /**
     * Get file from URL and store data in database
     *
     * @param queryName     name of search query
     * @param flickerPhotos photo object to fetch url from
     */
    public Observable<Boolean> getFileInDirectoryAndStoreData(String queryName, List<SearchPhotoDataModel.PhotoItem> flickerPhotos) {
        if (flickerPhotos == null || flickerPhotos.size() == 0) {
            return null;
        }
        Observable<Boolean> observable = Observable.fromIterable(flickerPhotos)
                .flatMap(photo -> {
                    //> Null checks
                    if (photo == null || photo.getPhotoUrl().isEmpty()) {
//                        return Observable.error(new NoDataException());
                    }
                    //> Check if file has already been downloaded previously and exists in directory
                    String localPath = mDataSource.getLocalImagePath(queryName, photo.getPhotoUrl());
                    if (!TextUtils.isEmpty(localPath)) {
                        File file = new File(localPath);
                        if (file.exists()) {
//                            return Observable.error(new FileAlreadyExistException());
                        }
                    }
                    //> If doesn't exists, then proceed
                    return Observable.just(photo);
                })
                .filter(photo -> !TextUtils.isEmpty(photo.getPhotoUrl()))
                .map(photo -> {
                    //> get file from network and store it in destination path
                    File destinationFile = mAppFileUtils.createFileAt(mContext, photo.getTitle(), Constants.AppConstants.EXTENSION);
                    File storedFile = mAppFileUtils.getFile(photo.getPhotoUrl(), null, destinationFile);
                    return new Pair<>(photo.getPhotoUrl(), storedFile == null ? "" : storedFile.getAbsolutePath());
                }).map(stringStringPair -> {
                    //> Store path in local database
                    mDataSource.insertData(queryName, stringStringPair.first, stringStringPair.second);
                    return true;
                }).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());

        return observable;
    }

    /**
     * Get Single observable from the method which loads data from database async on condition of search query
     *
     * @param searchString query string
     * @return Single Observable to return List of Photo object
     */
    private Single<SearchPhotoDataModel> getLocallySavedDataObservable(String searchString) {
        return Single.fromCallable(() -> {
            List<SearchPhotoDataModel.PhotoItem> list = new ArrayList<>();
            List<String> pathList = mDataSource.getAllImagePaths(searchString);
            if (pathList.size() != 0) {
                for (String localPath : pathList) {
                    SearchPhotoDataModel.PhotoItem photoItem = new SearchPhotoDataModel.PhotoItem();
                    photoItem.setPhotoUrl(localPath);
                    list.add(photoItem);
                }
            }

            SearchPhotoDataModel searchPhotoDataModel = new SearchPhotoDataModel();
            SearchPhotoDataModel.Photos photos = new SearchPhotoDataModel.Photos();
            photos.setPhoto(list);
            searchPhotoDataModel.setPhotos(photos);
            searchPhotoDataModel.setStat("ok");
            return searchPhotoDataModel;
        });
    }
}
