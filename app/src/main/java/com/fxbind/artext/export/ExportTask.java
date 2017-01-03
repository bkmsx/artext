package com.fxbind.artext.export;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.fxbind.artext.ffmpeg.FFmpeg;
import com.fxbind.artext.helper.Utils;
import com.fxbind.artext.sticker.FloatSticker;
import com.fxbind.artext.text.FloatText;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Created by Bkmsx on 12/11/2016.
 */

public class ExportTask extends AsyncTask<Void, Void, Void>{
    ProgressDialog mProgressDialog;
    Context mContext;
    ArrayList<FloatText> mListText;
    ArrayList<FloatSticker> mListSticker;
    String mImagePath;
    String mOutputPath;
    OnExportTaskListener mCallback;

    int mRotate;

    public ExportTask(Context context, ArrayList<FloatText> listText,
                      ArrayList<FloatSticker> listSticker, String imagePath, int rotate) {
        mContext = context;
        mListText = listText;
        mImagePath = imagePath;
        mListSticker = listSticker;
        mRotate = rotate;
        mCallback = (OnExportTaskListener) context;
    }
    private LinkedList<String> getCommand(){
        LinkedList<String> command = new LinkedList<>();
        mOutputPath = Utils.getOutputFolder() + "/" + Utils.getDefaultName() + ".png";

        String filter = "";
        String in = "[0:v]";
        String out = "[image]";
        if (mRotate != 0) {
            out = "[image];";
            if (mRotate == 90) {
                filter += in + "transpose=1" + out;
            } else if (mRotate == 180) {
                filter += in + "transpose=1, transpose=1" + out;
            } else {
                filter += in + "transpose=2" + out;
            }
            in = "[image]";
        }
        if (mListSticker.size() > 0) {
            out = mListText.size() > 0 ? "[sticker];" : "[sticker]";
            filter += ImageFilter.getFilter(in, out, mListSticker, 1);
            in = "[sticker]";
        }
        if (mListText.size() > 0) {
            out = "[text]";
            filter += TextFilter.getFilter(in, out, mListText);
        }
        command.add("-i");
        command.add(mImagePath);
        for (FloatSticker sticker : mListSticker) {
            command.add("-i");
            command.add(sticker.imagePath);
        }
        command.add("-filter_complex");
        command.add(filter);
        command.add("-map");
        command.add(out);
        command.add("-y");
        command.add(mOutputPath);

        Log.e("Export", "getCommand: " + command );
        return command;
    }

    @Override
    protected void onPreExecute() {
        mProgressDialog = new ProgressDialog(mContext);
        mProgressDialog.setMessage("Wait for export..");
        mProgressDialog.show();
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        mProgressDialog.dismiss();
        mCallback.onExportCompleted(mOutputPath);
        super.onPostExecute(aVoid);
    }

    @Override
    protected Void doInBackground(Void... voids) {
        FFmpeg.getInstance(mContext).executeFFmpegCommand(getCommand());
        return null;
    }

    public interface OnExportTaskListener {
        void onExportCompleted(String outputPath);
    }
}
