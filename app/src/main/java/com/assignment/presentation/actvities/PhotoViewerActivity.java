package com.assignment.presentation.actvities;

import android.os.Bundle;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.widget.Toolbar;
import android.widget.TextView;

import com.assignment.R;
import com.assignment.common.Constants;
import com.assignment.data.SearchPhotoDataModel;
import com.assignment.data.StatusData;
import com.assignment.presentation.BaseActivity;
import com.assignment.presentation.adapters.PhotoViewerAdapter;
import com.assignment.presentation.viewmodels.common.BaseViewModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.android.AndroidInjection;

public class PhotoViewerActivity extends BaseActivity {
    private Toolbar mToolbar;
    private TextView mPhotoCountTextView;
    private ViewPager mImageViewPager;
    private List<SearchPhotoDataModel.PhotoItem> mPhotos;
    private int mSelectedPosition, mCurrentPosition;

    @Inject
    Gson gson;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_full_screen;
    }

    @Override
    protected void initializeViews(Bundle bundle) {
        AndroidInjection.inject(this);
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setDisplayShowHomeEnabled(false);
            getSupportActionBar().setTitle("");
        }

        mPhotoCountTextView = findViewById(R.id.tv_photo_count);
        mImageViewPager = findViewById(R.id.vp_image);
        getDataFromIntent();

        if (mPhotos != null && mPhotos.size() != 0) {
            String countOnTextView = mSelectedPosition+1 + "/" + mPhotos.size();
            mPhotoCountTextView.setText(countOnTextView);
            PhotoViewerAdapter photoViewerAdapter = new PhotoViewerAdapter(this, mPhotos);
            mImageViewPager.setAdapter(photoViewerAdapter);

            mImageViewPager.setCurrentItem(mSelectedPosition);

            mImageViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int i, float v, int i1) {

                }

                @Override
                public void onPageSelected(int i) {
                    mCurrentPosition = i+1;
                    String countOnTextView = i + "/" + mPhotos.size();
                    mPhotoCountTextView.setText(countOnTextView);
                }

                @Override
                public void onPageScrollStateChanged(int i) {

                }
            });
        }


    }

    private void getDataFromIntent() {
        Type type = new TypeToken<ArrayList<SearchPhotoDataModel.PhotoItem>>() {
        }.getType();
        mPhotos = gson.fromJson(getIntent().getStringExtra(Constants.Extras.PHOTOS), type);
        mSelectedPosition = getIntent().getIntExtra(Constants.Extras.SELECTED_POSITION, 0);
        mCurrentPosition = mSelectedPosition;
    }

    @Override
    protected BaseViewModel initViewModel() {
        return null;
    }

    @Override
    public void onBackPressed() {

        if (mSelectedPosition == mCurrentPosition){
            super.onBackPressed();
        }else {
            finish();
        }

    }

    @Override
    protected void handleViewModelUpdatesOnSuccess(StatusData status) {

    }

    @Override
    protected void handleLiveData() {

    }

    @Override
    protected void handleViewModelUpdatesOnFailure(StatusData status, Throwable throwable) {

    }
}
