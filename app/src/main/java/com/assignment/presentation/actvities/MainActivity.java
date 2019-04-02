package com.assignment.presentation.actvities;

import android.Manifest;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.assignment.R;
import com.assignment.common.Constants;
import com.assignment.data.SearchPhotoDataModel;
import com.assignment.data.SearchPhotoRequest;
import com.assignment.data.StatusData;
import com.assignment.presentation.BaseActivity;
import com.assignment.presentation.adapters.PhotoGalleryAdapter;
import com.assignment.presentation.helpers.DialogUtils;
import com.assignment.presentation.helpers.LogUtil;
import com.assignment.presentation.viewmodels.MainViewModel;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import dagger.android.AndroidInjection;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends BaseActivity<MainViewModel> implements EasyPermissions.PermissionCallbacks {
    private Toolbar mToolbar;
    private EditText mSearchEditText;
    private TextView mPlaceHolder;
    private RecyclerView mPhotoRecyclerView;
    private int mPageNum = 1;
    private final int ITEMS_PER_PAGE = 20;
    private List<SearchPhotoDataModel.PhotoItem> mPhotoItems;
    private GridLayoutManager mGridLayoutManager;
    private PhotoGalleryAdapter mPhotoGalleryAdapter;
    private SwipeRefreshLayout mRefreshLayout;
    private ImageView mCrossImageView;
    private static final int RC_SETTINGS_SCREEN_PERM = 123;
    private static final int RC_FILE_STORAGE_APP_PERM = 124;
    @Inject
    @Named("mainViewModel")
    ViewModelProvider.Factory mFactory;

    @Inject
    SearchPhotoRequest mSearchPhotoRequest;

    @Inject
    Gson mGson;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initializeViews(Bundle bundle) {
        AndroidInjection.inject(this);
        mToolbar = findViewById(R.id.toolbar);
        mPlaceHolder = findViewById(R.id.txt_empty);
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setDisplayShowHomeEnabled(false);
            getSupportActionBar().setTitle("");
        }

        mSearchEditText = findViewById(R.id.et_search);
        mRefreshLayout = findViewById(R.id.refresh);
        mPhotoRecyclerView = findViewById(R.id.rv_image_gallery);
        ImageView backImageView = findViewById(R.id.iv_back);
        mCrossImageView = findViewById(R.id.iv_cross);


        mRefreshLayout.setOnRefreshListener(() -> {
            mRefreshLayout.setRefreshing(false);
        });
        backImageView.setOnClickListener(v ->
                DialogUtils.doAlert(MainActivity.this
                        , "Do you want to exit?"
                        , "Yes"
                        , this::finish, "No", null));

        mCrossImageView.setOnClickListener(v -> {
            mSearchEditText.setText("");
            mPhotoItems.clear();
            mPhotoGalleryAdapter.notifyDataSetChanged();
            mPlaceHolder.setVisibility(View.VISIBLE);
        });

        doPhotoLoadingOnView();
    }

    @Override
    protected MainViewModel initViewModel() {
        return ViewModelProviders.of(this, mFactory).get(MainViewModel.class);
    }

    private void doPhotoLoadingOnView() {
        mGridLayoutManager = new GridLayoutManager(this, 2);
        mPhotoRecyclerView.setLayoutManager(mGridLayoutManager);
        mPhotoItems = new ArrayList<>();
        mPhotoGalleryAdapter = new PhotoGalleryAdapter(this, mPhotoItems);
        mPhotoRecyclerView.setAdapter(mPhotoGalleryAdapter);
        mPhotoRecyclerView.addItemDecoration(new VerticalSpaceItemDecoration(3));
        mSearchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (s.toString().trim().length() >= 1) {
                    mCrossImageView.setVisibility(View.VISIBLE);
                } else {
                    mCrossImageView.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mSearchEditText.setOnEditorActionListener((v, actionId, event) -> {

            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                requestPermissionsAndGetPhotos();
            }
            return false;
        });
        mSearchPhotoRequest.setPageSize(ITEMS_PER_PAGE);

        mPhotoGalleryAdapter.setLoadMoreListener(() -> {
            mPageNum++;
            mRefreshLayout.setRefreshing(true);
            mSearchPhotoRequest.setPage(mPageNum);
            getViewModel().search(mSearchPhotoRequest);
        });

        mPhotoGalleryAdapter.setRecyclerItemClicked((index, data, view) -> {

            Intent intent = new Intent(MainActivity.this, PhotoViewerActivity.class);
            intent.putExtra(Constants.Extras.PHOTOS, mGson.toJson(mPhotoItems));
            intent.putExtra(Constants.Extras.SELECTED_POSITION, index);
            ActivityOptionsCompat options = ActivityOptionsCompat.
                    makeSceneTransitionAnimation(this, view, "photo");

            startActivity(intent, options.toBundle());
        });

        if (mPhotoItems.size() < 1) {
            mPlaceHolder.setVisibility(View.VISIBLE);
        }
    }

    @AfterPermissionGranted(RC_FILE_STORAGE_APP_PERM)
    private void requestPermissionsAndGetPhotos() {

        String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(this, perms)) {
            if (TextUtils.isEmpty(mSearchEditText.getText().toString().trim())) {
                return;
            }
            mRefreshLayout.setEnabled(true);
            if (!mRefreshLayout.isRefreshing()) {
                mRefreshLayout.setRefreshing(true);
            }

            mPageNum = 1;
            mRefreshLayout.setRefreshing(true);
            mSearchPhotoRequest.setText(mSearchEditText.getText().toString().trim());
            mSearchPhotoRequest.setPage(mPageNum);
            getViewModel().search(mSearchPhotoRequest);
        } else {
            EasyPermissions.requestPermissions(this, getString(R.string.rationale_app_perm), RC_FILE_STORAGE_APP_PERM, perms);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        LogUtil.d(getClass().getName(), "onPermissionsGranted:" + requestCode + ":" + perms.size());
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        LogUtil.d(getClass().getName(), "onPermissionsDenied:" + requestCode + ":" + perms.size());

        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this)
                    .setTitle(getString(R.string.title_settings_dialog))
                    .setRationale(getString(R.string.rationale_ask_again))
                    .setPositiveButton(getString(R.string.setting))
                    .setNegativeButton(getString(R.string.cancel))
                    .setRequestCode(RC_SETTINGS_SCREEN_PERM)
                    .build()
                    .show();
            return;
        }
        this.finish();
    }
    @Override
    protected void handleLiveData() {
        getViewModel().getPhotoSearchResponseData().observe(this, photoItems -> {
            mRefreshLayout.setRefreshing(false);

            if (mPageNum == 1) {
                mPhotoItems.clear();
            }
            if (photoItems != null && photoItems.getPhoto() != null) {
                mPlaceHolder.setVisibility(View.GONE);
                mPhotoItems.addAll(photoItems.getPhoto());
                mPhotoGalleryAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    protected void handleViewModelUpdatesOnSuccess(StatusData status) {

    }

    @Override
    protected void handleViewModelUpdatesOnFailure(StatusData status, Throwable throwable) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main_screen, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.two_columns) {
            mGridLayoutManager.setSpanCount(2);
        } else if (item.getItemId() == R.id.three_columns) {
            mGridLayoutManager.setSpanCount(3);
        } else if (item.getItemId() == R.id.four_columns) {
            mGridLayoutManager.setSpanCount(4);
        }
        return super.onOptionsItemSelected(item);
    }

    public class VerticalSpaceItemDecoration extends RecyclerView.ItemDecoration {

        private final int verticalSpaceHeight;

        public VerticalSpaceItemDecoration(int verticalSpaceHeight) {
            this.verticalSpaceHeight = verticalSpaceHeight;
        }

        @Override
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {

            outRect.top = verticalSpaceHeight;
            outRect.bottom = verticalSpaceHeight;
            outRect.left = verticalSpaceHeight;
            outRect.right = verticalSpaceHeight;
        }


    }

}
