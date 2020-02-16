package com.assignment.presentation.actvities

import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager
import com.assignment.BR
import com.assignment.R
import com.assignment.common.Constants
import com.assignment.databinding.ActivityFullScreenBinding
import com.assignment.presentation.BaseActivity
import com.assignment.presentation.adapters.PhotoViewerAdapter
import com.assignment.presentation.helpers.InternetStatusImpl
import com.assignment.presentation.viewmodels.PhotoViewerModel
import com.assignment.presentation.viewmodels.ViewModelFactory
import com.assignment.presentation.viewmodels.common.SingleLiveEvent
import com.assignment.usecase.Intractors
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.model.PhotoItem
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_full_screen.*
import java.util.*
import javax.inject.Inject

class PhotoViewerActivity : BaseActivity<ActivityFullScreenBinding, PhotoViewerModel>() {

    private var mPhotos: List<PhotoItem>? = null
    private var mSelectedPosition: Int = 0
    private var mCurrentPosition: Int = 0
    @Inject
    lateinit var gson: Gson
    @Inject
    lateinit var internetStatusImpl: InternetStatusImpl
    @Inject
    lateinit var intractors: Intractors

    override fun getLayoutId(): Int {
        return R.layout.activity_full_screen
    }

    override fun initializeViews(bundle: Bundle?) {
        AndroidInjection.inject(this)
        setSupportActionBar(toolbar)
        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(false)
            it.setDisplayShowHomeEnabled(false)
            it.title = ""
        }
    }

    override fun onResume() {
        super.onResume()
        getDataFromIntent()

        mPhotos?.let {
            viewModel?.currentPage?.set((mSelectedPosition + 1).toString() + "/" + it.size)
            vp_image?.adapter = PhotoViewerAdapter(this, it)
            vp_image?.currentItem = mSelectedPosition
            vp_image?.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                override fun onPageScrolled(i: Int, v: Float, i1: Int) {
                    Log.v("PhotoViewActivity", "onPageScrolled")
                }

                override fun onPageSelected(i: Int) {
                    mCurrentPosition = i + 1
                    viewModel?.currentPage?.set(mCurrentPosition.toString() + "/" + it.size)
                }

                override fun onPageScrollStateChanged(i: Int) {
                    Log.v("PhotoViewActivity", "onPageScrollStateChanged")
                }
            })
        }

    }
    private fun getDataFromIntent() {
        val type = object : TypeToken<ArrayList<PhotoItem>>() {

        }.type
        mPhotos = gson.fromJson<List<PhotoItem>>(intent.getStringExtra(Constants.Extras.PHOTOS), type)
        mSelectedPosition = intent.getIntExtra(Constants.Extras.SELECTED_POSITION, 0)
        mCurrentPosition = mSelectedPosition
    }

    override fun initViewModel(): PhotoViewerModel {
        val viewModelFactory = ViewModelFactory(this.application, intractors
                , SingleLiveEvent(), internetStatusImpl)
        return ViewModelProviders.of(this, viewModelFactory).get(PhotoViewerModel::class.java)
    }

    override fun onBackPressed() {
        if (mSelectedPosition == mCurrentPosition) {
            super.onBackPressed()
        } else {
            finish()
        }

    }

    override fun getBindingVariable(): Int {
        return BR.viewmodel
    }

}
