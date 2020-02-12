package com.assignment.common

import android.app.Activity
import android.app.Application
import com.assignment.injection.component.AppComponent
import com.assignment.injection.component.DaggerAppComponent
import javax.inject.Inject
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector

class AssignmentApp : Application(), HasActivityInjector {
    var appComponent: AppComponent? = null
        private set

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Activity>

    override fun onCreate() {
        super.onCreate()
        AssignmentApp.application = this
        createAppComponent()
    }

    private fun createAppComponent() {
        appComponent = DaggerAppComponent.builder().application(this).build()
        appComponent?.inject(this)
    }

    override fun activityInjector(): AndroidInjector<Activity>? {
        return dispatchingAndroidInjector
    }

    companion object {
        lateinit var application: AssignmentApp
    }
}
