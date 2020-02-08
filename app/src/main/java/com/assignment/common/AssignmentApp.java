package com.assignment.common;

import android.app.Activity;
import android.app.Application;
import com.assignment.injection.component.AppComponent;
import com.assignment.injection.component.DaggerAppComponent;

import javax.inject.Inject;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasActivityInjector;

public class AssignmentApp extends Application implements HasActivityInjector {

    private static AssignmentApp application;
    private AppComponent mAppComponent;

    @Inject
    DispatchingAndroidInjector<Activity> dispatchingAndroidInjector;

    @Override
    public void onCreate() {
        super.onCreate();
        AssignmentApp.application = this;
        createAppComponent();
    }

    public static AssignmentApp getApplication() {
        return application;
    }

    private void createAppComponent() {
        mAppComponent = DaggerAppComponent.builder().application(this).build();
        mAppComponent.inject(this);
    }

    public AppComponent getAppComponent() {
        return mAppComponent;
    }

    @Override
    public AndroidInjector<Activity> activityInjector() {
        return dispatchingAndroidInjector;
    }
}
