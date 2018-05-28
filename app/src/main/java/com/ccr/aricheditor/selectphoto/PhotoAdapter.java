package com.ccr.aricheditor.selectphoto;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.IdRes;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.ccr.aricheditor.R;
import com.ccr.aricheditor.utils.PhotoPickerIntent;


import java.io.File;
import java.util.ArrayList;

/**
 * Created by txf on 2016/1/27.
 */
public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder> {

    public ArrayList<String> photoPaths = new ArrayList<String>();
    private LayoutInflater inflater;

    private Context mContext;

    public final static int DELETE_REQUEST_CODE = 0x22;

    private final int TYPE_1 = 1; // 类型1
    private final int TYPE_2 = 2; // 类型2
    private int maxLength = 6;

    public PhotoAdapter(Context mContext, ArrayList<String> photoPaths) {
        this.photoPaths = photoPaths;
        this.mContext = mContext;
        inflater = LayoutInflater.from(mContext);

    }

    public PhotoAdapter(Context mContext, ArrayList<String> photoPaths, int maxLength) {
        this.photoPaths = photoPaths;
        this.mContext = mContext;
        inflater = LayoutInflater.from(mContext);
        this.maxLength = maxLength;
    }


    @Override
    public PhotoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = inflater.inflate(R.layout.add_item_photo, parent, false);

        PhotoViewHolder mViewHolder = new PhotoViewHolder(itemView);

        return mViewHolder;
    }


    @Override
    public int getItemViewType(int position) {

        Log.e("getItemViewType", position + "");
        Log.e("photoPaths", photoPaths.size() + "");

        int p = 0;

        if (photoPaths.size() == 0) {
            p = TYPE_1;
        } else if (maxLength >= 6 && position + 1 > photoPaths.size()) {
            p = TYPE_1;
        } else if (position + 1 == maxLength && photoPaths.size() != maxLength) {
            p = TYPE_1;
        } else {
            p = TYPE_2;
        }

        return p;
    }


    @Override
    public void onBindViewHolder(final PhotoViewHolder holder, final int position) {

        switch (getItemViewType(position)) {
            case TYPE_1:


                holder.ivPhoto.setImageResource(R.mipmap.img_up_pic);

                holder.ivPhoto.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        checkPermission(v.getId());
                    }
                });
                break;
            case TYPE_2:

                if (photoPaths.get(position).contains("http://")) {
                    Glide.with(mContext)
                            .load(photoPaths.get(position))
                            .centerCrop()
                            .thumbnail(0.1f)
                            .placeholder(R.drawable.ic_photo_black_48dp)
                            .error(R.drawable.ic_broken_image_black_48dp)
                            .into(holder.ivPhoto);

                } else {

                    Uri uri = Uri.fromFile(new File(photoPaths.get(position)));
                    Glide.with(mContext)
                            .load(uri)
                            .centerCrop()
                            .thumbnail(0.1f)
                            .placeholder(R.drawable.ic_photo_black_48dp)
                            .error(R.drawable.ic_broken_image_black_48dp)
                            .into(holder.ivPhoto);
                }


                holder.ivPhoto.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        previewPhoto(position);
                    }
                });

                break;
        }

    }


    /**
     * 预览照片
     *
     * @param position
     */
    public void previewPhoto(int position) {
        Intent intent = new Intent(mContext, PhotoPagerActivity.class);
        intent.putExtra(PhotoPagerActivity.EXTRA_CURRENT_ITEM, position);
        intent.putExtra(PhotoPagerActivity.EXTRA_PHOTOS, photoPaths);
        intent.putExtra(PhotoPagerActivity.EXTRA_SHOW_DELETE, true);
        ((Activity) mContext).startActivityForResult(intent, DELETE_REQUEST_CODE);
    }


    private void checkPermission(int viewId) {

        int permissionState = ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE);


        if (permissionState != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) mContext, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                ActivityCompat.requestPermissions((Activity) mContext, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE);
            }
        } else {
            // Permission granted
            onClick(viewId);
        }
    }

    public final static int REQUEST_CODE = 0x11;

    private void onClick(@IdRes int viewId) {

        PhotoPickerIntent intent = new PhotoPickerIntent(mContext);
        intent.setPhotoCount(maxLength - photoPaths.size());
        ((Activity) mContext).startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    public int getItemCount() {


        if (this.maxLength < 6)
            return maxLength;

        return photoPaths.size() == 6 ? photoPaths.size() : photoPaths.size() + 1;
    }


    public static class PhotoViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivPhoto;
        private View vSelected;

        public PhotoViewHolder(View itemView) {
            super(itemView);
            ivPhoto = (ImageView) itemView.findViewById(R.id.iv_photo);
            vSelected = itemView.findViewById(R.id.v_selected);
            vSelected.setVisibility(View.GONE);
        }
    }


}