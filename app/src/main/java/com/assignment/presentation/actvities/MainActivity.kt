package com.assignment.presentation.actvities

import android.Manifest
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityOptionsCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.assignment.R
import com.assignment.common.Constants
import com.assignment.data.PhotoSearchRepository
import com.assignment.domain.model.SearchPhotoRequest
import com.assignment.presentation.BaseActivity
import com.assignment.presentation.adapters.PhotoGalleryAdapter
import com.assignment.presentation.helpers.IInternetStatus
import com.assignment.presentation.helpers.InternetStatusImpl
import com.assignment.presentation.viewmodels.MainViewModel
import com.assignment.presentation.viewmodels.ViewModelFactory
import com.assignment.presentation.viewmodels.common.SingleLiveEvent
import com.assignment.usecase.Intractors
import com.google.gson.Gson
import com.interfaces.ILoadMore
import com.interfaces.IRecyclerItemClicked
import com.model.PhotoItem
import com.model.Photos
import com.model.StatusData
import dagger.android.AndroidInjection
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import javax.inject.Inject

class MainActivity : BaseActivity<MainViewModel>(), EasyPermissions.PermissionCallbacks {
    private var mToolbar: Toolbar? = null
    private var mSearchEditText: EditText? = null
    private var mPlaceHolder: TextView? = null
    private var mPhotoRecyclerView: RecyclerView? = null
    private var mPageNum = 1
    private val mItemPerPage = 20
    private var mPhotoItems = mutableListOf<PhotoItem>()
    private var mGridLayoutManager: GridLayoutManager? = null
    private var mPhotoGalleryAdapter: PhotoGalleryAdapter? = null
    private var mRefreshLayout: SwipeRefreshLayout? = null
    private var mCrossImageView: ImageView? = null

    @Inject
    internal lateinit var mSearchPhotoRequest: SearchPhotoRequest

    @Inject
    lateinit var intractors: Intractors

    @Inject
    lateinit var photoSearchRepository:PhotoSearchRepository

    @Inject
    lateinit var internetStatusImpl:InternetStatusImpl

    @Inject
    lateinit var mGson: Gson

    @Inject
    lateinit var mInternetStatus: IInternetStatus

    override val layoutId: Int
        get() = R.layout.activity_main


    override fun initViewModel(): MainViewModel {
        val viewModelFactory = ViewModelFactory(this.application,
                intractors,SingleLiveEvent(),internetStatusImpl)
        return ViewModelProviders.of(this,viewModelFactory).get(MainViewModel::class.java)
    }

    override fun initializeViews(bundle: Bundle?) {
        AndroidInjection.inject(this)
        mToolbar = findViewById(R.id.toolbar)
        mPlaceHolder = findViewById(R.id.txt_empty)
        setSupportActionBar(mToolbar)
        if (supportActionBar != null) {
            supportActionBar?.setDisplayHomeAsUpEnabled(false)
            supportActionBar?.setDisplayShowHomeEnabled(false)
            supportActionBar?.setTitle("")
        }

        mSearchEditText = findViewById(R.id.et_search)
        mRefreshLayout = findViewById(R.id.refresh)
        mPhotoRecyclerView = findViewById(R.id.rv_image_gallery)
        val backImageView = findViewById<ImageView>(R.id.iv_back)
        mCrossImageView = findViewById(R.id.iv_cross)


        mRefreshLayout!!.setOnRefreshListener { mRefreshLayout!!.isRefreshing = false }
        backImageView.setOnClickListener {

            /*DialogUtils.doAlert(this@MainActivity,
                    "Do you want to exit?",
                    "Yes", { this.finish() }, "No", null)*/
        }

        mCrossImageView!!.setOnClickListener { v ->
            mSearchEditText!!.setText("")
            mPhotoItems.clear()
            mPhotoGalleryAdapter!!.notifyDataSetChanged()
            mPlaceHolder!!.visibility = View.VISIBLE
        }

        doPhotoLoadingOnView()
    }
    override fun handleViewModelUpdatesOnSuccess(status: StatusData?) {
    }

    override fun handleViewModelUpdatesOnFailure(status: StatusData?, throwable: Throwable?) {
    }
    private fun doPhotoLoadingOnView() {
        mGridLayoutManager = GridLayoutManager(this, 2)
        mPhotoRecyclerView!!.layoutManager = mGridLayoutManager
        mPhotoGalleryAdapter = PhotoGalleryAdapter(this, mPhotoItems)
        mPhotoRecyclerView!!.adapter = mPhotoGalleryAdapter
        mPhotoRecyclerView!!.addItemDecoration(VerticalSpaceItemDecoration(3))
        mSearchEditText!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

                if (s.toString().trim { it <= ' ' }.length >= 1) {
                    mCrossImageView!!.visibility = View.VISIBLE
                } else {
                    mCrossImageView!!.visibility = View.GONE
                }
            }

