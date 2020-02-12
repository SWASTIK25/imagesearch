package com.assignment.presentation.adapters

import android.content.Context
import androidx.viewpager.widget.PagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView

import com.assignment.R
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.model.PhotoItem

class PhotoViewerAdapter(private val mContext: Context, private val mPhotos: List<PhotoItem>) : PagerAdapter() {

    private val mGlideRequestManager: RequestManager

    init {
        mGlideRequestManager = Glide.with(mContext)
    }

    override fun getCount(): Int {
        return mPhotos.size
    }

    override fun isViewFromObject(view: View, o: Any): Boolean {
        return view === o as ImageView
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val photoImageView = LayoutInflater.from(mContext).inflate(R.layout.layout_photo_viewer_item, container, false) as ImageView
        /*String url = "https://farm" +
                mPhotos.get(position).getFarm() +
                ".staticflickr.com/" +
                mPhotos.get(position).getServer() +
                "/" +
                mPhotos.get(position).getId() +
                "_" +
                mPhotos.get(position).getSecret() +
                "_z.jpg";*/
        mGlideRequestManager.load(mPhotos[position].photoUrl).placeholder(R.drawable.ic_placeholder).error(R.drawable.ic_error_outline_black_24dp)
                .into(photoImageView)

        container.addView(photoImageView)
        return photoImageView
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as ImageView)
    }
}
