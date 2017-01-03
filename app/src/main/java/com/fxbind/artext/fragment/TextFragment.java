package com.fxbind.artext.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fxbind.artext.R;
import com.fxbind.artext.color.ColorPickerView;
import com.fxbind.artext.export.ExportTask;
import com.fxbind.artext.helper.Utils;
import com.fxbind.artext.interfaces.OnFloatViewTouchListener;
import com.fxbind.artext.interfaces.OnStickerClickListener;
import com.fxbind.artext.main.MainActivity;
import com.fxbind.artext.sticker.FloatSticker;
import com.fxbind.artext.text.EditTextDialog;
import com.fxbind.artext.text.FloatText;
import com.fxbind.artext.text.FontAdapter;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Bkmsx on 12/14/2016.
 */

public class TextFragment extends Fragment implements EditTextDialog.DialogClickListener
        , ColorPickerView.OnColorChangedListener, OnFloatViewTouchListener, OnStickerClickListener {
    static private MainActivity mActivity;

    private RelativeLayout mMainLayout;
    private RelativeLayout mLayoutImage;
    private ImageView mImageView;
    private Button mBtnText, mBtnFont;
    private EditText mEdtHex;
    private LinearLayout mLayoutColor, mLayoutFont;
    private Button mBtnColor, mBtnBackground;
    private Button mBtnOkColor, mBtnOkFont, mBtnFavorite;
    private ListView mListViewFont;
    private RelativeLayout mLayoutEditText;
    private ImageView mBtnAddFirstTime;
    private ViewPager mViewPager;
    private LinearLayout mTabLayout1, mTabLayout2, mTabLayout3;
    private LinearLayout mLayoutSticker, mTabLayoutClose;
    private RelativeLayout mLayoutFloatView;
    private View mView;

    public FloatText mSelectedFloatText;
    public ColorPickerView mColorPicker;
    public FontAdapter mFontAdapter;
    public FloatSticker mSelectedSticker;

    public ArrayList<String> mListFont;
    public ArrayList<FloatText> mListText;
    public ArrayList<FloatSticker> mListSticker;

    public String mImagePath;

    private int mImageWidth, mImageHeight;
    private boolean mChooseColor;
    public int mCountText;
    private boolean mChooseText;
    private boolean mOpenLayoutSticker;
    private int rotate = 0;

    public static TextFragment newInstance(MainActivity activity) {
        mActivity = activity;
        return new TextFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        if (mView != null) {
            return mView;
        }
        mView = inflater.inflate(R.layout.text_fragment, null);

        mMainLayout = (RelativeLayout) mView.findViewById(R.id.main_layout);

        mLayoutImage = (RelativeLayout) mView.findViewById(R.id.layout_image);

        mImageView = (ImageView) mView.findViewById(R.id.image_view);

        mBtnText = (Button) mView.findViewById(R.id.btn_text);
        mBtnText.setOnClickListener(onBtnTextClick);

        mColorPicker = (ColorPickerView) mView.findViewById(R.id.color_picker);
        mColorPicker.setAlphaSliderVisible(true);
        mColorPicker.setOnColorChangedListener(this);

        mEdtHex = (EditText) mView.findViewById(R.id.text_hex);
        mEdtHex.setOnEditorActionListener(onEditColorActionListener);

        mLayoutColor = (LinearLayout) mView.findViewById(R.id.layout_color);

        mLayoutEditText = (RelativeLayout) mView.findViewById(R.id.layout_edit_text);

        mBtnFont = (Button) mView.findViewById(R.id.btn_font);
        mBtnFont.setOnClickListener(onBtnFontClick);

        mBtnColor = (Button) mView.findViewById(R.id.btn_color);
        mBtnColor.setOnClickListener(onBtnColorClick);

        mBtnBackground = (Button) mView.findViewById(R.id.btn_background);
        mBtnBackground.setOnClickListener(onBtnBgrClick);

        mBtnOkColor = (Button) mView.findViewById(R.id.btn_ok_color);
        mBtnOkColor.setOnClickListener(onBtnOkColorClick);

        mListViewFont = (ListView) mView.findViewById(R.id.listview_font);
        mListViewFont.setOnItemClickListener(onFontClickListener);

        mLayoutFont = (LinearLayout) mView.findViewById(R.id.layout_font);

        mBtnOkFont = (Button) mView.findViewById(R.id.btn_ok_font);
        mBtnOkFont.setOnClickListener(onBtnOkFontClick);

        mBtnFavorite = (Button) mView.findViewById(R.id.btn_favorite);
        mBtnFavorite.setOnClickListener(onBtnFavoriteClick);

        mBtnAddFirstTime = (ImageView) mView.findViewById(R.id.btn_first_addtext);

        mViewPager = (ViewPager) mView.findViewById(R.id.view_pager);
        mViewPager.addOnPageChangeListener(onViewPagerChanged);

        mTabLayout1 = (LinearLayout) mView.findViewById(R.id.sticker_tab_layout_1);
        mTabLayout2 = (LinearLayout) mView.findViewById(R.id.sticker_tab_layout_2);
        mTabLayout3 = (LinearLayout) mView.findViewById(R.id.sticker_tab_layout_3);
        mTabLayoutClose = (LinearLayout) mView.findViewById(R.id.tab_layout_close);
        mTabLayout1.setOnClickListener(onTabClickListener);
        mTabLayout2.setOnClickListener(onTabClickListener);
        mTabLayout3.setOnClickListener(onTabClickListener);
        mTabLayoutClose.setOnClickListener(onTabClickListener);

        mLayoutSticker = (LinearLayout) mView.findViewById(R.id.layout_sticker);
        mLayoutFloatView = (RelativeLayout) mView.findViewById(R.id.layout_floatview);

        mListFont = new ArrayList<>();
        mListText = new ArrayList<>();
        mListSticker = new ArrayList<>();

        setBtnAddFirstTime();

        mActivity.setBtnAddTextVisible(false);
        mActivity.setBtnAddStickerVisible(false);
        rotate = 0;

        mOpenLayoutSticker = false;
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mLayoutEditText.getLayoutParams();
        params.height = (int) (Utils.getScreenHeight() * 0.2f);

        setLayoutEditTextEnable(false);
        new LoadFontTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        return mView;
    }

    public void rotateImage() {
        new RotationTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private class RotationTask extends AsyncTask<Void, Void, Void> {
        Bitmap rotateBitmap;
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(mActivity);
            progressDialog.setMessage("Wait..");
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            rotate += 90;
            if (rotate == 360) {
                rotate = 0;
            }
            Matrix matrix = new Matrix();
            matrix.postRotate(rotate);
            Bitmap bitmap = BitmapFactory.decodeFile(mImagePath, null);
            rotateBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                    bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            mImageWidth = rotateBitmap.getWidth();
            mImageHeight = rotateBitmap.getHeight();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressDialog.dismiss();
            mImageView.setImageBitmap(rotateBitmap);
            setLayoutImage();
            resetAllFloatView();
        }
    }

    public void resetAllFloatView() {
        mListSticker.clear();
        mListText.clear();
        mCountText = 0;
        mLayoutFloatView.removeAllViews();
    }

    View.OnClickListener onTabClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.equals(mTabLayout1)) {
                highlightSelectedTab(0);
                mViewPager.setCurrentItem(0);
            } else if (v.equals(mTabLayout2)) {
                highlightSelectedTab(1);
                mViewPager.setCurrentItem(1);
            } else if (v.equals(mTabLayout3)) {
                highlightSelectedTab(2);
                mViewPager.setCurrentItem(2);
            } else if (v.equals(mTabLayoutClose)) {
                closeLayoutSticker();
            }
        }
    };

    private void highlightSelectedTab(int position) {
        int tabColor = position == 0 ? ContextCompat.getColor(mActivity, R.color.tab_highline_color) : Color.TRANSPARENT;
        mTabLayout1.setBackgroundColor(tabColor);
        tabColor = position == 1 ? ContextCompat.getColor(mActivity, R.color.tab_highline_color) : Color.TRANSPARENT;
        mTabLayout2.setBackgroundColor(tabColor);
        tabColor = position == 2 ? ContextCompat.getColor(mActivity, R.color.tab_highline_color) : Color.TRANSPARENT;
        mTabLayout3.setBackgroundColor(tabColor);
    }

    public void toggleLayoutSticker() {
        if (mOpenLayoutSticker) {
            closeLayoutSticker();
        } else {
            openLayoutSticker();
        }
    }

    public void openLayoutSticker() {
        StickerPagerAdapter stickerPagerAdapter = new StickerPagerAdapter(mActivity, mActivity.getSupportFragmentManager());
        mViewPager.setAdapter(stickerPagerAdapter);

        TranslateAnimation animation = new TranslateAnimation(0, 0, mLayoutSticker.getHeight(), 0);
        animation.setDuration(500);
        mLayoutSticker.setVisibility(View.VISIBLE);
        mLayoutSticker.startAnimation(animation);
        highlightSelectedTab(0);
        mOpenLayoutSticker = true;
    }

    public void closeLayoutSticker() {
        TranslateAnimation animation = new TranslateAnimation(0, 0, 0, mLayoutSticker.getHeight());
        animation.setDuration(500);
        mLayoutSticker.setVisibility(View.GONE);
        mLayoutSticker.startAnimation(animation);
        mOpenLayoutSticker = false;
    }

    ViewPager.OnPageChangeListener onViewPagerChanged = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            highlightSelectedTab(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    @Override
    public void onStickerClick(String path) {
        addSticker(path);
    }

    @Override
    public void onTouch(float x, float y) {
        mActivity.setBtnDeleteTextVisible(false);

        for (FloatText floatText : mListText) {
            if (x >= floatText.xMin && x <= floatText.xMax
                    && y >= floatText.yMin && y <= floatText.yMax) {
                setSelectedText(floatText);
                mChooseText = true;
            } else {
                floatText.drawBorder(false);
            }
        }

        for (FloatSticker floatSticker : mListSticker) {
            if (x >= floatSticker.xMin && x <= floatSticker.xMax
                    && y >= floatSticker.yMin && y <= floatSticker.yMax) {
                floatSticker.drawBorder(true);
                mSelectedSticker = floatSticker;
                mActivity.setBtnDeleteTextVisible(true);
                mChooseText = false;
            } else {
                floatSticker.drawBorder(false);
            }
        }
    }

    @Override
    public void onSelected(View view) {
        if (view instanceof FloatText) {
            mSelectedFloatText = (FloatText) view;
            mChooseText = true;
        } else {
            mSelectedSticker = (FloatSticker) view;
            mChooseText = false;
        }

    }

    private void setSelectedText(FloatText floatText) {
        mSelectedFloatText = floatText;
        updateLayoutEditText();
        mActivity.setBtnDeleteTextVisible(true);
        floatText.drawBorder(true);
    }

    public void updateLayoutEditText() {
        mFontAdapter.setSelectedPosition(mSelectedFloatText.fontId);
        if (mChooseColor) {
            int color = mSelectedFloatText.mColor;
            mColorPicker.setColor(color);
            mEdtHex.setText(convertToHexColor(color, false));
        } else {
            int color = mSelectedFloatText.mBackgroundColor;
            mColorPicker.setColor(color);
            mEdtHex.setText(convertToHexColor(color, false));
        }
    }

    public void setLayoutEditTextEnable(boolean enable) {
        mBtnText.setEnabled(enable);
        mBtnColor.setEnabled(enable);
        mBtnBackground.setEnabled(enable);
        mBtnFont.setEnabled(enable);
    }

    public void setBtnAddFirstTime() {
        mImageView.setImageBitmap(null);
        mBtnAddFirstTime.setVisibility(View.VISIBLE);
        mBtnAddFirstTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.openFileManager();
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mFontAdapter == null) {
            return;
        }
        mFontAdapter.saveListFavorite();
        mFontAdapter.saveSeletedFont();
    }

    View.OnClickListener onBtnFavoriteClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (mFontAdapter.onlyFavorite) {
                mFontAdapter.showAllFont();
                mBtnFavorite.setText(mActivity.getString(R.string.favorite_button_text));
            } else {
                mFontAdapter.showOnlyFavorite();
                mBtnFavorite.setText(mActivity.getString(R.string.all_button_text));
            }
        }
    };

    public int[] getLayoutImagePosition() {
        int[] point = new int[2];
        mLayoutImage.getLocationOnScreen(point);
        return point;
    }

    AdapterView.OnItemClickListener onFontClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
            mFontAdapter.setSelectedPosition(position);
            mSelectedFloatText.setFont(mListFont.get(position), position);
        }
    };

    private void openLayoutFont(boolean open) {
        int visibility = open ? View.VISIBLE : View.GONE;
        mLayoutFont.setVisibility(visibility);
    }

    private class LoadFontTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            loadFontOf(Utils.getFontFolder());
            loadFontOf(Utils.getUserFontFolder());
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            createListFont();
        }
    }

    private void loadFontOf(String folderDirect) {
        String[] list = new File(folderDirect).list();
        for (String name : list) {
            String path = folderDirect + "/" + name;
            mListFont.add(path);
        }
    }

    private void createListFont() {
        mFontAdapter = new FontAdapter(mActivity, android.R.layout.simple_list_item_1, mListFont);
        mListViewFont.setAdapter(mFontAdapter);
    }


    View.OnClickListener onBtnOkColorClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            openLayoutColor(false);
        }
    };

    View.OnClickListener onBtnFontClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            openLayoutFont(true);
            mSelectedFloatText.drawBorder(true);
            mFontAdapter.setSelectedPosition(mSelectedFloatText.fontId);
        }
    };

    View.OnClickListener onBtnOkFontClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            openLayoutFont(false);
            unhighlightSticker();
            mSelectedFloatText.drawBorder(true);
            unhighlightSticker();
        }
    };

    View.OnClickListener onBtnColorClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            openLayoutColor(true);
            mChooseColor = true;
            int color = mSelectedFloatText.mColor;
            mColorPicker.setColor(color);
            mLayoutColor.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.color_layout));
            mEdtHex.setText(convertToHexColor(color, false));
            mSelectedFloatText.drawBorder(true);
            unhighlightSticker();
        }
    };

    private void unhighlightSticker() {
        if (mSelectedSticker != null) {
            mSelectedSticker.drawBorder(false);
        }
    }

    View.OnClickListener onBtnBgrClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            openLayoutColor(true);
            mChooseColor = false;
            int color = mSelectedFloatText.mBackgroundColor;
            mColorPicker.setColor(color);
            mLayoutColor.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.background_layout));
            mEdtHex.setText(convertToHexColor(color, false));
            mSelectedFloatText.drawBorder(true);
            unhighlightSticker();
        }
    };

    private void openLayoutColor(boolean open) {
        int visible = open ? View.VISIBLE : View.GONE;
        mLayoutColor.setVisibility(visible);
    }

    @Override
    public void onColorChanged(int color) {
        if (mChooseColor) {
            mSelectedFloatText.setTextColor(color);
        } else {
            mSelectedFloatText.setTextBgrColor(color);
        }
        mEdtHex.setText(convertToHexColor(color, false));
    }

    public String convertToHexColor(int color, boolean export) {
        String resultColor = "";
        String s = String.format("%08X", (0xFFFFFFFF & color));
        if (export) {
            resultColor += s.substring(2) + "@0x" + s.substring(0, 2);
        } else {
            resultColor += s;
        }
        return resultColor;
    }

    View.OnClickListener onBtnTextClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            EditTextDialog dialog = EditTextDialog.newInstance(mActivity, mSelectedFloatText.text);
            dialog.show(getFragmentManager().beginTransaction(), "edit text");
            dialog.setOnDialogClickListener(TextFragment.this);
            showSoftKeyboard();
            unhighlightSticker();
            mSelectedFloatText.drawBorder(true);
        }
    };

    private void showSoftKeyboard() {
        InputMethodManager inputMethodManager =
                (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInputFromWindow(
                mBtnText.getApplicationWindowToken(),
                InputMethodManager.SHOW_FORCED, 0);
    }

    public void setImagePath(String imagePath) {
        mImagePath = imagePath;
        Bitmap bitmap = BitmapFactory.decodeFile(mImagePath, null);
        mImageWidth = bitmap.getWidth();
        mImageHeight = bitmap.getHeight();
        mImageView.setImageBitmap(bitmap);
        mBtnAddFirstTime.setVisibility(View.GONE);
        setLayoutImage();
    }

    private void setLayoutImage() {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mLayoutImage.getLayoutParams();
        float heightLimit = Utils.getScreenHeight() * 0.7f;
        float widthLimit = Utils.getScreenWidth() * 0.97f;
        boolean chooseHeight = heightLimit / (float) mImageHeight < widthLimit / (float) mImageWidth;
        if (chooseHeight) {
            params.height = (int) heightLimit;
            params.width = (int) (heightLimit * mImageWidth / mImageHeight);
        } else {
            params.width = (int) widthLimit;
            params.height = (int) (widthLimit * mImageHeight / mImageWidth);
        }
    }

    public void addSticker(String imagePath) {
        FloatSticker floatSticker = new FloatSticker(mActivity, imagePath);
        mLayoutFloatView.addView(floatSticker);
        mListSticker.add(floatSticker);
        unhighlightFloatText();
        unhighlightSticker();
        mSelectedSticker = floatSticker;
        mChooseText = false;
        mActivity.setBtnDeleteTextVisible(true);
        mActivity.setBtnExportVisible(true);
    }

    public void addText() {
        FloatText floatText = new FloatText(mActivity, "Text here");
        mLayoutFloatView.addView(floatText);
        mListText.add(floatText);
        unhighlightFloatText();
        mSelectedFloatText = floatText;
        setSelectedText(floatText);
        mCountText++;
        setLayoutEditTextEnable(true);
        updateLayoutEditText();
        unhighlightSticker();
        mChooseText = true;
    }

    private void unhighlightFloatText() {
        if (mSelectedFloatText != null) {
            mSelectedFloatText.drawBorder(false);
        }
    }

    public void onBtnDeleteClick() {
        if (mChooseText) {
            deleteText();
        } else {
            deleteSticker();
        }
        if (mCountText == 0) {
            setLayoutEditTextEnable(false);
            if (mListSticker.size() == 0) {
                mActivity.setBtnExportVisible(false);
            }
        }
    }

    public void deleteSticker() {
        mLayoutFloatView.removeView(mSelectedSticker);
        mListSticker.remove(mSelectedSticker);
    }

    public void deleteText() {
        mLayoutFloatView.removeView(mSelectedFloatText);
        mListText.remove(mSelectedFloatText);
        mCountText--;
    }

    TextView.OnEditorActionListener onEditColorActionListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    actionId == EditorInfo.IME_ACTION_DONE ||
                    event.getAction() == KeyEvent.ACTION_DOWN &&
                            event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                String hexColor = mEdtHex.getText().toString();
                int color = mChooseColor ? mSelectedFloatText.mColor : mSelectedFloatText.mBackgroundColor;
                try {
                    color = convertToIntegerColor(hexColor);
                } catch (Exception e) {
                    mEdtHex.setText(convertToHexColor(color, false));
                    Toast.makeText(mActivity, "Color code must in hex format", Toast.LENGTH_LONG).show();
                    return false;
                }

                mColorPicker.setColor(color);
                mEdtHex.clearFocus();
                if (mChooseColor) {
                    mSelectedFloatText.setTextColor(color);
                } else {
                    mSelectedFloatText.setBackgroundColor(color);
                }
            }
            return false;
        }
    };

    private int convertToIntegerColor(String hexColor) throws IllegalArgumentException {
        return Color.parseColor("#" + hexColor);
    }

    public void exportFile() {
        float layoutScale = getLayoutScale();
        for (FloatText text : mListText) {
            text.updateTextHolder(layoutScale);
        }
        for (FloatSticker sticker : mListSticker) {
            sticker.updateImageHolder(layoutScale);
        }
        new ExportTask(mActivity, mListText, mListSticker, mImagePath, rotate)
                .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private float getLayoutScale() {
        return (float) mImageHeight / (float) mLayoutImage.getHeight();
    }

    private void log(String msg) {
        Log.e("Text fragment", msg);
    }

    @Override
    public void onBtnOkClick(String text) {
        mSelectedFloatText.setText(text);
    }


}
