package com.assignment.presentation.helpers;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.assignment.common.AssignmentApp;

import javax.inject.Inject;

public class InternetStatusImpl implements IInternetStatus {

    private Context context;

    @Inject
    public InternetStatusImpl(AssignmentApp context) {
        this.context = context;
    }

    @Override
    public boolean isConnected() {
        return isNetworkAvailable(context);
    }

    private boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }
}
