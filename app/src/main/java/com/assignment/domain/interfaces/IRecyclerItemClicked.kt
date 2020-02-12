package com.interfaces

import android.view.View

interface IRecyclerItemClicked<T> {
    fun onItemClicked(index: Int, data: T, view: View)
}
