package com.trycath.myupdateapklibrary.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import java.io.File;

/**
 * 在此写用途
 *
 * @author: guoyoujin
 * @mail: guoyoujin123@gmail.com
 * @date: 2016-09-13 14:08
 * @version: V1.0 <描述当前版本功能>
 */

public class InstallApk {
    private static final String TAG = "InstallApk";
    public static void startInstall(Context context, File apkfile) {
        if (!apkfile.exists()) {
            Log.d(TAG,"startInstallexists");
            return;
        }
        Log.d(TAG,"startInstall");
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.setDataAndType(Uri.parse("file://" + apkfile.toString()), "application/vnd.android.package-archive");
        context.startActivity(i);
    }
    public static void startInstall(Context context, String filePath) {
        File apkfile = new File(filePath);
        if (!apkfile.exists()) {
            return;
        }
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setDataAndType(Uri.parse("file://" + apkfile.toString()),
                "application/vnd.android.package-archive");
        context.startActivity(i);
    }
}