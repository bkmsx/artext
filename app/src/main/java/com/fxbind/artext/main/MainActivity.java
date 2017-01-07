package com.fxbind.artext.main;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.fxbind.artext.R;
import com.fxbind.artext.export.ExportTask;
import com.fxbind.artext.fragment.FragmentGallery;
import com.fxbind.artext.fragment.FragmentImagesGallery;
import com.fxbind.artext.fragment.ReviewFragment;
import com.fxbind.artext.fragment.TextFragment;
import com.fxbind.artext.helper.Utils;

import java.io.File;
import java.util.ArrayList;


/**
 * Created by Fx Bind on 11/22/2016.
 */
public class MainActivity extends AppCompatActivity implements ExportTask.OnExportTaskListener {
    public TextFragment mTextFragment;
    public FragmentImagesGallery mImageFragment;

    public Menu mOptionMenu;
    public MenuItem mBtnDelete, mBtnShare, mBtnAddText,
                    mBtnAddSticker, mBtnMore;
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
                add(R.id.layout_fragment, mTextFragment).commit();

        mImageFragment = new FragmentImagesGallery();
        new CopyFontTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private class CopyFontTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            deleteOldFonts();
            copyFontsToStorage();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mTextFragment.loadAllFonts();
        }
    }

    private void deleteOldFonts() {
        File[] children = new File(Utils.getFontFolder()).listFiles();
        for (File child : children) {
            child.delete();
        }
    }

    private void copyFontsToStorage(){
        ArrayList<String> listFont = Utils.listFilesFromAssets(this, Constants.FONT_FOLDER);
        for (String font : listFont) {
            String fontName = font.replace("/","_");
            String destination = Utils.getFontFolder() + "/" + fontName;
            Utils.copyFileFromAssets(this, font, destination);
        }
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
            mTextFragment.setBtnAddFirstTime();
            setBtnDeleteTextVisible(false);
            setBtnShareVisible(false);
        }
        getSupportFragmentManager().beginTransaction().
                replace(R.id.layout_fragment, mTextFragment).commit();
        setBtnBackActionBarVisible(false);
        setBtnMoreVisible(true);
        if (mTextFragment.mImagePath != null) {
            setBtnAddStickerVisible(true);
            setBtnAddTextVisible(true);
        }
        setTitle(getString(R.string.app_name));
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
//        mBtnExport.setVisible(visible);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_menu, menu);
        mOptionMenu = menu;
        mBtnDelete = menu.findItem(R.id.menu_delete_text);
        mBtnShare = menu.findItem(R.id.menu_share);
        mBtnAddText = menu.findItem(R.id.menu_add_text);
        mBtnAddSticker = menu.findItem(R.id.menu_add_sticker);
        mBtnMore = menu.findItem(R.id.menu_more);
        setMainGroupMenuVisible(false);
        setBtnMoreVisible(true);
        return true;
    }

    public void setBtnMoreVisible(boolean visible) {
        if (mBtnMore != null) {
            mBtnMore.setVisible(visible);
        }
    }

    public void setBtnAddStickerVisible(boolean visible){
        if (mBtnAddSticker != null) {
            mBtnAddSticker.setVisible(visible);
        }
    }

    public void setBtnAddTextVisible(boolean visible){
        if (mBtnAddText != null) {
            mBtnAddText.setVisible(visible);
            log("hide add text");
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
            case R.id.menu_add_sticker:
                mTextFragment.toggleLayoutSticker();
                break;
            case R.id.menu_more:
                showPopupMenu();
                break;
            case android.R.id.home:
                onBackClick();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    private void showPopupMenu() {
        PopupMenu popup = new PopupMenu(MainActivity.this, findViewById(R.id.menu_more));

        popup.getMenuInflater()
                .inflate(R.menu.popup_menu, popup.getMenu());
        Menu menu = popup.getMenu();
        MenuItem menuSave = menu.findItem(R.id.menu_save);
        MenuItem menuRotate = menu.findItem(R.id.menu_rotate);
        boolean menuRotateVisible = mTextFragment.mImagePath != null;
        boolean menuSaveVisible = !(mTextFragment.mListText.isEmpty()
                                        && mTextFragment.mListSticker.isEmpty() && mTextFragment.rotate == 0);
        menuSave.setVisible(menuSaveVisible);
        menuRotate.setVisible(menuRotateVisible);
        popup.setOnMenuItemClickListener(onMenuItemClickListener);
        popup.show();
    }

    PopupMenu.OnMenuItemClickListener onMenuItemClickListener = new PopupMenu.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.menu_save:
                    mTextFragment.exportFile();
                    return true;
                case R.id.menu_rotate:
                    mTextFragment.rotateImage();
                    break;
                case R.id.menu_change_image:
                    openFileManager();
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
            }
            return true;
        }
    };

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
        onBackClick();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        deleteTempFolder();
    }

    private void deleteTempFolder() {
        File[] children = new File(Utils.getTempFolder()).listFiles();
        for (File child : children) {
            child.delete();
        }
    }
}
