package com.assignment.presentation.adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.assignment.R;
import com.assignment.data.SearchPhotoDataModel;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;

import java.util.List;

public class PhotoViewerAdapter extends PagerAdapter {

    private final RequestManager mGlideRequestManager;
    private Context mContext;
    private List<SearchPhotoDataModel.PhotoItem> mPhotos;

    public PhotoViewerAdapter(Context mContext, List<SearchPhotoDataModel.PhotoItem> mPhotos) {
        this.mContext = mContext;
        this.mPhotos = mPhotos;
        mGlideRequestManager = Glide.with(mContext);
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
        /*String url = "https://farm" +
                mPhotos.get(position).getFarm() +
                ".staticflickr.com/" +
                mPhotos.get(position).getServer() +
                "/" +
                mPhotos.get(position).getId() +
                "_" +
                mPhotos.get(position).getSecret() +
                "_z.jpg";*/
        mGlideRequestManager.
                load(mPhotos.get(position).getPhotoUrl()).placeholder(R.drawable.ic_placeholder).error(R.drawable.ic_error_outline_black_24dp)
                .into(photoImageView);

        container.addView(photoImageView);
        return photoImageView;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((ImageView) object);
    }
}
