package com.assignment.injection.module

import android.content.Context

import com.assignment.common.AssignmentApp
import com.google.gson.Gson
import com.google.gson.GsonBuilder

import javax.inject.Named
import javax.inject.Singleton

import dagger.Module
import dagger.Provides

@Module
class AppModule {
    val gson: Gson
        @Singleton
        @Provides
        get() = GsonBuilder().create()

    @Named("flickerApiKey")
    @Provides
    fun flickerApiKey(): String {
        return "3e1da1ab7062b54b4a63030b7630000d"
    }

    @Named("flickerSearchPhotoMethod")
    @Provides
    fun flickerSearchMethod(): String {
        return "flickr.photos.search"
    }


    @Singleton
    @Provides
    fun appContext(): Context {
        return AssignmentApp.application.applicationContext
    }
}
