package com.assignment.data

import android.content.Context
import android.text.TextUtils
import android.util.Pair
import com.apis.ApiServices
import com.assignment.common.Constants
import com.assignment.domain.model.SearchPhotoRequest
import com.assignment.exceptions.NoDataException
import com.assignment.exceptions.NoMoreDataException
import com.assignment.framework.db.LocalDataHelperImpl
import com.assignment.presentation.helpers.AppFileUtils
import com.assignment.presentation.helpers.IInternetStatus
import com.model.PhotoItem
import com.model.Photos
import com.model.SearchPhotoDataModel
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.SingleSource
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers
import java.util.*
import javax.inject.Inject
import kotlin.collections.HashMap
import kotlin.collections.List

class PhotoSearchRepository @Inject
constructor(private val mContext: Context, private val mInternetStatus: IInternetStatus, private val mAppFileUtils: AppFileUtils) {

    @Inject
    lateinit var mDataSource: LocalDataHelperImpl
    @Inject
    lateinit var mApiService: ApiServices

    fun searchPhotos(request: SearchPhotoRequest): Single<SearchPhotoDataModel> {
        //From local store
        if (!mInternetStatus.isConnected) {
            //Return all data in single page in case of offline
            if (request.page > 1) {
                request.page = 1
            }
            return getLocallySavedDataObservable(request.text)
                    .subscribeOn(Schedulers.computation())
                    .observeOn(AndroidSchedulers.mainThread())
        }
        return mApiService.searchPhotos(request.method!!, request.apiKey!!, request.text!!, request.pageSize, request.page, request.format!!, request.noJsonCallback)
                .flatMap(object : Function<SearchPhotoDataModel, SingleSource<out SearchPhotoDataModel>> {

                    @Throws(Exception::class)
                    override fun apply(flickrObj: SearchPhotoDataModel): SingleSource<out SearchPhotoDataModel> {

                        val (_, _, pages, photos, page) = flickrObj.photos
                                ?: return Single.error(NoDataException("Required data not available"))
                     // If last page reached
                        if (page == pages) {
                            return Single.error(NoMoreDataException("Required data not available"))
                        }
                        if (photos != null) {
                            for (photo in photos) {
                                // Make url for Images and store it in same POJO
                                val url = String.format(Locale.getDefault(), "https://farm%d.staticflickr.com/%s/%s_%s_q.jpg",
                                        photo.farm, photo.server, photo.id, photo.secret)
                                photo.photoUrl = url
                            }

                            flickrObj.photos?.photo = photos
                            return Single.just(flickrObj)
                        }
                        return Single.error(NoDataException("Required data not available"))
                    }


                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }


    /**
     * Get file from URL and store data in database
     *
     * @param queryName     name of search query
     * @param flickerPhotos photo object to fetch url from
     */
    fun getFileInDirectoryAndStoreData(queryName: String, flickerPhotos: List<PhotoItem>?): Observable<Boolean> {
        return if (flickerPhotos == null || flickerPhotos.isEmpty()) {
            return Observable.error(NoDataException("No Data found"))
        } else Observable.fromIterable(flickerPhotos)
                .flatMap { photo ->
                    Observable.just(photo)
                }
                .filter { !TextUtils.isEmpty(it.photoUrl) }
                .map {
                    // get file from network and store it in destination path
                    val destinationFile = mAppFileUtils.createFileAt(mContext, it.title
                            ?: "temp", Constants.AppConstants.EXTENSION)
                    //TODO need to handle null value
                    val storedFile = mAppFileUtils.getFile(it.photoUrl?:"", HashMap(), destinationFile)
                    Pair<String, String>(it.photoUrl, if (storedFile == null) "" else storedFile.getAbsolutePath())
                }.map { stringStringPair ->
                    //> Store path in local database
                    mDataSource.insertData(queryName, stringStringPair.first, stringStringPair.second)
                    true
                }.observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
    }

    /**
     * Get Single observable from the method which loads data from database async on condition of search query
     *
     * @param searchString query string
     * @return Single Observable to return List of Photo object
     */
    private fun getLocallySavedDataObservable(searchString: String?): Single<SearchPhotoDataModel> {
        return Single.fromCallable {
            val list = ArrayList<PhotoItem>()
            val pathList = mDataSource.getAllImagePaths(searchString!!)
            if (pathList.isNotEmpty()) {
                for (localPath in pathList) {
                    val photoItem = PhotoItem()
                    photoItem.photoUrl = localPath
                    list.add(photoItem)
                }
            }

            val searchPhotoDataModel = SearchPhotoDataModel()
            val photos = Photos()
            photos.photo = list
            searchPhotoDataModel.photos = photos
            searchPhotoDataModel.stat = "ok"
            searchPhotoDataModel
        }
    }
}
