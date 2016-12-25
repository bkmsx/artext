package com.fxbind.textphoto.fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import com.fxbind.textphoto.R;
import com.fxbind.textphoto.color.ColorPickerView;
import com.fxbind.textphoto.export.ExportTask;
import com.fxbind.textphoto.helper.Utils;
import com.fxbind.textphoto.main.MainActivity;
import com.fxbind.textphoto.text.EditTextDialog;
import com.fxbind.textphoto.text.FloatText;
import com.fxbind.textphoto.text.FontAdapter;

import java.util.ArrayList;

/**
 * Created by Bkmsx on 12/14/2016.
 */

public class TextFragment extends Fragment implements EditTextDialog.DialogClickListener
        , ColorPickerView.OnColorChangedListener, FloatText.OnFloatTextTouchListener{
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

    public FloatText mSelectedFloatText;
    public ColorPickerView mColorPicker;
    public FontAdapter mFontAdapter;

    public ArrayList<String> mListFont;
    public ArrayList<FloatText> mListText;

    public String mImagePath;

    private int mImageWidth, mImageHeight;
    private boolean mOpenLayoutColor;
    private boolean mChooseColor;
    public int mCountText;

    private static final String FONT_FOLDER = "fonts";

    public static TextFragment newInstance(MainActivity activity) {
        mActivity = activity;
        return new TextFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.text_fragment, null);

        mMainLayout = (RelativeLayout) view.findViewById(R.id.main_layout);

        mLayoutImage = (RelativeLayout) view.findViewById(R.id.layout_image);

        mImageView = (ImageView) view.findViewById(R.id.image_view);

        mBtnText = (Button) view.findViewById(R.id.btn_text);
        mBtnText.setOnClickListener(onBtnTextClick);

        mColorPicker = (ColorPickerView) view.findViewById(R.id.color_picker);
        mColorPicker.setAlphaSliderVisible(true);
        mColorPicker.setOnColorChangedListener(this);

        mEdtHex = (EditText) view.findViewById(R.id.text_hex);
        mEdtHex.setOnEditorActionListener(onEditColorActionListener);

        mLayoutColor = (LinearLayout) view.findViewById(R.id.layout_color);

        mLayoutEditText = (RelativeLayout) view.findViewById(R.id.layout_edit_text);

        mBtnFont = (Button) view.findViewById(R.id.btn_font);
        mBtnFont.setOnClickListener(onBtnFontClick);

        mBtnColor = (Button) view.findViewById(R.id.btn_color);
        mBtnColor.setOnClickListener(onBtnColorClick);

        mBtnBackground = (Button) view.findViewById(R.id.btn_background);
        mBtnBackground.setOnClickListener(onBtnBgrClick);

        mBtnOkColor = (Button) view.findViewById(R.id.btn_ok_color);
        mBtnOkColor.setOnClickListener(onBtnOkColorClick);

        mListViewFont = (ListView) view.findViewById(R.id.listview_font);
        mListViewFont.setOnItemClickListener(onFontClickListener);

        mLayoutFont = (LinearLayout) view.findViewById(R.id.layout_font);

        mBtnOkFont = (Button) view.findViewById(R.id.btn_ok_font);
        mBtnOkFont.setOnClickListener(onBtnOkFontClick);

        mBtnFavorite = (Button) view.findViewById(R.id.btn_favorite);
        mBtnFavorite.setOnClickListener(onBtnFavoriteClick);

        mBtnAddFirstTime = (ImageView) view.findViewById(R.id.btn_first_addtext);

        mListFont = new ArrayList<>();

        mListText = new ArrayList<>();

        setImagePath();
        if (mActivity.mFirstRun) {
            setBtnAddFirstTime();
            mImageView.setImageBitmap(null);
            mActivity.setBtnAddTextVisible(false);
        }

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mLayoutEditText.getLayoutParams();
        params.height = (int) (Utils.getScreenHeight() * 0.2f);

        setLayoutEditTextEnable(false);
        new CopyFontTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        return view;
    }

    @Override
    public void onTouch(float x, float y) {
        for (FloatText floatText : mListText) {
            if (x >= floatText.xMin && x <= floatText.xMax
                    && y >= floatText.yMin && y <= floatText.yMax) {
                setSelectedText(floatText);
                return;
            }
        }
    }

    @Override
    public void onSelected(FloatText floatText) {
        mSelectedFloatText = floatText;
    }

    private void setSelectedText(FloatText floatText) {
        mSelectedFloatText = floatText;
        updateLayoutEditText();
        mActivity.setBtnDeleteTextVisible(true);
        for (FloatText text : mListText) {
            if (text.equals(floatText)) {
                text.drawBorder(true);
            } else {
                text.drawBorder(false);
            }
        }
    }

    public void updateLayoutEditText(){
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

    private void setBtnAddFirstTime() {
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
        mFontAdapter.saveListFavorite();
        mFontAdapter.saveSeletedFont();
    }

    View.OnClickListener onBtnFavoriteClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (mFontAdapter.onlyFavorite) {
                mFontAdapter.showAllFont();
                mBtnFavorite.setText("Favorite");
            } else{
                mFontAdapter.showOnlyFavorite();
                mBtnFavorite.setText("All");
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

    private class CopyFontTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            copyFontsToStorage();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            createListFont();
        }
    }

    private void createListFont() {
        mFontAdapter = new FontAdapter(mActivity, android.R.layout.simple_list_item_1, mListFont);
        mListViewFont.setAdapter(mFontAdapter);
    }

    private void copyFontsToStorage(){
        ArrayList<String> listFont = Utils.listFilesFromAssets(mActivity, FONT_FOLDER);
        for (String font : listFont) {
            String fontName = font.replace("/","_");
            String destination = Utils.getFontFolder() + "/" + fontName;
            Utils.copyFileFromAssets(mActivity, font, destination);
            mListFont.add(destination);
        }
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
        }
    };

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
        }
    };

    private void openLayoutColor(boolean open) {
        int visible = open? View.VISIBLE : View.GONE;
        mLayoutColor.setVisibility(visible);
        mOpenLayoutColor = open;
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
        }
    };

    private void showSoftKeyboard() {
        InputMethodManager inputMethodManager =
                (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInputFromWindow(
                mBtnText.getApplicationWindowToken(),
                InputMethodManager.SHOW_FORCED, 0);
    }

    public void setImagePath(){
        if (mImagePath == null) {
            return;
        }
        Bitmap bitmap = BitmapFactory.decodeFile(mImagePath, null);
        mImageWidth = bitmap.getWidth();
        mImageHeight = bitmap.getHeight();
        mImageView.setImageBitmap(bitmap);
        setLayoutImage();
    }

    private void setLayoutImage(){
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mLayoutImage.getLayoutParams();
        float heightLimit = Utils.getScreenHeight() * 0.7f;
        float widthLimit = Utils.getScreenWidth() * 0.97f;
        boolean chooseHeight = heightLimit/(float)mImageHeight < widthLimit/(float)mImageWidth;
        if (chooseHeight) {
            params.height = (int)heightLimit;
            params.width = (int) (heightLimit*mImageWidth/mImageHeight);
        } else {
            params.width = (int) widthLimit;
            params.height = (int) (widthLimit*mImageHeight/mImageWidth);
        }

    }

    public void addText() {
        FloatText floatText = new FloatText(mActivity, "Text here");
        mLayoutImage.addView(floatText);
        mListText.add(floatText);
        mSelectedFloatText = floatText;
        setSelectedText(floatText);
        mCountText++;
        setLayoutEditTextEnable(true);
        updateLayoutEditText();
    }

    public void deleteText() {
        mLayoutImage.removeView(mSelectedFloatText);
        mListText.remove(mSelectedFloatText);
        mCountText--;
        if (mCountText == 0) {
            mActivity.setBtnExportVisible(false);
            setLayoutEditTextEnable(false);
        }
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

    View.OnKeyListener onEdtHexKeyListener = new View.OnKeyListener() {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_ENTER) {
                int color = convertToIntegerColor(mEdtHex.getText().toString());
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

    private int convertToIntegerColor (String hexColor) throws IllegalArgumentException{
        return Color.parseColor("#" + hexColor);
    }

    public void exportFile() {
        float layoutScale = getLayoutScale();
        for (FloatText text : mListText) {
            text.updateTextHolder(layoutScale);
        }
        new ExportTask(mActivity, mListText, mImagePath).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private float getLayoutScale(){
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
