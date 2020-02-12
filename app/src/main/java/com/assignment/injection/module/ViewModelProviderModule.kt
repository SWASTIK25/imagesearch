package com.assignment.injection.module

import android.content.Context

import com.apis.ApiServices
import com.assignment.data.PhotoSearchRepository
import com.assignment.framework.db.LocalDataHelper
import com.assignment.framework.db.LocalDataHelperImpl
import com.assignment.injection.scope.ActivityScope
import com.assignment.presentation.helpers.AppFileUtils
import com.assignment.presentation.helpers.InternetStatusImpl
import com.assignment.usecase.DeletePhotos
import com.assignment.usecase.Intractors
import com.assignment.usecase.SearchPhotos
import com.assignment.usecase.StoreDataLocally

import javax.inject.Singleton

import dagger.Module
import dagger.Provides
import retrofit2.Retrofit

@Module
class ViewModelProviderModule {


    @Singleton
    @Provides
    fun provideLocalDataHelper(context: Context): LocalDataHelper {
        return LocalDataHelperImpl(context)
    }

    @ActivityScope
    @Provides
    fun provideApiService(retrofit: Retrofit): ApiServices {
        return retrofit.create(ApiServices::class.java)
    }
    
    @ActivityScope
    @Provides
    fun provideIntractor(photoSearchRepository: PhotoSearchRepository): Intractors {
        return Intractors(SearchPhotos(photoSearchRepository), DeletePhotos(photoSearchRepository), StoreDataLocally(photoSearchRepository))
    }
}

