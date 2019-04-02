package com.assignment.injection.module;

import android.content.Context;

import com.assignment.common.AssignmentApp;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class AppModule {
    @Singleton
    @Provides
    public Gson getGson() {
        return new GsonBuilder().create();
    }

    @Named("flickerApiKey")
    @Provides
    public String flickerApiKey() {
        return "3e1da1ab7062b54b4a63030b7630000d";
    }

    @Named("flickerSearchPhotoMethod")
    @Provides
    public String flickerSearchMethod() {
        return "flickr.photos.search";
    }


    @Singleton
    @Provides
    public Context appContext() {
        return AssignmentApp.getApplication();
    }
}
