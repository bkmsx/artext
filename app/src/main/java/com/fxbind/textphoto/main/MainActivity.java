package com.fxbind.textphoto.main;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.fxbind.textphoto.R;
import com.fxbind.textphoto.export.ExportTask;
import com.fxbind.textphoto.fragment.FragmentGallery;
import com.fxbind.textphoto.fragment.FragmentImagesGallery;
import com.fxbind.textphoto.fragment.ReviewFragment;
import com.fxbind.textphoto.fragment.TextFragment;

import java.io.File;


/**
 * Created by Fx Bind on 11/22/2016.
 */
public class MainActivity extends AppCompatActivity implements ExportTask.OnExportTaskListener {
    public TextFragment mTextFragment;
    public FragmentImagesGallery mImageFragment;

    public Menu mOptionMenu;
    public MenuItem mBtnDelete, mBtnShare,
                    mBtnExport, mBtnAddText,
                    mBtnAddSticker;
    public boolean mFirstRun;
    public boolean mOpenSubFolder;
    public boolean mOpenReview;
    public boolean mOpenGallery;
    public boolean mOpenFileManager;

    public String mOutputPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);
        mTextFragment = TextFragment.newInstance(this);
        getSupportFragmentManager().beginTransaction().
                replace(R.id.layout_fragment, mTextFragment).commit();
        mImageFragment = new FragmentImagesGallery();
        mFirstRun = true;
    }

    public void setBtnBackActionBarVisible(boolean visible) {
        getSupportActionBar().setDisplayHomeAsUpEnabled(visible);
    }

    public void openFileManager() {
        getSupportFragmentManager().beginTransaction().
                replace(R.id.layout_fragment, mImageFragment).commit();
        setMainGroupMenuVisible(false);
        setBtnBackActionBarVisible(true);
        setTitle(getString(R.string.image_gallery_title));
        mOpenFileManager = true;
    }

    public void backToTextFragment() {
        if (mOpenReview) {
            mFirstRun = true;
        }
        getSupportFragmentManager().beginTransaction().
                replace(R.id.layout_fragment, mTextFragment).commit();
        setMainGroupMenuVisible(true);
        setBtnDeleteTextVisible(false);
        setBtnBackActionBarVisible(false);
        setBtnShareVisible(false);
        setBtnExportVisible(false);
        setTitle(getString(R.string.app_name));
        mTextFragment.mListText.clear();
        mTextFragment.mCountText = 0;
        mOpenSubFolder = false;
        mOpenReview = false;
        mOpenGallery = false;
        mOpenFileManager = false;
    }

    public void setBtnShareVisible(boolean visible) {
        mBtnShare.setVisible(visible);
    }

    @Override
    public void onExportCompleted(String outputPath) {
        openFragmentReview(outputPath);
        setTitle(getString(R.string.review_title));
    }

    public void openFragmentReview(String outputPath) {
        getSupportFragmentManager().beginTransaction().
                replace(R.id.layout_fragment, ReviewFragment.newInstance(this, outputPath)).commit();
        setMainGroupMenuVisible(false);
        setBtnBackActionBarVisible(true);
        setBtnDeleteTextVisible(true);
        setBtnShareVisible(true);
        mOutputPath = outputPath;
        mOpenReview = true;
    }

    public void deleteFile() {
        new File(mOutputPath).delete();
        if (mOpenGallery) {
            backToGallery();
            return;
        }
        backToTextFragment();
        Toast.makeText(this, R.string.notify_delete_file, Toast.LENGTH_LONG).show();
    }

    public void shareFile() {
        MediaScannerConnection.scanFile(this, new String[]{mOutputPath}, null,
                onScanCompletedListener);
    }

    public void shareImage(Uri uri) {
        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.setType("image/*");
        sendIntent.putExtra(Intent.EXTRA_STREAM, uri);
        sendIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(sendIntent);
    }

    MediaScannerConnection.OnScanCompletedListener onScanCompletedListener = new MediaScannerConnection.OnScanCompletedListener() {
        @Override
        public void onScanCompleted(String path, Uri uri) {
            shareImage(uri);
        }
    };

    private void log(String msg) {
        Log.e("Main activity", msg);
    }

    public void setMainGroupMenuVisible(boolean visible) {
        mOptionMenu.setGroupVisible(R.id.main_group, visible);
    }

    public void setBtnDeleteTextVisible(boolean visible) {
        mBtnDelete.setVisible(visible);
    }

    public void setBtnExportVisible(boolean visible) {
        mBtnExport.setVisible(visible);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_menu, menu);
        mOptionMenu = menu;
        mBtnDelete = menu.findItem(R.id.menu_delete_text);
        mBtnShare = menu.findItem(R.id.menu_share);
        mBtnExport = menu.findItem(R.id.menu_export);
        mBtnAddText = menu.findItem(R.id.menu_add_text);
        mBtnAddSticker = menu.findItem(R.id.menu_add_sticker);
        setBtnDeleteTextVisible(false);
        setBtnShareVisible(false);
        setBtnExportVisible(false);
        setBtnAddTextVisible(false);
        setBtnAddStickerVisible(false);
        return true;
    }

    public void setBtnAddStickerVisible(boolean visible){
        if (mBtnAddSticker != null) {
            mBtnAddSticker.setVisible(visible);
        }
    }

    public void setBtnAddTextVisible(boolean visible){
        if (mBtnAddText != null) {
            mBtnAddText.setVisible(visible);
        }
    }

    private void openGallery() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.layout_fragment, FragmentGallery.newInstance(this)).commit();
        mOpenGallery = true;
        setTitle(getString(R.string.gallery_title));
        setMainGroupMenuVisible(false);
        setBtnBackActionBarVisible(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_add_text:
                mTextFragment.addText();
                setBtnDeleteTextVisible(true);
                setBtnExportVisible(true);
                return true;
            case R.id.menu_export:
                mTextFragment.exportFile();
                return true;
            case R.id.menu_change_image:
                openFileManager();
                break;
            case R.id.menu_delete_text:
                if (mOpenReview) {
                    deleteFile();
                    return true;
                }
                mTextFragment.onBtnDeleteClick();
                setBtnDeleteTextVisible(false);
                break;
            case R.id.menu_share:
                shareFile();
                break;
            case R.id.menu_rotate:
                mTextFragment.rotateImage();
                break;
            case R.id.menu_gallery:
                openGallery();
                break;
            case R.id.menu_contact_us:
                contactUs();
                break;
            case R.id.menu_rate:
                rate5Stars();
                break;
            case R.id.menu_add_sticker:
                mTextFragment.toggleLayoutSticker();
                break;
            case android.R.id.home:
                onBackClick();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void rate5Stars(){
        Uri uri = Uri.parse("market://details?id=" + getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        startActivity(goToMarket);
    }

    private void contactUs() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc822");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"fxbind@gmail.com"});
        intent.putExtra(Intent.EXTRA_SUBJECT, "Suggestion");
        intent.putExtra(Intent.EXTRA_TEXT, "Hello, FxBind!");
        startActivity(intent);
    }

    public void backToGallery() {
        openGallery();
        mOpenReview = false;
    }

    public boolean onBackClick() {
        if (mOpenSubFolder) {
            mImageFragment.backToMain();
            mOpenSubFolder = false;
            return true;
        }

        if (mOpenReview && mOpenGallery) {
            backToGallery();
            return true;
        }

        if (mOpenReview || mOpenFileManager|| mOpenGallery) {
            backToTextFragment();
            return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        if (onBackClick()) {
            return;
        }
    }
}
