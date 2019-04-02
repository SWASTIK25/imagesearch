package com.assignment.injection.module;

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
        return "9e982e83f827b4249f27273f970a3be2";
    }

    @Named("flickerSearchPhotoMethod")
    @Provides
    public String flickerSearchMethod() {
        return "flickr.photos.search";
    }


}
