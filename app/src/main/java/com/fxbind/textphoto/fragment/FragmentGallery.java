package com.fxbind.textphoto.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.fxbind.textphoto.R;
import com.fxbind.textphoto.helper.Utils;
import com.fxbind.textphoto.main.MainActivity;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by bkmsx on 12/24/2016.
 */

public class FragmentGallery extends Fragment {
    static MainActivity mActivity;
    GridView mGridView;
    ArrayList<String> mListImage;

    public String[] pattern = {".png", "jpg"};

    public static FragmentGallery newInstance(MainActivity activity) {
        mActivity = activity;
        return new FragmentGallery();
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_image_gallery, null);
        mGridView = (GridView) view.findViewById(R.id.grid_view);
        mListImage = loadAllImage(Utils.getOutputFolder());
        sortPictureByDate();
        GalleryAdapter adapter = new GalleryAdapter(mActivity, R.layout.image_gallery_layout, mListImage);
        mGridView.setAdapter(adapter);
        mGridView.setOnItemClickListener(onItemClickListener);
        return view;
    }

    private void sortPictureByDate(){
        ArrayList<Long> modifiedDate = new ArrayList<>();
        for (String picture : mListImage) {
            long date = new File(picture).lastModified();
            modifiedDate.add(date);
        }

        for (int i=0; i<mListImage.size()-1; i++) {
            for (int j=i+1; j<mListImage.size(); j++){
                if (modifiedDate.get(j) > modifiedDate.get(i)) {
                    long date = modifiedDate.get(i);
                    modifiedDate.set(i, modifiedDate.get(j));
                    modifiedDate.set(j, date);

                    String picture = mListImage.get(i);
                    mListImage.set(i, mListImage.get(j));
                    mListImage.set(j, picture);
                }
            }
        }
    }


    AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String path = mListImage.get(position);
            mActivity.openFragmentReview(path);
            mActivity.setTitle(getName(path));
        }
    };

    public String getName(String path) {
        String name = new File(path).getName();
        return name.substring(0, name.length()-4);
    }

    public class GalleryAdapter extends ArrayAdapter<String> {
        public GalleryAdapter(Context context, int resource, ArrayList<String> objects) {
            super(context, resource, objects);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.image_gallery_layout, null);
            ImageView imageView = (ImageView) view.findViewById(R.id.image_view);
            TextView textView = (TextView) view.findViewById(R.id.text_view);
            String path = getItem(position);
            Glide.with(getContext()).load(path).centerCrop().into(imageView);
            textView.setText(getName(path));
            return view;
        }
    }

    private ArrayList<String> loadAllImage(String directory) {
        File fileDirectory = new File(directory);
        ArrayList<String> listImage = new ArrayList<>();
        File[] fileList = fileDirectory.listFiles();
        for (int i = 0; i < fileList.length; i++) {
           {
                if (matchFile(fileList[i])) {
                    listImage.add(fileList[i].getAbsolutePath());
                }
            }
        }
        return listImage;
    }

    private boolean matchFile(File file) {
        for (int i = 0; i < pattern.length; i++) {
            if (file.getName().endsWith(pattern[i])) {
                return true;
            }
        }
        return false;
    }
}
