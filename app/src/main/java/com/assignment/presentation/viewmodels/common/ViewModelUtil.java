package com.assignment.presentation.viewmodels.common;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import javax.inject.Inject;

/**
 * Creates a one off view model factory for the given view model instance.
 */
public class ViewModelUtil {

    @Inject
    public ViewModelUtil() {
    }

    public <T extends ViewModel> ViewModelProvider.Factory createFor(@NonNull final T viewModel) {
        return new ViewModelProvider.Factory() {

            @Override
            public <T extends ViewModel> T create(Class<T> modelClass) {
                if (modelClass.isAssignableFrom(viewModel.getClass())) {
                    return (T) viewModel;
                }
                throw new IllegalArgumentException("unexpected viewModel class " + modelClass);
            }
        };
    }

}
