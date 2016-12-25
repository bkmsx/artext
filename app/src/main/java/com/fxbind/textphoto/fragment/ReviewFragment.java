package com.fxbind.textphoto.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.fxbind.textphoto.R;

/**
 * Created by bkmsx on 12/24/2016.
 */

public class ReviewFragment extends Fragment {
    static  Context mContext;
    static String mImagePath;
    public static ReviewFragment newInstance(Context context, String imagePath) {
        mContext = context;
        mImagePath = imagePath;
        return new ReviewFragment();
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.review_fragment, null);
        ImageView imageView = (ImageView) view.findViewById(R.id.image_view);
        Glide.with(mContext).load(mImagePath).into(imageView);
        return view;
    }
}
