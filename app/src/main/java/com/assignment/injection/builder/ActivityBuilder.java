package com.assignment.injection.builder;

import com.assignment.injection.module.ViewModelProviderModule;
import com.assignment.injection.scope.ActivityScope;
import com.assignment.presentation.actvities.MainActivity;
import com.assignment.presentation.actvities.PhotoViewerActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class ActivityBuilder {
    @ActivityScope
    @ContributesAndroidInjector(modules = {ViewModelProviderModule.class})
    abstract MainActivity bindMainActivity();

    @ActivityScope
    @ContributesAndroidInjector(modules = {ViewModelProviderModule.class})
    abstract PhotoViewerActivity bindPhotoViewerActivity();

}
