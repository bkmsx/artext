package com.fxbind.artext.ffmpeg;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.fxbind.artext.helper.Utils;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.LinkedList;

/**
 * Created by Fx Bind on 11/22/2016.
 */

public class FFmpeg {
    private static final String CPU_X86 = "x86";
    private static final String CPU_ARMEABI_V7A = "armeabi-v7a";
    private static final String CPU_ARMEABI_V7A_NEON = "armeabi-v7a-neon";
    private static final String FFMPEG = "ffmpeg";
    public static String mFfmpegPath;
    public static String mAllLog;
    public static String mLineLog;
    public static FFmpeg sFFmpeg;

    public static FFmpeg getInstance(Context context){
        if (sFFmpeg == null){
            sFFmpeg = new FFmpeg();
            initFFMPEG(context);
        }
        return sFFmpeg;
    }

    public boolean performGetVideoInfo(String inputVideo, String outputFile){
        Log.e("Az Plugin: ", "Get to Az plugin");
        LinkedList<String> command = new LinkedList<String>();

        command.add(mFfmpegPath);
        command.add("-i");
        command.add(inputVideo);

        boolean value = executeFFmpegCommand(command);
        Utils.writeToFile(new File(outputFile), mAllLog);
        return value;
    }

    public String getLineLog() {
        return mLineLog;
    }

    public boolean executeFFmpegCommand(LinkedList<String> command) {
        command.add(0, mFfmpegPath);
        Process ffmpegProcess = null;
        ProcessBuilder procBuilder = new ProcessBuilder(command);
        mAllLog="";
        try {
            ffmpegProcess = procBuilder.redirectErrorStream(true).start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    ffmpegProcess.getInputStream()));
            System.out
                    .println("***Starting FFMPEG***" + procBuilder.toString());
            while ((mLineLog = reader.readLine()) != null) {
                System.out.println("***" + mLineLog + "***");
                mAllLog += mLineLog;
            }
            System.out.println("***Ending FFMPEG***");

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        if (ffmpegProcess != null) {
            ffmpegProcess.destroy();
        }
        return true;
    }

    public static boolean initFFMPEG(Context ctx) {
        System.out.println("cpu info: " + getCpuInfo());
        try {
            InputStream ffmpegInputStream = ctx.getAssets().open(
                    getCpuInfo() + "/" + FFMPEG);

            mFfmpegPath = ctx.getApplicationInfo().dataDir + "/" + FFMPEG;

            File destinationFile = new File(mFfmpegPath);

            OutputStream destinationOS = new BufferedOutputStream(
                    new FileOutputStream(destinationFile));
            int numRead;
            byte[] buf = new byte[1024];
            while ((numRead = ffmpegInputStream.read(buf)) >= 0) {
                destinationOS.write(buf, 0, numRead);
            }

            destinationOS.flush();
            destinationOS.close();

            try {
                String[] args = { "/system/bin/chmod", "755", mFfmpegPath };
                Process process = new ProcessBuilder(args).start();
                try {
                    process.waitFor();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                process.destroy();

            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @SuppressWarnings("deprecation")
    public static String getCpuInfo() {
        if (Build.CPU_ABI.equals(CPU_X86)
                || Build.CPU_ABI.equals(CPU_ARMEABI_V7A)
                || Build.CPU_ABI.equals(CPU_ARMEABI_V7A_NEON)) {
            return Build.CPU_ABI;
        } else {
            return CPU_ARMEABI_V7A;
        }
    }
}
