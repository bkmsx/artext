package com.fxbind.textphoto.export;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.fxbind.textphoto.ffmpeg.FFmpeg;
import com.fxbind.textphoto.helper.Utils;
import com.fxbind.textphoto.text.FloatText;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Created by Bkmsx on 12/11/2016.
 */

public class ExportTask extends AsyncTask<Void, Void, Void>{
    ProgressDialog mProgressDialog;
    Context mContext;
    ArrayList<FloatText> mListText;
    String mImagePath;
    String mOutputPath;
    OnExportTaskListener mCallback;

    public ExportTask(Context context, ArrayList<FloatText> listText, String imagePath) {
        mContext = context;
        mListText = listText;
        mImagePath = imagePath;
        mCallback = (OnExportTaskListener) context;
    }
    private LinkedList<String> getCommand(){
        LinkedList<String> command = new LinkedList<>();
        mOutputPath = Utils.getOutputFolder() + "/" + Utils.getDefaultName() + ".png";

        String in = "[0:v]";
        String out = "[image]";
        String filter = TextFilter.getFilter(in, out, mListText);

        command.add("-i");
        command.add(mImagePath);
        command.add("-filter_complex");
        command.add(filter);
        command.add("-map");
        command.add(out);
        command.add("-y");
        command.add(mOutputPath);
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
