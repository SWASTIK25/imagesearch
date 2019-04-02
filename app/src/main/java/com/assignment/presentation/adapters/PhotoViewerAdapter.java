package com.assignment.presentation.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.assignment.R;
import com.assignment.data.SearchPhotoDataModel;
import com.bumptech.glide.Glide;

import java.util.List;

public class PhotoViewerAdapter extends PagerAdapter {

    private Context mContext;
    private List<SearchPhotoDataModel.PhotoItem> mPhotos;

    public PhotoViewerAdapter(Context mContext, List<SearchPhotoDataModel.PhotoItem> mPhotos) {
        this.mContext = mContext;
        this.mPhotos = mPhotos;
    }

    @Override
    public int getCount() {
        return mPhotos.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == ((ImageView) o);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        ImageView photoImageView = (ImageView) LayoutInflater.from(mContext).inflate(R.layout.layout_photo_viewer_item, container, false);
        String url = "https://farm" +
                mPhotos.get(position).getFarm() +
                ".staticflickr.com/" +
                mPhotos.get(position).getServer() +
                "/" +
                mPhotos.get(position).getId() +
                "_" +
                mPhotos.get(position).getSecret() +
                "_z.jpg";
        Glide.with(mContext).
                load(url)
                .into(photoImageView);

        container.addView(photoImageView);
        return photoImageView;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((ImageView) object);
    }
}