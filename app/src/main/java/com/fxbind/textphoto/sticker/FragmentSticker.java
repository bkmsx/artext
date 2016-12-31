package com.fxbind.textphoto.sticker;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.fxbind.textphoto.R;
import com.fxbind.textphoto.helper.Utils;
import com.fxbind.textphoto.interfaces.OnStickerClickListener;
import com.fxbind.textphoto.main.MainActivity;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by bkmsx on 12/29/2016.
 */

public class FragmentSticker extends Fragment {
    public static final String STICKER_FOLDER = "stickers/emoticons";
    public static final String ASSETS_PATH = "file:///android_asset/";

    static MainActivity mActivity;
    ArrayList<String> listSticker;
    OnStickerClickListener mCallback;

    public static FragmentSticker newInstance(MainActivity activity) {
        mActivity = activity;
        return new FragmentSticker();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_image_gallery, null);
        GridView gridView = (GridView) view.findViewById(R.id.grid_view);
        listSticker = Utils.listFilesFromAssets(mActivity, STICKER_FOLDER);
        StickerAdapter stickerAdapter = new StickerAdapter(mActivity, R.layout.sticker_item, listSticker);
        gridView.setAdapter(stickerAdapter);
        gridView.setNumColumns(5);
        gridView.setOnItemClickListener(onItemClickListener);
        mCallback = mActivity.mTextFragment;
        return view;
    }

    AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String assestPath = listSticker.get(position);
            String[] split = assestPath.split("/");
            String name = split[split.length-1];
            String imagePath = Utils.getTempFolder() + "/" + name;
            Utils.copyFileFromAssets(mActivity, assestPath, imagePath);
            mCallback.onStickerClick(imagePath);
            log(imagePath);
        }
    };

    class StickerAdapter extends ArrayAdapter<String> {

        public StickerAdapter(Context context, int resource, ArrayList<String> list) {
            super(context, resource, list);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = LayoutInflater.from(mActivity).inflate(R.layout.sticker_item, null);
            ImageView imageView = (ImageView) view.findViewById(R.id.image_view);
            String imagePath = ASSETS_PATH + getItem(position);
            Glide.with(mActivity).load(imagePath).centerCrop().into(imageView);
            return view;
        }


    }

    private void log(String msg) {
        Log.e("FragmentSticker", msg);
    }
}
