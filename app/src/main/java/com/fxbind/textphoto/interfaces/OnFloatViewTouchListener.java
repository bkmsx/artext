package com.fxbind.textphoto.interfaces;

import android.view.View;

import com.fxbind.textphoto.text.FloatText;

import java.util.Objects;

/**
 * Created by bkmsx on 12/28/2016.
 */

public interface OnFloatViewTouchListener {
    void onTouch(float x, float y);
    void onSelected(View view);
}
