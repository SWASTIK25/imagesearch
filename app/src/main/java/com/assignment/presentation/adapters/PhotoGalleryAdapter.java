package com.assignment.presentation.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.assignment.R;
import com.assignment.common.interfaces.ILoadMore;
import com.assignment.common.interfaces.IRecyclerItemClicked;
import com.assignment.data.SearchPhotoDataModel;
import com.assignment.presentation.helpers.widget.SquareImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;

import java.util.List;

public class PhotoGalleryAdapter extends RecyclerView.Adapter<PhotoGalleryAdapter.PhotoGalleryViewHolder> {

    private Context mContext;
    private List<SearchPhotoDataModel.PhotoItem> mPhotoItems;
    private ILoadMore mLoadMoreListener;
    private IRecyclerItemClicked<SearchPhotoDataModel.PhotoItem> mRecyclerItemClicked;
    private RequestManager mGlideRequestManager;

    public PhotoGalleryAdapter(Context mContext, List<SearchPhotoDataModel.PhotoItem> mPhotoItems) {
        this.mContext = mContext;
        this.mPhotoItems = mPhotoItems;
        mGlideRequestManager = Glide.with(mContext);
    }

    @NonNull
    @Override
    public PhotoGalleryViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.layout_photo_gallery_item, viewGroup, false);
        return new PhotoGalleryViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoGalleryViewHolder photoGalleryViewHolder, int position) {
        mGlideRequestManager.load(mPhotoItems.get(position).getPhotoUrl()).placeholder(R.drawable.ic_placeholder).error(R.drawable.ic_error_outline_black_24dp)
                .into(photoGalleryViewHolder.mSquareImageView);

        photoGalleryViewHolder.mSquareImageView.setOnClickListener(v -> {
            if (mRecyclerItemClicked != null)
                mRecyclerItemClicked.onItemClicked(position, mPhotoItems.get(position), v);
        });

        if (position == getItemCount() - 1) {
            if (mLoadMoreListener != null)
                mLoadMoreListener.loadMore();
        }

    }

    public void setLoadMoreListener(ILoadMore mLoadMoreListener) {
        this.mLoadMoreListener = mLoadMoreListener;
    }

    public void setRecyclerItemClicked(IRecyclerItemClicked<SearchPhotoDataModel.PhotoItem> mRecyclerItemClicked) {
        this.mRecyclerItemClicked = mRecyclerItemClicked;
    }

    @Override
    public int getItemCount() {
        return mPhotoItems.size();
    }

    public static class PhotoGalleryViewHolder extends RecyclerView.ViewHolder {

        private SquareImageView mSquareImageView;

        public PhotoGalleryViewHolder(@NonNull View itemView) {
            super(itemView);
            mSquareImageView = itemView.findViewById(R.id.iv_image);
        }
    }
}
