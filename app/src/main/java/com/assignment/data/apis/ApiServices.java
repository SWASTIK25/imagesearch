package com.assignment.data.apis;

import com.assignment.data.SearchPhotoDataModel;

import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiServices {

    @GET("/services/rest/")
    Single<SearchPhotoDataModel> searchPhotos(@Query("method") String method
            , @Query("api_key") String apiKey
            , @Query("text") String text
            , @Query("per_page") int pageSize
            , @Query("page") int page
            , @Query("format") String format
            , @Query("nojsoncallback") int noJsonCallback);

}
