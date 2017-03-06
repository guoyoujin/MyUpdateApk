package com.trycath.myupdateapklibrary.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;
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
        if(Build.VERSION.SDK_INT>=24) { //判读版本是否在7.0以上
            //参数1 上下文, 参数2 Provider主机地址 和配置文件中保持一致   参数3  共享的文件
            Uri apkUri =
                    FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider.download", apkfile);
            //添加这一句表示对目标应用临时授权该Uri所代表的文件
            i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            i.setDataAndType(apkUri, "application/vnd.android.package-archive");
        }else{
            i.setDataAndType(Uri.fromFile(apkfile),
                    "application/vnd.android.package-archive");
        }
        context.startActivity(i);
    }
    public static void startInstall(Context context, String filePath) {
        File apkfile = new File(filePath);
        if (!apkfile.exists()) {
            return;
        }
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if(Build.VERSION.SDK_INT>=24) { //判读版本是否在7.0以上
            //参数1 上下文, 参数2 Provider主机地址 和配置文件中保持一致   参数3  共享的文件
            Uri apkUri =
                    FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider.download", apkfile);
            //添加这一句表示对目标应用临时授权该Uri所代表的文件
            i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            i.setDataAndType(apkUri, "application/vnd.android.package-archive");
        }else{
            i.setDataAndType(Uri.fromFile(apkfile),
                    "application/vnd.android.package-archive");
        }
        context.startActivity(i);
    }
}