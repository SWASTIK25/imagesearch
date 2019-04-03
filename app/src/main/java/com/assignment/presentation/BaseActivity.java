package com.assignment.presentation;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.assignment.R;
import com.assignment.data.StatusData;
import com.assignment.exceptions.BaseException;
import com.assignment.presentation.viewmodels.common.BaseViewModel;
import com.assignment.presentation.viewmodels.common.SingleLiveEvent;

public abstract class BaseActivity<VM extends BaseViewModel> extends AppCompatActivity {

    private ProgressDialog mProgressBar;
    private VM mViewModel;

    /**
     * abstract impl provide layout which needs to be incorporated with current layout
     *
     * @return layout id
     */
    protected abstract
    @LayoutRes
    int getLayoutId();

    /**
     * abstract impl for init layout view
     */
    protected abstract void initializeViews(Bundle bundle);

    /**
     * current activity theme
     *
     * @return theme
     */
    protected int getThemeOfActivity() {
        return R.style.AppTheme;
    }

    /** abstract impl for init view model
     * */
    protected abstract VM initViewModel();

    /** abstract impl for handle success for request
     * */
    protected abstract void handleViewModelUpdatesOnSuccess(StatusData status);

    /** abstract impl for handling live data
     * */
    protected abstract void handleLiveData();

    /** abstract impl for handle failure for request
     * */
    protected abstract void handleViewModelUpdatesOnFailure(StatusData status, Throwable throwable);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setTheme(getThemeOfActivity());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frame_base);
        mProgressBar = new ProgressDialog(this);
        FrameLayout mContentLayout = (FrameLayout) findViewById(R.id.base_frame);
        int id = getLayoutId();
        View contentView = getLayoutInflater().inflate(id, null);
        mContentLayout.addView(contentView);
        initializeViews(savedInstanceState);
        mViewModel = initViewModel();
        if (mViewModel != null)
            subscribeStatusEvent(mViewModel.getSingleLiveEvent());

        handleLiveData();
    }


    /**
     * subscribe singleLiveEvent for knowing status of request
     *
     * @param singleLiveEvent : Single Live Data for updating status for request
     */
    private void subscribeStatusEvent(SingleLiveEvent<StatusData> singleLiveEvent) {
        singleLiveEvent.observe(this, statusData -> {
            if (statusData != null && statusData.getStatus() != null) {
                switch (statusData.getStatus()) {
                    case SUCCESS:
                        handleViewModelUpdatesOnSuccess(statusData);
                        break;
                    case FAIL:
                        handleViewModelUpdatesOnFailure(statusData, statusData.getThrowable());
                        break;
                }
            } else {
                handleViewModelUpdatesOnFailure(statusData, new BaseException("Status Data is null"));
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    /** Provide associated view model
     * */
    @NonNull
    protected VM getViewModel() {
        return mViewModel;
    }

    /**
     * show loader
     */
    public void showLoader() {
        showLoader(null);
    }

    /**
     * show loader
     *
     * @param msg : loader message
     */
    public void showLoader(String msg) {
        mProgressBar.setCancelable(false);
        if (TextUtils.isEmpty(msg)) {
            mProgressBar.setMessage(getString(R.string.please_wait));
        } else {
            mProgressBar.setMessage(msg);
        }
        mProgressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressBar.show();
    }

    /*hide loader */
    public void hideLoader() {
        mProgressBar.dismiss();
    }
}