            override fun afterTextChanged(s: Editable) {

            }
        })

        mSearchEditText!!.setOnEditorActionListener { v, actionId, event ->

            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                requestPermissionsAndGetPhotos()
            }
            false
        }
        mSearchPhotoRequest.pageSize = mItemPerPage

        mPhotoGalleryAdapter?.setLoadMoreListener(object:ILoadMore{
            override fun loadMore() {
                if (mInternetStatus.isConnected) {
                    mPageNum++
                    mRefreshLayout?.isRefreshing = true
                    mSearchPhotoRequest.page = mPageNum
                    viewModel?.search(mSearchPhotoRequest)
                }
            }
        } )
        mPhotoGalleryAdapter?.setRecyclerItemClicked(object :IRecyclerItemClicked<PhotoItem>{
            override fun onItemClicked(index: Int, data: PhotoItem, view: View) {
                val intent = Intent(this@MainActivity, PhotoViewerActivity::class.java)
                intent.putExtra(Constants.Extras.PHOTOS, mGson.toJson(mPhotoItems))
                intent.putExtra(Constants.Extras.SELECTED_POSITION, index)
                val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this@MainActivity, view, "photo")

                startActivity(intent, options.toBundle())
            }

        })

        if (mPhotoItems.size < 1) {
            mPlaceHolder?.visibility = View.VISIBLE
        }
    }

    @AfterPermissionGranted(RC_FILE_STORAGE_APP_PERM)
    private fun requestPermissionsAndGetPhotos() {

        val perms = Manifest.permission.WRITE_EXTERNAL_STORAGE
        if (EasyPermissions.hasPermissions(this, perms)) {
            if (TextUtils.isEmpty(mSearchEditText!!.text.toString().trim { it <= ' ' })) {
                Toast.makeText(this, getString(R.string.info_search_empty), Toast.LENGTH_LONG).show()
                return
            }
            mRefreshLayout?.isEnabled = true
            if (!mRefreshLayout!!.isRefreshing) {
                mRefreshLayout!!.isRefreshing = true
            }

            mPageNum = 1
            mRefreshLayout?.isRefreshing = true
            mSearchPhotoRequest.text = mSearchEditText!!.text.toString().trim { it <= ' ' }
            mSearchPhotoRequest.page = mPageNum
            viewModel?.search(mSearchPhotoRequest)
        } else {
            EasyPermissions.requestPermissions(this@MainActivity, getString(R.string.rationale_app_perm), RC_FILE_STORAGE_APP_PERM, perms)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {
        Log.d(javaClass.name, "onPermissionsGranted:" + requestCode + ":" + perms.size)
    }

    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {
        Log.d(javaClass.name, "onPermissionsDenied:" + requestCode + ":" + perms.size)

        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this)
                    .setTitle(getString(R.string.title_settings_dialog))
                    .setRationale(getString(R.string.rationale_ask_again))
                    .setPositiveButton(getString(R.string.setting))
                    .setNegativeButton(getString(R.string.cancel))
                    .setRequestCode(RC_SETTINGS_SCREEN_PERM)
                    .build()
                    .show()
            return
        }
        this.finish()
    }

    override fun handleLiveData() {
        viewModel?.photoSearchResponseData?.observe(this,object :Observer<Photos>{
            override fun onChanged(photoItems: Photos?) {
                mRefreshLayout?.isRefreshing = false
                photoItems?.photo.let {photoList->
                    photoList?.let {
                        if (mPageNum == 1) {
                            if (it.isEmpty()) {
                                Toast.makeText(this@MainActivity, getString(R.string.no_data_found), Toast.LENGTH_SHORT).show()
                            }
                            mPhotoItems.clear()
                        }
                        if (photoItems?.photo != null) {
                            mPlaceHolder?.visibility = View.GONE
                            mPhotoItems.addAll(it)
                            mPhotoGalleryAdapter?.notifyDataSetChanged()
                        }
                    }

                }

            }

        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = getMenuInflater()
        inflater.inflate(R.menu.menu_main_screen, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.two_columns) {
            mGridLayoutManager!!.spanCount = 2
        } else if (item.itemId == R.id.three_columns) {
            mGridLayoutManager!!.spanCount = 3
        } else if (item.itemId == R.id.four_columns) {
            mGridLayoutManager!!.spanCount = 4
        }
        return super.onOptionsItemSelected(item)
    }

    inner class VerticalSpaceItemDecoration(private val verticalSpaceHeight: Int) : RecyclerView.ItemDecoration() {

        override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {

            outRect.top = verticalSpaceHeight
            outRect.bottom = verticalSpaceHeight
            outRect.left = verticalSpaceHeight
            outRect.right = verticalSpaceHeight
        }


    }

    companion object {
        private const val RC_SETTINGS_SCREEN_PERM = 123
        private const val RC_FILE_STORAGE_APP_PERM = 124
    }

}
