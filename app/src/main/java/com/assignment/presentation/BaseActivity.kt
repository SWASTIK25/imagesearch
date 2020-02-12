package com.assignment.presentation

import android.app.ProgressDialog
import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import android.text.TextUtils
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import androidx.lifecycle.Observer

import com.assignment.R
import com.assignment.exceptions.BaseException
import com.assignment.presentation.viewmodels.common.BaseViewModel
import com.assignment.presentation.viewmodels.common.SingleLiveEvent
import com.model.StatusData

abstract class BaseActivity<VM : BaseViewModel> : AppCompatActivity() {

    private var mProgressBar: ProgressDialog? = null
    /** Provide associated view model
     */
    protected var viewModel: VM? = null
        private set

    /**
     * abstract impl provide layout which needs to be incorporated with current layout
     *
     * @return layout id
     */
    @get:LayoutRes
    protected abstract val layoutId: Int

    /**
     * current activity theme
     *
     * @return theme
     */
    protected val themeOfActivity: Int
        get() = R.style.AppTheme

    /**
     * abstract impl for init layout view
     */
    protected abstract fun initializeViews(bundle: Bundle?)

    /** abstract impl for init view model
     */
    protected abstract fun initViewModel(): VM

    /** abstract impl for handle success for request
     */
    protected abstract fun handleViewModelUpdatesOnSuccess(status: StatusData?)

    /** abstract impl for handling live data
     */
    protected abstract fun handleLiveData()

    /** abstract impl for handle failure for request
     */
    protected abstract fun handleViewModelUpdatesOnFailure(status: StatusData?, throwable: Throwable?)

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(themeOfActivity)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.frame_base)
        mProgressBar = ProgressDialog(this)
        val mContentLayout = findViewById<View>(R.id.base_frame) as FrameLayout
        val id = layoutId
        val contentView = layoutInflater.inflate(id, null)
        mContentLayout.addView(contentView)
        initializeViews(savedInstanceState)
        viewModel = initViewModel()
        if (viewModel != null)
            subscribeStatusEvent(viewModel!!.singleLiveEvent)

        handleLiveData()
    }


    /**
     * subscribe singleLiveEvent for knowing status of request
     *
     * @param singleLiveEvent : Single Live Data for updating status for request
     */
    private fun subscribeStatusEvent(singleLiveEvent: SingleLiveEvent<StatusData>) {
        singleLiveEvent.observe(this, Observer {
            if (it?.status != null) {
                when (it.status) {
                    StatusData.Status.SUCCESS -> handleViewModelUpdatesOnSuccess(it)
                    StatusData.Status.FAIL -> handleViewModelUpdatesOnFailure(it, it.throwable)
                }
            } else {
                handleViewModelUpdatesOnFailure(it, BaseException("Status Data is null"))
            }
        } )

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {

            android.R.id.home -> onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * show loader
     *
     * @param msg : loader message
     */
    @JvmOverloads
    fun showLoader(msg: String? = null) {
        mProgressBar!!.setCancelable(false)
        if (TextUtils.isEmpty(msg)) {
            mProgressBar!!.setMessage(getString(R.string.please_wait))
        } else {
            mProgressBar!!.setMessage(msg)
        }
        mProgressBar!!.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        mProgressBar!!.show()
    }

    /*hide loader */
    fun hideLoader() {
        mProgressBar!!.dismiss()
    }
}
/**
 * show loader
 */
