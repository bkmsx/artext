package com.fxbind.textphoto.text;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.fxbind.textphoto.R;
import com.fxbind.textphoto.main.MainActivity;

/**
 * Created by Bkmsx on 12/15/2016.
 */

public class EditTextDialog extends DialogFragment {
    static MainActivity mActivity;
    public EditText mEditText;
    private DialogClickListener callback;

    static String mText;

    public static EditTextDialog newInstance(MainActivity activity, String text) {
        mActivity = activity;
        mText = text;
        return new EditTextDialog();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder buider = new AlertDialog.Builder(mActivity);
        buider.setTitle("Text");
        View view = LayoutInflater.from(mActivity).inflate(R.layout.edittext_dialog, null);
        mEditText = (EditText) view.findViewById(R.id.edt_text);
        mEditText.setText(mText);
        buider.setView(view);

        buider.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                hideSoftKeyboard();
                callback.onBtnOkClick(mEditText.getText().toString());
            }
        });

        buider.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                hideSoftKeyboard();
            }
        });

        Dialog dialog = buider.create();
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }

    private void hideSoftKeyboard(){
        InputMethodManager inputMethodManager =
                (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInputFromWindow(
                mEditText.getApplicationWindowToken(),
                InputMethodManager.SHOW_FORCED, 0);
    }

    public void setOnDialogClickListener(DialogClickListener listener) {
        callback = listener;
    }

    public interface DialogClickListener {
        void onBtnOkClick(String text);
    }
}
