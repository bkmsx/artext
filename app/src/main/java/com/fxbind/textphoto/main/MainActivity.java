package com.fxbind.textphoto.main;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.fxbind.textphoto.R;
import com.fxbind.textphoto.export.ExportTask;
import com.fxbind.textphoto.fragment.TextFragment;
import com.fxbind.textphoto.helper.Utils;
import com.fxbind.textphoto.text.FloatText;
import com.fxbind.textphoto.text.FontManager;

import java.util.ArrayList;

/**
 * Created by Fx Bind on 11/22/2016.
 */
public class MainActivity extends AppCompatActivity {
    public TextFragment mTextFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTextFragment = TextFragment.newInstance(this);
        getSupportFragmentManager().beginTransaction().
                replace(R.id.layout_fragment, mTextFragment).commit();
    }

    private void log(String msg) {
        Log.e("Main activity", msg);
    }
}
