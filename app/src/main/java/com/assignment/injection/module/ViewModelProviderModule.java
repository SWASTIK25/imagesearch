package com.assignment.injection.module;

import android.arch.lifecycle.ViewModelProvider;

import com.assignment.injection.scope.ActivityScope;
import com.assignment.presentation.viewmodels.MainViewModel;
import com.assignment.presentation.viewmodels.common.BaseViewModel;
import com.assignment.presentation.viewmodels.common.ViewModelUtil;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

@Module
public class ViewModelProviderModule {

    @ActivityScope
    @Provides
    @Named("mainViewModel")
    public ViewModelProvider.Factory provideViewModelProviderFactoryForReportView(ViewModelUtil viewModelUtil, MainViewModel viewModel) {
        return viewModelUtil.createFor(viewModel);
    }
}

