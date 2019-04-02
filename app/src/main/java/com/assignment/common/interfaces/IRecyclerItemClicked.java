package com.assignment.common.interfaces;

import android.view.View;

public interface IRecyclerItemClicked<T> {
    void onItemClicked(int index, T data, View view);
}
