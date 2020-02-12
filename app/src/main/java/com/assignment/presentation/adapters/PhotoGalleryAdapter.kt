package com.assignment.presentation.adapters

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.assignment.R
import com.assignment.presentation.helpers.widget.SquareImageView
import com.interfaces.ILoadMore
import com.interfaces.IRecyclerItemClicked
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.model.PhotoItem

class PhotoGalleryAdapter(private val mContext: Context, private val mPhotoItems: MutableList<PhotoItem>) : RecyclerView.Adapter<PhotoGalleryAdapter.PhotoGalleryViewHolder>() {
    private var mLoadMoreListener: ILoadMore? = null
    private var mRecyclerItemClicked: IRecyclerItemClicked<PhotoItem>? = null
    private val mGlideRequestManager: RequestManager = Glide.with(mContext)

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): PhotoGalleryViewHolder {
        val v = LayoutInflater.from(mContext).inflate(R.layout.layout_photo_gallery_item, viewGroup, false)
        return PhotoGalleryViewHolder(v)
    }

    override fun onBindViewHolder(photoGalleryViewHolder: PhotoGalleryViewHolder, position: Int) {
        mGlideRequestManager.asDrawable().load(mPhotoItems[position].photoUrl)
                .placeholder(R.drawable.ic_placeholder)
                .error(R.drawable.ic_error_outline_black_24dp)
                .into(photoGalleryViewHolder.mSquareImageView)

        photoGalleryViewHolder.mSquareImageView.setOnClickListener { v ->
            if (mRecyclerItemClicked != null)
                mRecyclerItemClicked!!.onItemClicked(position, mPhotoItems[position], v)
        }

        if (position == itemCount - 1) {
            if (mLoadMoreListener != null)
                mLoadMoreListener!!.loadMore()
        }

    }

    fun setLoadMoreListener(mLoadMoreListener: ILoadMore) {
        this.mLoadMoreListener = mLoadMoreListener
    }

    fun setRecyclerItemClicked(mRecyclerItemClicked: IRecyclerItemClicked<PhotoItem>) {
        this.mRecyclerItemClicked = mRecyclerItemClicked
    }

    override fun getItemCount(): Int {
        return mPhotoItems.size
    }

    class PhotoGalleryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val mSquareImageView:ImageView = itemView.findViewById<ImageView>(R.id.iv_image)
    }
}
