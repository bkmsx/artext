package com.fxbind.artext.fragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import com.fxbind.artext.main.MainActivity;
import com.fxbind.artext.sticker.FragmentSticker;

/**
 * Created by bkmsx on 1/2/2017.
 */
 class StickerPagerAdapter extends FragmentStatePagerAdapter {
    private MainActivity mActivity;

    StickerPagerAdapter(MainActivity activity, FragmentManager fm) {
        super(fm);
        mActivity = activity;
    }

    @Override
    public Fragment getItem(int position) {
//        Log.e("StickerPagerAdapter", "getItem: " + position );
        return FragmentSticker.newInstance(mActivity, position);
    }


    @Override
    public int getCount() {
        return 3;
    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
    }
}
