package com.fxbind.textphoto.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;

import com.fxbind.textphoto.main.Constants;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Fx Bind on 11/11/2016.
 */
public class Utils {
    public static void writeToFile(File fileTxt, String data) {
        try {
            FileWriter out = new FileWriter(fileTxt);
            out.write(data);
            out.close();
        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    public static SharedPreferences getSharedPref(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static String getDefaultName() {
        return new SimpleDateFormat("yy_MM_dd_HH_mm_ss", Locale.getDefault())
                .format(new Date(System.currentTimeMillis()));
    }

    public static Bitmap createDefaultBitmap(){
        Paint paint = new Paint();
        Bitmap bitmap = Bitmap.createBitmap(400, 400, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        paint.setColor(Color.BLACK);
        canvas.drawRect(0, 0, bitmap.getWidth(), bitmap.getHeight(), paint);
        return bitmap;
    }

    public static int getScreenWidth(){
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight(){
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }

    public static final int dpToPixel(Context context, int dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return (int) (dp*density);
    }

    public static ArrayList<String> listFilesFromAssets(Context context, String directory) {
        Resources res = context.getResources(); //if you are in an activity
        AssetManager am = res.getAssets();
        String fileList[] = new String[0];
        try {
            fileList = am.list(directory);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ArrayList<String> arrayList = new ArrayList<>();
        for (int i=0; i<fileList.length; i++){
            arrayList.add(directory+"/"+fileList[i]);
        }
        return arrayList;
    }

    public static void copyFileFromAssets(Context context, String input, String output){
        AssetManager assetManager = context.getAssets();
        InputStream in;
        OutputStream out;
        try {
            in = assetManager.open(input);
            out = new FileOutputStream(output);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            out.flush();
            out.close();
        } catch (Exception e) {
        }
    }

    public static String getOutputFolder(){
        String direct = Environment.getExternalStorageDirectory().toString();
        String outputFolder = direct+"/"+ Constants.OUTPUT_FOLDER;
        File file = new File(outputFolder);
        if (!file.exists()){
            file.mkdirs();
        }
        return outputFolder;
    }

    public static String getTempFolder(){
        String direct = Environment.getExternalStorageDirectory().toString();
        String tempFolder = direct+"/"+ Constants.OUTPUT_FOLDER+"/"+Constants.TEMP_FOLDER;
        File file = new File(tempFolder);
        if (!file.exists()){
            file.mkdirs();
        }
        return tempFolder;
    }

    public static String getFontFolder(){
        String direct = Environment.getExternalStorageDirectory().toString();
        String fontFolder = direct+"/"+ Constants.OUTPUT_FOLDER+"/"+Constants.FONT_FOLDER;
        File file = new File(fontFolder);
        if (!file.exists()){
            file.mkdirs();
        }
        return fontFolder;
    }

    public static String getUserFontFolder(){
        String direct = Environment.getExternalStorageDirectory().toString();
        String fontFolder = direct+"/"+ Constants.OUTPUT_FOLDER+"/"+Constants.USER_FONT_FOLDER;
        File file = new File(fontFolder);
        if (!file.exists()){
            file.mkdirs();
        }
        return fontFolder;
    }

    public static String getBackgroundFolder(){
        String direct = Environment.getExternalStorageDirectory().toString();
        String backgroundFolder = direct+"/"+ Constants.OUTPUT_FOLDER+"/"+Constants.BACKGROUND_FOLDER;
        File file = new File(backgroundFolder);
        if (!file.exists()){
            file.mkdirs();
        }
        return backgroundFolder;
    }

    public static String getInternalDirectory(){
        return Environment.getExternalStorageDirectory().toString();
    }
}
