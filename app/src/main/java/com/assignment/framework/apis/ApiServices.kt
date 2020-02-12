package com.apis

import com.model.SearchPhotoDataModel
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiServices {

    @GET("/services/rest/")
    fun searchPhotos(@Query("method") method: String, @Query("api_key") apiKey: String,
                     @Query("text") text: String, @Query("per_page") pageSize: Int,
                     @Query("page") page: Int, @Query("format") format: String,
                     @Query("nojsoncallback") noJsonCallback: Int): Single<SearchPhotoDataModel>

}
