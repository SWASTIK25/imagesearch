package com.assignment.presentation

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.assignment.R
import com.assignment.presentation.viewmodels.common.BaseViewModel

abstract class BaseActivity<BindingClass : ViewDataBinding, VM : BaseViewModel> : AppCompatActivity() {

    /** Provide associated view model
     */
    protected var viewModel: VM? = null
    private var mViewDataBinding: BindingClass? = null
    /**
     * abstract impl provide layout which needs to be incorporated with current layout
     *
     * @return layout id
     */
    protected abstract fun getLayoutId(): Int

    /**
     * current activity theme
     *
     * @return theme
     */
    private val themeOfActivity: Int
        get() = R.style.AppTheme

    /**
     * abstract impl for init layout view
     */
    protected abstract fun initializeViews(bundle: Bundle?)

    /** abstract impl for init view model
     */
    protected abstract fun initViewModel(): VM

    /**
     * get binding variable
     */
    protected abstract fun getBindingVariable(): Int

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(themeOfActivity)
        super.onCreate(savedInstanceState)
        mViewDataBinding =DataBindingUtil.setContentView(this,getLayoutId())
        initializeViews(savedInstanceState)
        viewModel = initViewModel()
        if (getBindingVariable() >= 0)
            mViewDataBinding?.setVariable(getBindingVariable(), viewModel)
        mViewDataBinding?.executePendingBindings()

    }

    override fun onDestroy() {
        super.onDestroy()
        mViewDataBinding = null
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {

            android.R.id.home -> onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

}
