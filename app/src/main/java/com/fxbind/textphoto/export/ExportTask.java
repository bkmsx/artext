package com.fxbind.textphoto.export;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.fxbind.textphoto.ffmpeg.FFmpeg;
import com.fxbind.textphoto.helper.Utils;

import java.util.LinkedList;

/**
 * Created by Bkmsx on 12/11/2016.
 */

public class ExportTask extends AsyncTask<Void, Void, Void>{
    ProgressDialog mProgressDialog;
    Context mContext;

    public ExportTask(Context context) {
        mContext = context;
    }
    private LinkedList<String> getCommand(){
        LinkedList<String> command = new LinkedList<>();
        String input = Utils.getInternalDirectory() + "/tien.png";
        String output = Utils.getInternalDirectory() + "/outien.png";
        String font = Utils.getInternalDirectory() + "/font.otf";
        String filter = "[v]drawtext=fontfile="+font+":text='bkmsx':fontsize=50:fontcolor=red";
        command.add("-i");
        command.add(input);
        command.add("-filter_complex");
        command.add(filter);
        command.add("-y");
        command.add(output);
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
        super.onPostExecute(aVoid);
    }

    @Override
    protected Void doInBackground(Void... voids) {
        FFmpeg.getInstance(mContext).executeFFmpegCommand(getCommand());
        return null;
    }
}
