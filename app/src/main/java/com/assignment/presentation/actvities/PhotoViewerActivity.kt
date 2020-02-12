package com.assignment.presentation.actvities

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager
import com.assignment.R
import com.assignment.common.Constants
import com.assignment.data.PhotoSearchRepository
import com.assignment.presentation.BaseActivity
import com.assignment.presentation.adapters.PhotoViewerAdapter
import com.assignment.presentation.helpers.IInternetStatus
import com.assignment.presentation.helpers.InternetStatusImpl
import com.assignment.presentation.viewmodels.PhotoViewerModel
import com.assignment.presentation.viewmodels.ViewModelFactory
import com.assignment.presentation.viewmodels.common.BaseViewModel
import com.assignment.presentation.viewmodels.common.SingleLiveEvent
import com.assignment.usecase.DeletePhotos
import com.assignment.usecase.Intractors
import com.assignment.usecase.SearchPhotos
import com.assignment.usecase.StoreDataLocally
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.model.PhotoItem
import com.model.StatusData
import dagger.android.AndroidInjection
import java.util.*
import javax.inject.Inject

class PhotoViewerActivity : BaseActivity<PhotoViewerModel>() {
    private var mToolbar: Toolbar? = null
    private var mPhotoCountTextView: TextView? = null
    private var mImageViewPager: ViewPager? = null
    private var mPhotos: List<PhotoItem>? = null
    private var mSelectedPosition: Int = 0
    private var mCurrentPosition: Int = 0

    @Inject
    lateinit var gson: Gson

    @Inject
    lateinit var photoSearchRepository:PhotoSearchRepository

    @Inject
    lateinit var internetStatusImpl: InternetStatusImpl

    override val layoutId: Int
        get() = R.layout.activity_full_screen

    override fun initializeViews(bundle: Bundle?) {
        AndroidInjection.inject(this)
        mToolbar = findViewById(R.id.toolbar)
        setSupportActionBar(mToolbar)
        if (supportActionBar != null) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(false)
            supportActionBar!!.setDisplayShowHomeEnabled(false)
            supportActionBar!!.setTitle("")
        }

        mPhotoCountTextView = findViewById(R.id.tv_photo_count)
        mImageViewPager = findViewById(R.id.vp_image)
        getDataFromIntent()

        mPhotos?.let {
            val countOnTextView = (mSelectedPosition + 1).toString() + "/" + it.size
            mPhotoCountTextView!!.text = countOnTextView
            val photoViewerAdapter = PhotoViewerAdapter(this, it)
            mImageViewPager!!.adapter = photoViewerAdapter

            mImageViewPager!!.currentItem = mSelectedPosition

            mImageViewPager!!.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                override fun onPageScrolled(i: Int, v: Float, i1: Int) {

                }

                override fun onPageSelected(i: Int) {
                    mCurrentPosition = i + 1
                    val countOnTextView = i.toString() + "/" + it.size
                    mPhotoCountTextView!!.text = countOnTextView
                }

                override fun onPageScrollStateChanged(i: Int) {

                }
            })
        }


    }

    private fun getDataFromIntent() {
        val type = object : TypeToken<ArrayList<PhotoItem>>() {

        }.type
        mPhotos = gson!!.fromJson<List<PhotoItem>>(intent.getStringExtra(Constants.Extras.PHOTOS), type)
        mSelectedPosition = intent.getIntExtra(Constants.Extras.SELECTED_POSITION, 0)
        mCurrentPosition = mSelectedPosition
    }

    override fun initViewModel(): PhotoViewerModel {
        val viewModelFectory = ViewModelFactory(this.application,
                Intractors(SearchPhotos(photoSearchRepository), DeletePhotos(photoSearchRepository), StoreDataLocally(photoSearchRepository))
                , SingleLiveEvent<StatusData>(),internetStatusImpl)
        return ViewModelProviders.of(this,viewModelFectory).get(PhotoViewerModel::class.java)
       /* return ViewModelProviders.of(this@PhotoViewerActivity,
                Intractors(SearchPhotos(photoSearchRepository), DeletePhotos(photoSearchRepository), StoreDataLocally(photoSearchRepository))
                , SingleLiveEvent<StatusData>(),internetStatusImpl).get(PhotoViewerModel::class.java)*/
    }

    override fun onBackPressed() {

        if (mSelectedPosition == mCurrentPosition) {
            super.onBackPressed()
        } else {
            finish()
        }

    }

    override fun handleViewModelUpdatesOnSuccess(status: StatusData?) {

    }

    override fun handleLiveData() {

    }

    override fun handleViewModelUpdatesOnFailure(status: StatusData?, throwable: Throwable?) {

    }
}
