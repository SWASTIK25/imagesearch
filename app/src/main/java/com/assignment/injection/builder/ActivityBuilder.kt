package com.assignment.injection.builder

import com.assignment.injection.module.ViewModelProviderModule
import com.assignment.injection.scope.ActivityScope
import com.assignment.presentation.actvities.MainActivity
import com.assignment.presentation.actvities.PhotoViewerActivity

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBuilder {
    @ActivityScope
    @ContributesAndroidInjector(modules = [ViewModelProviderModule::class])
    abstract fun bindMainActivity(): MainActivity

    @ActivityScope
    @ContributesAndroidInjector(modules = [ViewModelProviderModule::class])
    abstract fun bindPhotoViewerActivity(): PhotoViewerActivity

}
