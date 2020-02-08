package com.assignment.injection.component;

import com.assignment.common.AssignmentApp;
import com.assignment.injection.builder.ActivityBuilder;
import com.assignment.injection.module.ApiModule;
import com.assignment.injection.module.AppModule;
import com.assignment.injection.module.ViewModelProviderModule;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import dagger.android.AndroidInjectionModule;

@Singleton
@Component(modules = {AndroidInjectionModule.class, AppModule.class, ApiModule.class,  ActivityBuilder.class})
public interface AppComponent {

    void inject(AssignmentApp application);

    @Component.Builder
    interface Builder {
        AppComponent build();

        @BindsInstance
        Builder application(AssignmentApp application);
    }
}
